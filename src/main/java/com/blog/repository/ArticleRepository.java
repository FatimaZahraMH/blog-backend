package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ArticleRepository extends JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article> {

    Optional<Article> findBySlug(String slug);

    boolean existsBySlug(String slug);


    Page<Article> findByAuthorIdAndPublished(Long authorId, boolean published, Pageable pageable);


    @Query("SELECT DISTINCT a FROM Article a " +
            "LEFT JOIN FETCH a.tags " +
            "WHERE a.author.id = :authorId AND a.published = true")
    Page<Article> findPublishedByAuthorIdWithTags(@Param("authorId") Long authorId, Pageable pageable);
}