package io.john.programming.todoapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")  // Set the base path for the API
public class HealthCheckController {

    @GetMapping("/health-check")  // Health check endpoint
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "app healthy");
        return ResponseEntity.ok(response);
    }
}
