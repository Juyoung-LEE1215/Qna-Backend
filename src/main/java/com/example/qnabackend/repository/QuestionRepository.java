package com.example.qnabackend.repository;

import com.example.qnabackend.entity.Question;
import com.example.qnabackend.entity.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);
    Page<Question> findByStatusAndCategory(QuestionStatus status, String category, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.stats s WHERE q.status = :status ORDER BY s.likeCount DESC")
    Page<Question> findByStatusOrderByLikeCountDesc(QuestionStatus status, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.stats s WHERE q.status = :status AND q.category = :category ORDER BY s.likeCount DESC")
    Page<Question> findByStatusAndCategoryOrderByLikeCountDesc(QuestionStatus status, String category, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.stats s WHERE q.status = :status ORDER BY s.viewCount DESC")
    Page<Question> findByStatusOrderByViewCountDesc(QuestionStatus status, Pageable pageable);

    @Query("SELECT q FROM Question q JOIN q.stats s WHERE q.status = :status AND q.category = :category ORDER BY s.viewCount DESC")
    Page<Question> findByStatusAndCategoryOrderByViewCountDesc(QuestionStatus status, String category, Pageable pageable);
}
