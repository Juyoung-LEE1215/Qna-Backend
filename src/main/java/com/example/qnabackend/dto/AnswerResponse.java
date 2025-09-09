package com.example.qnabackend.dto;

import com.example.qnabackend.entity.Answer;
import com.example.qnabackend.entity.AnswerStatus;

import java.time.LocalDateTime;

/** 답변 응답 DTO */
public record AnswerResponse(
        Long id,
        Long questionId,
        Long userId,
        String content,
        boolean isPrivate,
        AnswerStatus status,
        int upvoteCount,
        int downvoteCount,
        int reportCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AnswerResponse of(Answer a, boolean canSeeContent) {
        String body = a.isPrivate() && !canSeeContent
                ? "(비공개 답변입니다)"
                : (a.getStatus() == AnswerStatus.BLINDED && !canSeeContent)
                ? "(블라인드 처리된 답변입니다)"
                : a.getContent();
        return new AnswerResponse(
                a.getId(),
                a.getQuestionId(),
                a.getUserId(),
                body,
                a.isPrivate(),
                a.getStatus(),
                a.getUpvoteCount(),
                a.getDownvoteCount(),
                a.getReportCount(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
