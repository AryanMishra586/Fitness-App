package com.fitness.gateway.user;

import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId) {

            return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                        throw new RuntimeException("User not found");
                    } else if (e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                        throw new RuntimeException("User service is down");
                    } else {
                        throw new RuntimeException("An error occurred while validating user");
                    }
                });
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        throw new RuntimeException("Invalid user data "+ e.getMessage());
                    } else if (e.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                        throw new RuntimeException("User service is down "+ e.getMessage());
                    } else {
                        throw new RuntimeException("An error occurred while registering user "+ e.getMessage());
                    }
                });
    }
}
