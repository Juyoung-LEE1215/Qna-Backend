package com.example.qnabackend.repository;

import com.example.qnabackend.entity.Question;
import com.example.qnabackend.entity.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);
    Page<Question> findByStatusAndCategory(QuestionStatus status, String category, Pageable pageable);
}
