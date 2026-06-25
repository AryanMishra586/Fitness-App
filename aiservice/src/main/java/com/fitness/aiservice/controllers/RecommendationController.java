package com.fitness.aiservice.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.aiservice.models.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import com.fitness.aiservice.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    private final RecommendationRepository recommendationRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendation(@PathVariable String userId) {
        System.out.println("Fetching recommendations for userId: " + userId);
        return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendation(@PathVariable String activityId) {
        // Implementation for getting activity-based recommendations
        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }

    @PostMapping("/testing")
    public void test(){
        Recommendation recommendation = Recommendation.builder()
        .activityId("1")
                    .userId("34")
                    .activityType("RUNNING")
                    .recommendation("Unable to generate recommendation due to an AI service error.")
                    .improvements(java.util.List.of("Retry later when the AI service is available."))
                    .suggestions(java.util.List.of("Check Gemini API quota and rate limits."))
                    .safety(java.util.List.of("Follow general safety guidelines."))
                    .createdAt(LocalDateTime.now())
                    .build();
                    recommendationRepository.save(recommendation);
    }
}
