package com.example.qnabackend.service;

import com.example.qnabackend.dto.*;
import com.example.qnabackend.entity.*;
import com.example.qnabackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service                           // ✅ 빈 등록 필수
@RequiredArgsConstructor           // final 필드 생성자 주입
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerVoteRepository voteRepository;
    private final AnswerReportRepository reportRepository;

    @Value("${qna.answer.report.threshold:5}")
    private int reportThreshold;

    @Transactional
    public Long create(Long userId, AnswerCreateRequest req) {
        var q = questionRepository.findById(req.questionId())
                .orElseThrow(() -> new IllegalArgumentException("question not found"));
        if (q.getStatus() != QuestionStatus.OPEN) throw new IllegalStateException("question not open");

        Answer a = Answer.builder()
                .questionId(req.questionId())
                .userId(userId)
                .content(req.content())
                .isPrivate(req.isPrivate())
                .status(AnswerStatus.VISIBLE)
                .build();
        return answerRepository.save(a).getId();
    }

    @Transactional(readOnly = true)
    public Page<AnswerResponse> list(Long questionId, Long currentUserId, boolean isAdmin,
                                     int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, (sort == null || sort.isBlank()) ? "id" : sort));
        Page<Answer> result = answerRepository.findByQuestionIdAndStatus(questionId, AnswerStatus.VISIBLE, pageable);
        return result.map(a -> AnswerResponse.of(a, canSeeBody(a, questionId, currentUserId, isAdmin)));
    }

    private boolean canSeeBody(Answer a, Long questionId, Long currentUserId, boolean isAdmin) {
        if (isAdmin) return true;
        if (a.getStatus() == AnswerStatus.BLINDED) {
            return a.getUserId().equals(currentUserId);
        }
        if (!a.isPrivate()) return true;
        var q = questionRepository.findById(questionId).orElse(null);
        return q != null && (q.getUserId().equals(currentUserId) || a.getUserId().equals(currentUserId));
    }

    @Transactional
    public void update(Long userId, Long answerId, AnswerUpdateRequest req) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        if (!a.getUserId().equals(userId)) throw new SecurityException("not owner");
        if (a.getStatus() == AnswerStatus.DELETED) throw new IllegalStateException("deleted");
        a.setContent(req.content());
        a.setPrivate(req.isPrivate());
    }

    @Transactional
    public void delete(Long userId, Long answerId) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        if (!a.getUserId().equals(userId)) throw new SecurityException("not owner");
        a.setStatus(AnswerStatus.DELETED);
    }

    @Transactional
    public void vote(Long userId, Long answerId, VoteType type) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        if (a.getUserId().equals(userId)) throw new IllegalStateException("self vote not allowed");
        if (a.getStatus() != AnswerStatus.VISIBLE) throw new IllegalStateException("not votable");

        var existing = voteRepository.findByAnswerIdAndUserId(answerId, userId);
        if (existing.isEmpty()) {
            voteRepository.save(AnswerVote.builder().answerId(answerId).userId(userId).type(type).build());
            applyVoteDelta(a, type, +1);
        } else {
            var v = existing.get();
            if (v.getType() == type) {
                voteRepository.delete(v);                    // 토글 해제
                applyVoteDelta(a, type, -1);
            } else {
                VoteType prev = v.getType();                 // 타입 변경
                v.setType(type);
                applyVoteDelta(a, type, +1);
                applyVoteDelta(a, prev, -1);
            }
        }
    }

    private void applyVoteDelta(Answer a, VoteType t, int delta) {
        if (t == VoteType.UP) a.setUpvoteCount(a.getUpvoteCount() + delta);
        else a.setDownvoteCount(a.getDownvoteCount() + delta);
    }

    @Transactional
    public void report(Long reporterId, Long answerId, String reason) {
        Answer a = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("answer not found"));
        if (a.getUserId().equals(reporterId)) throw new IllegalStateException("self report not allowed");
        if (reportRepository.existsByAnswerIdAndReporterId(answerId, reporterId))
            throw new IllegalStateException("already reported");

        reportRepository.save(AnswerReport.builder()
                .answerId(answerId).reporterId(reporterId).reason(reason).build());
        a.setReportCount(a.getReportCount() + 1);
        if (a.getReportCount() >= reportThreshold && a.getStatus() == AnswerStatus.VISIBLE) {
            a.setStatus(AnswerStatus.BLINDED);
        }
    }
}
