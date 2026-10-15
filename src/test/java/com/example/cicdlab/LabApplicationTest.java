package com.example.cicdlab;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "app.version=2.0.0-test",
            "app.build-id=build-42",
            "app.environment=test"
        })
class LabApplicationTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Test
    void servesApplicationHome() throws Exception {
        HttpResponse<String> response = get("/");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Java CI/CD lab is running."));
    }

    @Test
    void exposesHealthForDeploymentProbes() throws Exception {
        HttpResponse<String> response = get("/health");

        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"UP\"}", response.body());
        assertTrue(response.headers().firstValue("content-type")
                .orElse("").startsWith("application/json"));
    }

    @Test
    void returnsDefaultAndCustomGreetings() throws Exception {
        HttpResponse<String> defaultResponse = get("/api/hello");
        HttpResponse<String> customResponse = get("/api/hello?name=Jenkins");

        assertEquals(200, defaultResponse.statusCode());
        assertTrue(defaultResponse.body().contains("Hello, World!"));
        assertEquals(200, customResponse.statusCode());
        assertTrue(customResponse.body().contains("Hello, Jenkins!"));
    }

    @Test
    void exposesConfiguredReleaseIdentity() throws Exception {
        HttpResponse<String> response = get("/version");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("2.0.0-test"));
        assertTrue(response.body().contains("build-42"));
    }

    @Test
    void exposesConfiguredEnvironment() throws Exception {
        HttpResponse<String> response = get("/api/info");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"environment\":\"test\""));
        assertTrue(response.body().contains("java-cicd-lab"));
    }

    @Test
    void returnsBadRequestForAnExcessivelyLongName() throws Exception {
        HttpResponse<String> response = get("/api/hello?name=" + "a".repeat(101));

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Name must be 100 characters or fewer."));
    }

    @Test
    void returnsNotFoundForUnknownRoutes() throws Exception {
        assertEquals(404, get("/missing").statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
