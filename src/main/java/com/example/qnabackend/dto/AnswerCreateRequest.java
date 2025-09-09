package com.example.qnabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 답변 등록 요청 DTO */
public record AnswerCreateRequest(
        @NotNull Long questionId,
        @NotBlank String content,
        boolean isPrivate
) {}
