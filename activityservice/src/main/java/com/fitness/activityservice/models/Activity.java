package com.fitness.activityservice.models;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "activities")
public class Activity {
    
    @Id
    private String id;
    private String userId;
    private ActivityType activityType;
    private Integer duration;
    private Double caloriesBurned;
    private LocalDateTime startTime;

    @Field("metrics")
    private Map<String,Object> additionalMetrics;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
