package com.stringWordAnalysisService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stringWordAnalysisService.entity.StringRecord;
import com.stringWordAnalysisService.repository.StringRecordRepository;
import com.stringWordAnalysisService.service.StringProcessorService;
import com.stringWordAnalysisService.service.dto.ResponseObject;

@ExtendWith(MockitoExtension.class)
public class WordAnalysisServiceTest {

    @Mock
    private StringRecordRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StringProcessorService wordAnalysisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testProcessString_WhenNewString_ShouldProcessAndSave() {
        // Mock input
        Map<String, String> input = new HashMap<>();
        input.put("input", "madam racecar hello");

        // Mock repository call (String is NOT in the database)
        when(repository.findByInputString("madam racecar hello")).thenReturn(Optional.empty());

        // Mock response from external service
        ResponseObject mockResponse = new ResponseObject(UUID.randomUUID(), 3, true, List.of("madam", "racecar"));
        ResponseEntity<ResponseObject> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), eq(input), eq(ResponseObject.class))).thenReturn(mockResponseEntity);

        // Call method
        ResponseObject result = wordAnalysisService.processString(input);

        // Verify behavior
        assertNotNull(result);
        assertEquals(3, result.wordCount());
        assertTrue(result.hasPalindrome());
        assertEquals(List.of("madam", "racecar"), result.palindromeWords());

        // Verify repository save call
        verify(repository, times(1)).save(any(StringRecord.class));
    }

    @Test
    public void testProcessString_WhenStringAlreadyProcessed_ShouldThrowException() {
        // Mock input
        Map<String, String> input = new HashMap<>();
        input.put("input", "madam racecar hello");

        // Mock existing record in repository
        when(repository.findByInputString("madam racecar hello")).thenReturn(Optional.of(new StringRecord()));

        // Expect exception
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            wordAnalysisService.processString(input);
        });

        assertEquals("String already processed", exception.getMessage());

        // Verify repository save was never called
        verify(repository, never()).save(any(StringRecord.class));
    }
}
