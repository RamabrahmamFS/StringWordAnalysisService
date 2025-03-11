package com.stringWordAnalysisService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.stringWordAnalysisService.entity.StringRecord;
import com.stringWordAnalysisService.exception.StringProcessorException;
import com.stringWordAnalysisService.repository.StringRecordRepository;
import com.stringWordAnalysisService.service.StringProcessorService;
import com.stringWordAnalysisService.service.dto.ResponseObject;

@ExtendWith(MockitoExtension.class)
public class WordAnalysisServiceTest { @InjectMocks
    private StringProcessorService stringProcessorService;

    @Mock
    private StringRecordRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processString_shouldReturnResponseObject_whenStringIsNew() {
        // Arrange
        String input = "racecar";
        Map<String, String> inputMap = Map.of("input", input);

        ResponseObject mockResponse = new ResponseObject(UUID.randomUUID(), 3, true, List.of("madam", "racecar"));
        ResponseEntity<ResponseObject> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(repository.findByInputString(input)).thenReturn(Optional.empty());
        when(restTemplate.postForEntity(anyString(), any(), eq(ResponseObject.class))).thenReturn(mockEntity);

        // Act
        ResponseObject result = stringProcessorService.processString(inputMap);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.wordCount()).isEqualTo(7);
        assertThat(result.hasPalindrome()).isTrue();
        assertThat(result.palindromeWords()).containsExactly("racecar");

        verify(repository).save(any(StringRecord.class));
    }

    @Test
    void processString_shouldThrowException_whenStringAlreadyProcessed() {
        // Arrange
        String input = "alreadyExists";
        Map<String, String> inputMap = Map.of("input", input);

        when(repository.findByInputString(input)).thenReturn(Optional.of(new StringRecord()));

        // Act & Assert
        assertThatThrownBy(() -> stringProcessorService.processString(inputMap))
            .isInstanceOf(StringProcessorException.class)
            .hasMessage("String already processed");

        verify(repository, never()).save(any());
    }

    @Test
    void processString_shouldThrowException_whenRestTemplateThrowsException() {
        // Arrange
        String input = "networkFailure";
        Map<String, String> inputMap = Map.of("input", input);

        when(repository.findByInputString(input)).thenReturn(Optional.empty());
        when(restTemplate.postForEntity(anyString(), any(), eq(ResponseObject.class)))
            .thenThrow(new RestClientException("Connection refused"));

        // Act & Assert
        assertThatThrownBy(() -> stringProcessorService.processString(inputMap))
            .isInstanceOf(StringProcessorException.class)
            .hasMessageContaining("Error communicating with analysis service");

        verify(repository, never()).save(any());
    }

    @Test
    void processString_shouldThrowException_whenInputIsEmpty() {
        // Arrange
        Map<String, String> inputMap = Map.of("input", "");

        // Act & Assert
        assertThatThrownBy(() -> stringProcessorService.processString(inputMap))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Input string must not be empty");

        verify(repository, never()).save(any());
    }

    @Test
    void processString_shouldThrowException_whenInputIsNull() {
        // Arrange
        Map<String, String> inputMap = new HashMap<>();

        // Act & Assert
        assertThatThrownBy(() -> stringProcessorService.processString(inputMap))
            .isInstanceOf(StringProcessorException.class)
            .hasMessage("Input string must not be empty");

        verify(repository, never()).save(any());
    }}
