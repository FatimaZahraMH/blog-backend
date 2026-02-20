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


        if (hasText(filter.getKeyword())) {
            String kw = filter.getKeyword().toLowerCase();
            builder.and(
                article.title.containsIgnoreCase(kw)
                    .or(article.content.containsIgnoreCase(kw))
                    .or(article.summary.containsIgnoreCase(kw))
            );
        }


        if (hasText(filter.getAuthorUsername())) {
            builder.and(
                article.author.username.equalsIgnoreCase(filter.getAuthorUsername())
            );
        }


        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            builder.and(
                article.tags.any().name.in(filter.getTags())
            );
        }


        if (filter.getPublished() != null) {
            builder.and(article.published.eq(filter.getPublished()));
        } else {

            builder.and(article.published.isTrue());
        }


        if (filter.getCreatedAfter() != null) {
            builder.and(article.createdAt.after(filter.getCreatedAfter()));
        }


        if (filter.getCreatedBefore() != null) {
            builder.and(article.createdAt.before(filter.getCreatedBefore()));
        }


        if (Boolean.TRUE.equals(filter.getHasCoverImage())) {
            builder.and(article.coverImageUrl.isNotNull()
                    .and(article.coverImageUrl.isNotEmpty()));
        }

        return builder;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
