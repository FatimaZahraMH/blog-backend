package com.blog.service.impl;

import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleSearchFilter;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.PageResponse;
import com.blog.entity.Article;
import com.blog.entity.Role;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.UnauthorizedException;
import com.blog.mapper.ArticleMapper;
import com.blog.repository.ArticlePredicate;
import com.blog.repository.ArticleRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.UserRepository;
import com.blog.service.ArticleService;
import com.blog.service.ImageStorageService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository   articleRepository;
    private final UserRepository      userRepository;
    private final TagRepository       tagRepository;
    private final ArticleMapper       articleMapper;
    private final ImageStorageService imageStorageService;

    @Override
    @Transactional
    public ArticleResponse createArticle(ArticleRequest request, String username) {
        User author    = getUserByUsername(username);
        List<Tag> tags = resolveTags(request.getTags());

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(generateUniqueSlug(request.getTitle()))
                .content(request.getContent())
                .summary(request.getSummary())
                .published(request.isPublished())
                .author(author)
                .tags(tags)
                .build();

        log.info("Article créé : '{}' par {}", article.getTitle(), username);
        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleResponse updateArticle(Long id, ArticleRequest request, String username) {
        Article article = getArticleOrThrow(id);
        checkOwnership(article, getUserByUsername(username));

        if (!article.getTitle().equals(request.getTitle())) {
            article.setSlug(generateUniqueSlug(request.getTitle()));
        }
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setPublished(request.isPublished());
        article.setTags(resolveTags(request.getTags()));

        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, String username) {
        Article article = getArticleOrThrow(id);
        checkOwnership(article, getUserByUsername(username));

        if (article.getCoverImageUrl() != null) {
            imageStorageService.delete(article.getCoverImageUrl());
        }
        articleRepository.delete(article);
        log.info("Article supprimé : id={} par {}", id, username);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long id) {
        return articleMapper.toResponse(getArticleOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleResponse getArticleBySlug(String slug) {
        return articleMapper.toResponse(
            articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "slug", slug))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> searchArticles(
            ArticleSearchFilter filter, int page, int size, String sortBy) {

        Pageable  pageable   = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Predicate predicate  = ArticlePredicate.build(filter);
        Page<Article> result = articleRepository.findAll(predicate, pageable);

        log.debug("Recherche articles -> {} résultats", result.getTotalElements());
        return toPageResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> getArticlesByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPageResponse(
                articleRepository.findByAuthorIdAndPublished(authorId, true, pageable)
        );
    }

    @Override
    @Transactional
    public ArticleResponse uploadCoverImage(Long articleId, MultipartFile image, String username) {
        Article article = getArticleOrThrow(articleId);
        checkOwnership(article, getUserByUsername(username));

        if (article.getCoverImageUrl() != null) {
            imageStorageService.delete(article.getCoverImageUrl());
        }

        String imageUrl = imageStorageService.store(image);
        article.setCoverImageUrl(imageUrl);

        log.info("Image de couverture mise à jour pour l'article id={}", articleId);
        return articleMapper.toResponse(articleRepository.save(article));
    }

    @Override
    @Transactional
    public ArticleResponse removeCoverImage(Long articleId, String username) {
        Article article = getArticleOrThrow(articleId);
        checkOwnership(article, getUserByUsername(username));

        imageStorageService.delete(article.getCoverImageUrl());
        article.setCoverImageUrl(null);

        return articleMapper.toResponse(articleRepository.save(article));
    }



    private Article getArticleOrThrow(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "username", username));
    }

    private void checkOwnership(Article article, User user) {
        if (user.getRole() != Role.ADMIN && !article.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cet article");
        }
    }

    private List<Tag> resolveTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) return new ArrayList<>();
        return tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build())))
                .toList();
    }

    private String generateUniqueSlug(String title) {
        String base = toSlug(title);
        String slug = base;
        int count   = 1;
        while (articleRepository.existsBySlug(slug)) slug = base + "-" + count++;
        return slug;
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized).replaceAll("")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "-");
    }

    private PageResponse<ArticleResponse> toPageResponse(Page<Article> page) {
        return PageResponse.<ArticleResponse>builder()
                .content(page.getContent().stream().map(articleMapper::toResponse).toList())
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .first(page.isFirst()).last(page.isLast())
                .build();
    }
}
