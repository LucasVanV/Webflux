package com.example.exo15.controller;

import com.example.exo15.dto.ErrorResponse;
import com.example.exo15.dto.LoginRequest;
import com.example.exo15.dto.LoginResponse;
import com.example.exo15.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    // utilisateurs codés en dur
    private final Map<String, String> users = Map.of(
            "Lucas", "Lucas123",
            "Nicolas", "Nicolas123"
    );

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest request) {
        String expectedPassword = users.get(request.getUsername());

        if (expectedPassword == null || !expectedPassword.equals(request.getPassword())) {
            ErrorResponse error = new ErrorResponse(
                    "UNAUTHORIZED",
                    "Identifiants invalides"
            );
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
        }

        String token = jwtService.generateToken(request.getUsername());
        return Mono.just(ResponseEntity.ok(new LoginResponse(token)));
    }
}