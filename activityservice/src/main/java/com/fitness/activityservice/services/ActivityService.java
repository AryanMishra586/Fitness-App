package com.fitness.activityservice.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
// import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.models.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exhange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    
    // public ActivityService(ActivityRepository activityRepository, UserValidationService userValidationService) {
    //     this.activityRepository = activityRepository;
    //     this.userValidationService = userValidationService;
    // }
    
    public ActivityResponse trackActivity(ActivityRequest request) {
        
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser) {
            throw new RuntimeException("Invalid user ID");
        }

        
        Activity activity = new Activity();
        // activity.setId(request.getId());
        activity.setUserId(request.getUserId());
        activity.setActivityType(request.getActivityType());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurned(request.getCaloriesBurned());
        activity.setStartTime(request.getStartTime());
        activity.setAdditionalMetrics(request.getAdditionalMetrics());
        
        Activity savedActivity = activityRepository.save(activity);

        // Publish to rabbitmq
        try{
            rabbitTemplate.convertAndSend(exchange, routingKey,savedActivity);
        }
        catch(Exception e){
            log.error("Failed to Publish activity to RabbitMQ");
            throw new RuntimeException("Error occurred while publishing to RabbitMQ");
        }



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
