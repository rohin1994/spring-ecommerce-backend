package com.ecommerce.api_gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class RateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exceedsRateLimit_returns429ProblemDetail() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/v1/products"))
                    .andExpect(status().is(not(429)));
        }

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.type").value("too-many-requests"))
                .andExpect(jsonPath("$.title").value("Too many requests"));
    }
}
