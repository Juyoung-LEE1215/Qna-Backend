package com.example.qnabackend.service;

import com.example.qnabackend.dto.*;
import com.example.qnabackend.entity.Question;
import com.example.qnabackend.entity.QuestionStat;
import com.example.qnabackend.entity.QuestionStatus;
import com.example.qnabackend.repository.QuestionRepository;
import com.example.qnabackend.repository.QuestionStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionStatRepository questionStatRepository;

    public Long create(Long userId, QuestionCreateRequest req) {
        Question q = Question.builder()
                .userId(userId)
                .title(req.title())
                .content(req.content())
                .category(req.category())
                .status(QuestionStatus.OPEN)
                .build();
        Question saved = questionRepository.save(q);

        QuestionStat stats = QuestionStat.builder()
                .question(saved)
                .viewCount(0L)
                .likeCount(0L)
                .answerCount(0L)
                .popularityScore(0.0)
                .build();

        questionStatRepository.save(stats);
        saved.assignStats(stats);

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(String category, Pageable pageable) {
        Page<Question> result = (category == null || category.isBlank())
                ? questionRepository.findByStatus(QuestionStatus.OPEN, pageable)
                : questionRepository.findByStatusAndCategory(QuestionStatus.OPEN, category, pageable);
        return result.map(QuestionResponse::of);
    }

    @Transactional
    public QuestionResponse get(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));

        QuestionStat stats = question.getStats();
        stats.increaseViewCount();
        stats.recalculatePopularityScore();
        questionStatRepository.save(stats);

        return QuestionResponse.of(question);
    }


    public void update(Long userId, Long id, QuestionUpdateRequest req) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        if (!q.getUserId().equals(userId)) {
            throw new IllegalStateException("only author can modify");
        }
        q.setTitle(req.title());
        q.setContent(req.content());
        q.setCategory(req.category());
    }

    public void delete(Long userId, Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        if (!q.getUserId().equals(userId)) {
            throw new IllegalStateException("only author can delete");
        }
        q.setStatus(QuestionStatus.DELETED);
    }

    @Transactional
    public void updateStats(Long id,String type){
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        QuestionStat stats = question.getStats();

        switch(type.toLowerCase()){
            case "view" -> stats.increaseViewCount();
            case "like" -> stats.increaseLikeCount();
            case "answer" -> stats.increaseAnswerCount();
            default -> throw new IllegalArgumentException("type not supported");
        }

        stats.recalculatePopularityScore();
        questionStatRepository.save(stats);
    }
}