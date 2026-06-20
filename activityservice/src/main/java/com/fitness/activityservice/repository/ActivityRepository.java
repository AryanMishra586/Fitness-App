package com.fitness.activityservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitness.activityservice.models.Activity;

public interface ActivityRepository  extends MongoRepository<Activity, String> {
    
}
