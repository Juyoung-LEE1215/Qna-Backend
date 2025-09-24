package com.example.qnabackend.repository;

import com.example.qnabackend.entity.AnswerReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerReportRepository extends JpaRepository<AnswerReport, Long> {
    boolean existsByAnswerIdAndReporterId(Long answerId, Long reporterId);
}
