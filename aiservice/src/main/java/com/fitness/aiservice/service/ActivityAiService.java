package com.fitness.aiservice.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.models.Activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public String generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response for activity {}: {}", activity.getId(), aiResponse);
        processAiResponse(activity, aiResponse);
        return aiResponse;
    }

    private void processAiResponse(Activity activity, String aiResponse) {

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
        }
        catch(Exception e){
            e.printStackTrace();
        }
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
