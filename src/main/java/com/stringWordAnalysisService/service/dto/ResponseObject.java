package com.stringWordAnalysisService.service.dto;

import java.util.List;
import java.util.UUID;

import java.util.List;
import java.util.UUID;

public record ResponseObject(
    UUID id,
    int wordCount,
    boolean hasPalindrome,
    List<String> palindromeWords
) {}
    

