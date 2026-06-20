package com.fitness.activityservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.services.ActivityService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    
    private final ActivityService activityService;
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    @PostMapping("/track")
    public ResponseEntity<ActivityResponse> trackActitvity( @RequestBody ActivityRequest request) {
        // Logic to track activity
        return ResponseEntity.ok(activityService.trackActivity(request));
        
    }
}
