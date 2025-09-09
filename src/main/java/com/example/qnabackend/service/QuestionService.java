package com.example.qnabackend.service;

import com.example.qnabackend.dto.QuestionCreateRequest;
import com.example.qnabackend.dto.QuestionResponse;
import com.example.qnabackend.dto.QuestionUpdateRequest;
import com.example.qnabackend.entity.Question;
import com.example.qnabackend.entity.QuestionStatus;
import com.example.qnabackend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service                     // ✅ 빈 등록
@RequiredArgsConstructor     // ✅ 생성자 주입 (Lombok)
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public Long create(Long userId, QuestionCreateRequest req) {
        Question q = Question.builder()
                .userId(userId)
                .title(req.title())
                .content(req.content())
                .category(req.category())
                .status(QuestionStatus.OPEN)
                .build();
        return questionRepository.save(q).getId();
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(String category, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, (sort == null || sort.isBlank()) ? "id" : sort));
        Page<Question> result = (category == null || category.isBlank())
                ? questionRepository.findByStatus(QuestionStatus.OPEN, pageable)
                : questionRepository.findByStatusAndCategory(QuestionStatus.OPEN, category, pageable);
        return result.map(QuestionResponse::of);
    }

    @Transactional(readOnly = true)
    public QuestionResponse get(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        return QuestionResponse.of(q);
    }

    @Transactional
    public void update(Long userId, Long id, QuestionUpdateRequest req) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        if (!q.getUserId().equals(userId)) throw new SecurityException("not owner");
        if (q.getStatus() == QuestionStatus.DELETED) throw new IllegalStateException("deleted question");
        q.setTitle(req.title());
        q.setContent(req.content());
        q.setCategory(req.category());
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        if (!q.getUserId().equals(userId)) throw new SecurityException("not owner");
        q.setStatus(QuestionStatus.DELETED); // soft delete
    }
}
