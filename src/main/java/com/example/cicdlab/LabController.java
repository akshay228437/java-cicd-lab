package com.example.cicdlab;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LabController {

    private final GreetingService greetingService;
    private final String appVersion;
    private final String buildId;
    private final String environment;

    public LabController(
            GreetingService greetingService,
            @Value("${app.version}") String appVersion,
            @Value("${app.build-id}") String buildId,
            @Value("${app.environment}") String environment) {
        this.greetingService = greetingService;
        this.appVersion = appVersion;
        this.buildId = buildId;
        this.environment = environment;
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "application", "java-cicd-lab",
                "message", "Java CI/CD lab is running.");
    }

    @GetMapping("/api/hello")
    public Map<String, String> hello(
            @RequestParam(name = "name", defaultValue = "World") String name) {
        return Map.of("message", greetingService.greet(name));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("version", appVersion, "buildId", buildId);
    }

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of(
                "application", "java-cicd-lab",
                "environment", environment,
                "version", appVersion,
                "buildId", buildId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalidRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }
}
