package com.example.qnabackend.dto;

import jakarta.validation.constraints.NotBlank;

/** 답변 수정 요청 DTO */
public record AnswerUpdateRequest(
        @NotBlank String content,
        boolean isPrivate
) {}
