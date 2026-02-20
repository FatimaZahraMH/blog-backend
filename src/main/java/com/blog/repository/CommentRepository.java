package com.blog.repository;

import com.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.article.id = :articleId")
    void deleteAllByArticleId(@Param("articleId") Long articleId);
}
