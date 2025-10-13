package com.example.qnabackend.controller;

import com.example.qnabackend.dto.QuestionCreateRequest;
import com.example.qnabackend.dto.QuestionResponse;
import com.example.qnabackend.dto.QuestionUpdateRequest;
import com.example.qnabackend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qna/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Long> create(@AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody QuestionCreateRequest req) {
        if (userId == null) userId = 1L; // dev용 임시
        Long id = questionService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, toSafeSort(sort));
        return ResponseEntity.ok(questionService.list(category, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @Valid @RequestBody QuestionUpdateRequest req) {
        if (userId == null) userId = 1L;
        questionService.update(userId, id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id) {
        if (userId == null) userId = 1L;
        questionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stats")
    public ResponseEntity<Void> updateStats(@PathVariable Long id,
                                            @RequestParam String type) {
        questionService.updateStats(id, type);
        return ResponseEntity.ok().build();
    }

    // ---- 정렬 화이트리스트 ----
    private Sort toSafeSort(String sort) {
        String prop = "id";
        Sort.Direction dir = Sort.Direction.DESC;

        if (sort != null && !sort.isBlank()) {
            switch (sort.toLowerCase()) {
                case "latest" -> prop = "createdAt";
                case "recommended" -> prop = "likeCount";
                case "popular" -> prop = "viewCount";
            }
        }

        return Sort.by(dir, prop);
    }
}
