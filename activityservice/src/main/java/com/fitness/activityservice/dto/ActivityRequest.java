package com.fitness.activityservice.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityservice.models.ActivityType;

import lombok.Data;
@Data
public class ActivityRequest {
    
    // private String id;
    private String userId;
    private ActivityType activityType;
    private Integer duration;
    private Double caloriesBurned;
    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
}
