package com.example.qnabackend.repository;

import com.example.qnabackend.entity.AnswerVote;
import com.example.qnabackend.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerVoteRepository extends JpaRepository<AnswerVote, Long> {
    Optional<AnswerVote> findByAnswerIdAndUserId(Long answerId, Long userId);
    long countByAnswerIdAndType(Long answerId, VoteType type);
}
