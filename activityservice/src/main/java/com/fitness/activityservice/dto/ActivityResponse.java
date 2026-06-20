package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.LastModifiedDate;

import com.fitness.activityservice.models.ActivityType;

import lombok.Data;

@Data
public class ActivityResponse {
    
    private String id;
    private String userId;
    private ActivityType activityType;
    private Integer duration;
    private Double caloriesBurned;
    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
