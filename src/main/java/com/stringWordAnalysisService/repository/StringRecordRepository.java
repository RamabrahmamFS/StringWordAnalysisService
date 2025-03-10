package com.stringWordAnalysisService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stringWordAnalysisService.entity.StringRecord;

import java.util.Optional;
import java.util.UUID;

public interface StringRecordRepository extends JpaRepository<StringRecord, UUID> {
    Optional<StringRecord> findByInputString(String inputString);
}