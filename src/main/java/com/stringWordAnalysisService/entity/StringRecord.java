package com.stringWordAnalysisService.entity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class StringRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String inputString;

    private int wordCount;
    
    private boolean hasPalindrome;

 
    private String palindromeWords;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public boolean isHasPalindrome() {
		return hasPalindrome;
	}

	public void setHasPalindrome(boolean hasPalindrome) {
		this.hasPalindrome = hasPalindrome;
	}

	public String getPalindromeWords() {
		return palindromeWords;
	}

	public void setPalindromeWords(String palindromeWords) {
		this.palindromeWords = palindromeWords;
	}

    
}