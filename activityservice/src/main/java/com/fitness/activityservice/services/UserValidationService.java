package com.fitness.activityservice.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {

        try{
            return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        }
        catch(WebClientResponseException e){
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new RuntimeException("User not found");
            }
            else if (e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new RuntimeException("User service is down");
            }else {
                throw new RuntimeException("An error occurred while validating user");
            }
        }
    }
}
