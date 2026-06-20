package com.fitness.activityservice.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

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

    @GetMapping("/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivityByUserId(@PathVariable String userId) {
        // Logic to get activity by user ID
        return ResponseEntity.ok(activityService.getActivityByUserId(userId));
    }

    @GetMapping("/activity/{id}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String id) {
        // Logic to get activity by ID
        return ResponseEntity.ok(activityService.getActivityById(id));
    }
}
