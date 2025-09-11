package com.example.qnabackend.controller;

import com.example.qnabackend.dto.QuestionCreateRequest;
import com.example.qnabackend.dto.QuestionResponse;
import com.example.qnabackend.dto.QuestionUpdateRequest;
import com.example.qnabackend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Q&A 질문 컨트롤러
 * - 생성/목록/단건/수정/삭제(soft delete)
 */
@RestController
@RequestMapping("/api/qna/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /** 질문 생성 */
    @PostMapping
    public ResponseEntity<Long> create(@AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody QuestionCreateRequest req) {
        Long id = questionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /** 질문 목록 (카테고리/페이징/정렬) */
    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort // 기본 정렬은 서비스에서 처리
    ) {
        return ResponseEntity.ok(questionService.list(category, page, size, sort));
    }

    /** 질문 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.get(id));
    }

    /** 질문 수정 (작성자만) */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody QuestionUpdateRequest req) {
        questionService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    /** 질문 삭제 (soft delete) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        questionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
