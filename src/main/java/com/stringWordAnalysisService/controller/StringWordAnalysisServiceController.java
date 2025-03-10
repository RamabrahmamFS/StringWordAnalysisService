package com.stringWordAnalysisService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stringWordAnalysisService.service.StringProcessorService;
import com.stringWordAnalysisService.service.dto.ResponseObject;

import java.util.Map;


@RestController
@RequestMapping("/api/process")
public class StringWordAnalysisServiceController {
	
	@Autowired
	private  StringProcessorService stringProcessorService;

	@PostMapping
	public ResponseObject process(@RequestBody Map<String, String> request) {
		return stringProcessorService.processString(request);
	}
	
	


}
