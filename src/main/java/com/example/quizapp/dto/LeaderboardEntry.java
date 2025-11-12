package com.example.quizapp.dto;

public record LeaderboardEntry(
        String displayName,
        int score,
        int total,
        String submittedAt
) {}

