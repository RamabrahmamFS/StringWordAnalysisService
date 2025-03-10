package com.stringWordAnalysisService;


import com.stringWordAnalysisService.repository.StringRecordRepository;
import com.stringWordAnalysisService.service.dto.ResponseObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StringProcessorServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRecordRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        // Create a MockRestServiceServer for intercepting Service 2 calls
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        repository.deleteAll(); // Clean DB before each test
    }

    @Test
    void testProcessString_Success() throws Exception {
        // Given input data
        Map<String, String> request = new HashMap<>();
        request.put("input", "madam racecar level");

        // Mock response from Service 2
        ResponseObject mockResponse = new ResponseObject(UUID.randomUUID(), 3, true, List.of("madam", "racecar"));
        mockServer.expect(requestTo("http://localhost:8091/api/analyze"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // Perform POST request
        mockMvc.perform(post("/api/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordCount", is(3)))
                .andExpect(jsonPath("$.hasPalindrome", is(true)));

        // Ensure database entry is created
        assert repository.findByInputString("madam racecar level").isPresent();

        // Verify the mock server interaction
        mockServer.verify();
    }

   
}
