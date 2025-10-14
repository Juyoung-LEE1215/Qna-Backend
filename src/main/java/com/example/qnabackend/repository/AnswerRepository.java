package com.example.qnabackend.repository;

import com.example.qnabackend.entity.Answer;
import com.example.qnabackend.entity.AnswerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByQuestionId(Long questionId, Pageable pageable);
    Page<Answer> findByQuestionIdAndStatus(Long questionId, AnswerStatus status, Pageable pageable);
}
