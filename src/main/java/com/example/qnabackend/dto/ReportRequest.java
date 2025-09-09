package com.example.qnabackend.dto;

import jakarta.validation.constraints.Size;

/** 답변 신고 요청 DTO */
public record ReportRequest(
        @Size(max = 255) String reason
) {}
