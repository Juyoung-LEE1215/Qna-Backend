package com.example.qnabackend.repository;

import com.example.qnabackend.entity.QuestionStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionStatRepository extends JpaRepository<QuestionStat, Long> {
}
