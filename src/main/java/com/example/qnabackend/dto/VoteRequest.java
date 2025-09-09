package com.example.qnabackend.dto;

import jakarta.validation.constraints.NotBlank;

/** 답변 추천/비추천 요청 DTO */
public record VoteRequest(
        @NotBlank String type // "UP" 또는 "DOWN"
) {}
