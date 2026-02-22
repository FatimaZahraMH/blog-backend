package com.blog.repository;

import com.blog.dto.request.ArticleSearchFilter;
import com.blog.entity.QArticle;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.experimental.UtilityClass;


@UtilityClass
public class ArticlePredicate {

    public static Predicate build(ArticleSearchFilter filter) {


        QArticle article = QArticle.article;


        BooleanBuilder builder = new BooleanBuilder();


        if (hasText(filter.keyword())) {
            builder.and(
                    article.title.containsIgnoreCase(filter.keyword())
                            .or(article.content.containsIgnoreCase(filter.keyword()))
            );
        }

        if (hasText(filter.authorUsername())) {
            builder.and(
                    article.author.username.equalsIgnoreCase(filter.authorUsername())
            );
        }

        if (filter.tags() != null && !filter.tags().isEmpty()) {
            builder.and(article.tags.any().name.in(filter.tags()));
        }

        if (filter.published() != null) {
            builder.and(article.published.eq(filter.published()));
        } else {
            builder.and(article.published.isTrue());
        }

        if (filter.createdAfter() != null) {
            builder.and(article.createdAt.after(filter.createdAfter()));
        }

        if (filter.createdBefore() != null) {
            builder.and(article.createdAt.before(filter.createdBefore()));
        }

        if (Boolean.TRUE.equals(filter.hasCoverImage())) {
            builder.and(
                    article.coverImageUrl.isNotNull()
                            .and(article.coverImageUrl.isNotEmpty())
            );
        }

        return builder;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
