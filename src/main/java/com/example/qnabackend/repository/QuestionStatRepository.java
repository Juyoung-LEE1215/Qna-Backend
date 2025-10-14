package com.example.qnabackend.repository;

import com.example.qnabackend.entity.QuestionStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionStatRepository extends JpaRepository<QuestionStat, Long> {
}
