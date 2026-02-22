package com.blog.service;

import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleSearchFilter;
import com.blog.dto.request.PageParametres;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    ArticleResponse createArticle(ArticleRequest request, String username);
    ArticleResponse updateArticle(Long id, ArticleRequest request, String username);
    void deleteArticle(Long id, String username);
    ArticleResponse getArticleById(Long id);
    ArticleResponse getArticleBySlug(String slug);


    PageResponse<ArticleResponse> searchArticles(
            ArticleSearchFilter filter, PageParametres page);


    PageResponse<ArticleResponse> getArticlesByAuthor(Long authorId, int page, int size);


    ArticleResponse uploadCoverImage(Long articleId, MultipartFile image, String username);


    ArticleResponse removeCoverImage(Long articleId, String username);
}
