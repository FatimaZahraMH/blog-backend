package com.blog.repository;

import com.blog.entity.Article;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, QuerydslPredicateExecutor<Article> {              // ← QueryDSL ajouté

    Optional<Article> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Article> findByAuthorId(Long authorId, Pageable pageable);
}
