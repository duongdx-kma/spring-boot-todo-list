package io.john.programming.todoapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TodoAppApplicationTests {

    @Autowired
    private MockMvc mockMvc;  // MockMvc is used to simulate HTTP requests to controllers.

    @Test
    void contextLoads() {
        // Ensures the Spring Boot application context is loaded without any issues.
    }

    // Test for Health Check API from HealthCheckController
    @Test
    void healthCheck_ReturnsHealthyStatus() throws Exception {
        // Simulate an HTTP GET request to the /health-check endpoint and verify the response
        mockMvc.perform(get("/health-check"))
                .andExpect(status().isOk())  // Expect HTTP 200 OK status
                .andExpect(jsonPath("$.status").value("healthy"));  // Expect JSON response {"status": "healthy"}
    }
}
