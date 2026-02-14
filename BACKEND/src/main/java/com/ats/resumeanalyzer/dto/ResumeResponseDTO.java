package com.ats.resumeanalyzer.dto;

import java.util.List;

public class ResumeResponseDTO {

    private String role;
    private int score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String recommendation;

    // Required for JSON serialization/deserialization
    public ResumeResponseDTO() {
    }

    // Full constructor (USED BY SERVICE LOGIC)
    public ResumeResponseDTO(
            String role,
            int score,
            List<String> matchedSkills,
            List<String> missingSkills,
            String recommendation
    ) {
        this.role = role;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendation = recommendation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
