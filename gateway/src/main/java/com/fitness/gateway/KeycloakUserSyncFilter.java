package com.fitness.gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
class KeycloakUserSyncFilter implements WebFilter {
    
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
     
        String userId=exchange.getRequest().getHeaders().getFirst("X-USER-ID");
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");

        if(userId!=null && token!=null){
            return userService.validateUser(userId)
                .flatMap(exist -> {
                    if (!exist) {
                        // Register user
                        RegisterRequest registerRequest = getuserDetails(token);
                        if(registerRequest != null) {
                            return userService.registerUser(registerRequest).then(Mono.empty());
                        }
                        else{
                            return Mono.empty();
                        }
                    } else {
                        log.info("User already exist , skipping syncing");
                        return Mono.empty();
                    }
                })
                .then(Mono.defer(() ->{
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-USER-ID", userId)
                        .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getuserDetails(String token) {
        
        try{
            String tokenWithoutBearer = token.replace("Bearer ", "");
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            registerRequest.setPassword("defaultPassword"); // Set a default password or generate one as needed
            return registerRequest;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    


}