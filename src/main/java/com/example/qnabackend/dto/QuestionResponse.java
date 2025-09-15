package com.example.qnabackend.dto;

import com.example.qnabackend.entity.Question;
import com.example.qnabackend.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionResponse(
        Long id,
        Long userId,
        String title,
        String content,
        String category,
        QuestionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static QuestionResponse of(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getUserId(),
                q.getTitle(),
                q.getContent(),
                q.getCategory(),
                q.getStatus(),
                q.getCreatedAt(),
                q.getUpdatedAt()
        );
    }
}
