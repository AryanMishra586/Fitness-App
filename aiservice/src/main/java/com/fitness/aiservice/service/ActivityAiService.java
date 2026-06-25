package com.fitness.aiservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.models.Activity;
import com.fitness.aiservice.models.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response for activity {}: {}", activity.getId(), aiResponse);
        
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {

        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates").get(0)
            .path("content")
            .path("parts")
            .get(0)
            .path("text");

            String jsonContent = textNode.asText()
            .replaceAll("```json\\n", "")
            .replaceAll("\\n```", "").trim();

            log.info("Extracted JSON content for activity {}: {}", activity.getId(), jsonContent);


            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "OverAll:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safetyTips = extractSafetyTips(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safetyTips)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        catch(Exception e){
            e.printStackTrace();
            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendation("Unable to generate recommendation due to an error.")
                    .improvements(Collections.singletonList("No specific improvements suggested."))
                    .suggestions(Collections.singletonList("No specific suggestions provided."))
                    .safety(Collections.singletonList("Follow general safety guidelines."))
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    private List<String> extractSafetyTips(JsonNode safetyNode) {
        List<String> safetyTips = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(safetyTip->{
                safetyTips.add(safetyTip.asText());
            });
        }
        return safetyTips.isEmpty() ? Collections.singletonList("Follow general safety guidelines.") : safetyTips;
        

    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
       
        List<String> suggestions = new ArrayList<>();
        if(suggestionNode.isArray()){
            suggestionNode.forEach(suggestion->{
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions provided.") : suggestions;

    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement->{
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s", area, detail));
            });
        }
        return improvements.isEmpty() ? Collections.singletonList("No specific improvements suggested.") : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText()).append("\n\n");
        }

    }

    private String createPromptForActivity(Activity activity) {
        String activitySummary = String.format(
                "Activity ID: %s%nUser ID: %s%nActivity Type: %s%nDuration: %s minutes%nCalories Burned: %s%nStart Time: %s%nAdditional Metrics: %s%nCreated At: %s%nUpdated At: %s",
                safeValue(activity.getId()),
                safeValue(activity.getUserId()),
                safeValue(activity.getActivityType()),
                safeValue(activity.getDuration()),
                safeValue(activity.getCaloriesBurned()),
                safeValue(activity.getStartTime()),
                safeValue(activity.getAdditionalMetrics()),
                safeValue(activity.getCreatedAt()),
                safeValue(activity.getUpdatedAt()));

        return String.format("""
                You are a fitness coach and exercise analyst.

                Analyze the activity data below and generate personalized fitness recommendations.
                Focus on practical next steps, safety, and improvements based on the activity details.

                Activity data:
                %s

                Return ONLY valid JSON with this exact shape:
                {
                  "recommendation": "short overall recommendation",
                  "improvements": ["improvement 1", "improvement 2"],
                  "safety": ["safety tip 1", "safety tip 2"],
                  "suggestions": ["suggestion 1", "suggestion 2"]
                }

                Rules:
                - Use concise, actionable language.
                - Keep the response focused on the activity context.
                - Do not include markdown, explanations, code fences, or extra text.
                - If a field is unknown, infer cautiously from the available activity data.
                """, activitySummary);
    }

    private String safeValue(Object value) {
        return value == null ? "N/A" : value.toString();
    }
}
