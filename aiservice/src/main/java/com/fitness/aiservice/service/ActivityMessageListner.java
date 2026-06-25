package com.fitness.aiservice.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.models.Activity;
import com.fitness.aiservice.models.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListner {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    private final ActivityAiService aiService;
    private final RecommendationRepository recommendationRepository;


    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity){
        log.info("Received activity from queue {}: {}", queueName, activity);
        // log.info("Generated Recommendation : {}", aiService.generateRecommendation(activity));
        // Here you can add logic to process the activity, e.g., save to database, call recommendation engine, etc.

        Recommendation recommendation;
        try {
            recommendation = aiService.generateRecommendation(activity);
        } catch (Exception ex) {
            log.error("Failed to generate AI recommendation for activity {}", activity.getId(), ex);
            recommendation = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getActivityType())
                    .recommendation("Unable to generate recommendation due to an AI service error.")
                    .improvements(java.util.List.of("Retry later when the AI service is available."))
                    .suggestions(java.util.List.of("Check Gemini API quota and rate limits."))
                    .safety(java.util.List.of("Follow general safety guidelines."))
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        try {
            recommendationRepository.save(recommendation);
            log.info("Saved recommendation for activity {}", activity.getId());
        } catch (Exception ex) {
            log.error("Failed to save recommendation for activity {}", activity.getId(), ex);
            throw new RuntimeException("Failed to save recommendation for activity " + activity.getId(), ex);
        }
    }
    
}

