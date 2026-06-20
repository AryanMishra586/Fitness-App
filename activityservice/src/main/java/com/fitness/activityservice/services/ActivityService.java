package com.fitness.activityservice.services;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.models.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

import lombok.AllArgsConstructor;

@Service
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    
    public ActivityResponse trackActivity(ActivityRequest request) {
        
        Activity activity = new Activity();
        // activity.setId(request.getId());
        activity.setUserId(request.getUserId());
        activity.setActivityType(request.getActivityType());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurned(request.getCaloriesBurned());
        activity.setStartTime(request.getStartTime());
        activity.setAdditionalMetrics(request.getAdditionalMetrics());
        
        Activity savedActivity = activityRepository.save(activity);
        return convertToResponse(savedActivity);
    }
    public ActivityResponse convertToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }

    public  List<ActivityResponse> getActivityByUserId(String userId) {

        List<Activity> activities = activityRepository.findByUserId(userId);
        List<ActivityResponse> responses = new ArrayList<>();
        for(Activity activity : activities) {
            responses.add(convertToResponse(activity));
        }
        return responses;
    }

    public ActivityResponse getActivityById(String id) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new RuntimeException("Activity not found"));
        return convertToResponse(activity);
    }
}
