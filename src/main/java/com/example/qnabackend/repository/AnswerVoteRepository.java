package com.example.qnabackend.repository;

import com.example.qnabackend.entity.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, Long> {
    Optional<AnswerVote> findByAnswerIdAndUserId(Long answerId, Long userId);
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
}
