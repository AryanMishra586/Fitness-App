package com.fitness.aiservice.service;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fitness.aiservice.models.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    
    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getUserRecommendation(String userId) {
        try {
            return recommendationRepository.findByUserId(userId);
        } catch (DataAccessException exception) {
            return Collections.emptyList();
        }
    }

    public Recommendation getActivityRecommendation(String activityId) {
        try {
            return recommendationRepository.findByActivityId(activityId)
                    .orElseThrow(() -> new RuntimeException("Recommendation not found for activityId: " + activityId));
        } catch (DataAccessException exception) {
            throw new RuntimeException("Unable to load recommendation for activityId: " + activityId, exception);
        }
    }

    
}
