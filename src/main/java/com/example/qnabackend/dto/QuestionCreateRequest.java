package com.example.qnabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 질문 생성 요청 DTO */
public record QuestionCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 10000) String content,
        @Size(max = 50) String category
) {}
