package com.stringWordAnalysisService.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stringWordAnalysisService.entity.StringRecord;
import com.stringWordAnalysisService.repository.StringRecordRepository;
import com.stringWordAnalysisService.service.dto.ResponseObject;

@Service
public class StringProcessorService {

	@Autowired
	private  StringRecordRepository repository;

	@Autowired
	private RestTemplate restTemplate;

	private static final String STUDENT_API_BASE_URL = "http://localhost:8091/api/analyze";

	public ResponseObject processString(String input) {
		Optional<StringRecord> existingRecord = repository.findByInputString(input);
		if (existingRecord.isPresent()) {
			throw new IllegalStateException("String already processed");
		}

		ResponseEntity<ResponseObject> response = restTemplate.postForEntity(STUDENT_API_BASE_URL, input,
				ResponseObject.class);
		
		ResponseObject result = response.getBody();

		StringRecord record = new StringRecord();
		record.setInputString(input);
		record.setWordCount(result.wordCount());
		record.setHasPalindrome(result.hasPalindrome());
		record.setPalindromeWords(String.join(",", result.palindromeWords()));

		repository.save(record);
		return result;
	}
}