package com.example.qnabackend.controller;

import com.example.qnabackend.dto.*;
import com.example.qnabackend.entity.VoteType;
import com.example.qnabackend.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Q&A 답변 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/qna/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    /** ✅ 답변 등록 */
    @PostMapping
    public ResponseEntity<Long> create(@AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody AnswerCreateRequest req) {
        Long id = answerService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /** ✅ 답변 목록 조회 (질문 기준) */
    @GetMapping
    public ResponseEntity<Page<AnswerResponse>> list(@RequestParam Long questionId,
                                                     @AuthenticationPrincipal Long userId,
                                                     @RequestParam(defaultValue = "false") boolean isAdmin,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(
                answerService.list(questionId, userId, isAdmin, page, size, sort)
        );
    }

    /** ✅ 답변 수정 (작성자만 가능) */
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody AnswerUpdateRequest req) {
        answerService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 답변 삭제 (soft delete) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        answerService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 답변 추천/비추천 (UP/DOWN, 같은 타입은 토글 해제) */
    @PostMapping("/{id}/votes")
    public ResponseEntity<Void> vote(@AuthenticationPrincipal Long userId,
                                     @PathVariable Long id,
                                     @RequestBody VoteRequest req) {
        VoteType type = VoteType.valueOf(req.type().toUpperCase()); // "UP" | "DOWN"
        answerService.vote(userId, id, type);
        return ResponseEntity.ok().build();
    }

    /** ✅ 답변 신고 (중복 신고 방지, 임계치 초과 시 BLINDED 처리) */
    @PostMapping("/{id}/reports")
    public ResponseEntity<Void> report(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @RequestBody ReportRequest req) {
        answerService.report(userId, id, req.reason());
        return ResponseEntity.accepted().build();
    }
}
