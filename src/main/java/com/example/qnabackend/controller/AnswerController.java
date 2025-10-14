package com.example.qnabackend.controller;

import com.example.qnabackend.dto.*;
import com.example.qnabackend.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qna/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<Long> create(@AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody AnswerCreateRequest req) {
        if (userId == null) userId = 1L;
        Long id = answerService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<Page<AnswerResponse>> listByQuestion(
            @RequestParam Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(answerService.list(questionId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody AnswerUpdateRequest req) {
        if (userId == null) userId = 1L;
        answerService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        if (userId == null) userId = 1L;
        answerService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/votes")
    public ResponseEntity<Void> vote(@AuthenticationPrincipal Long userId,
                                     @PathVariable("id") Long answerId,
                                     @Valid @RequestBody VoteRequest req) {
        if (userId == null) userId = 1L;
        answerService.vote(userId, answerId, req.type());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/reports")
    public ResponseEntity<Void> report(@AuthenticationPrincipal Long userId,
                                       @PathVariable("id") Long answerId,
                                       @Valid @RequestBody ReportRequest req) {
        if (userId == null) userId = 1L;
        answerService.report(userId, answerId, req.reason());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}