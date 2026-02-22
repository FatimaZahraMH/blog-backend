package com.blog.controller;

import com.blog.constants.ApiRoutes;
import com.blog.constants.SecuriteConstantes;
import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleSearchFilter;
import com.blog.dto.request.PageParametres;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.PageResponse;
import com.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiRoutes.ARTICLES)
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Gestion des articles du blog")
public class ArticleController {

    private final ArticleService articleService;


    @GetMapping(ApiRoutes.ARTICLES_ID)
    @Operation(summary = "Récupérer un article par ID")
    public ResponseEntity<ArticleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping(ApiRoutes.ARTICLES_SLUG)
    @Operation(summary = "Récupérer un article par slug (URL)")
    public ResponseEntity<ArticleResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticleBySlug(slug));
    }

    @GetMapping(ApiRoutes.ARTICLES_AUTHOR)
    @Operation(summary = "Articles d'un auteur avec pagination")
    public ResponseEntity<PageResponse<ArticleResponse>> getByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId, page, size));
    }


    @GetMapping(ApiRoutes.ARTICLES_SEARCH)
    @Operation(summary = "Recherche avancée d'articles")
    public PageResponse<ArticleResponse> search(
            @ParameterObject @ModelAttribute @Valid ArticleSearchFilter filter,
            @ParameterObject @ModelAttribute @Valid PageParametres page) {

        return articleService.searchArticles(filter, page);
    }

    @PostMapping
    @PreAuthorize(SecuriteConstantes.AUTEUR_OU_ADMIN)
    @Operation(summary = "Créer un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> create(
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize(SecuriteConstantes.AUTEUR_OU_ADMIN)
    @Operation(summary = "Modifier un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                articleService.updateArticle(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(SecuriteConstantes.AUTEUR_OU_ADMIN)
    @Operation(summary = "Supprimer un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }



    @PostMapping(value = ApiRoutes.ARTICLES_COVER,
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(SecuriteConstantes.AUTEUR_OU_ADMIN)
    @Operation(summary = "Uploader une image de couverture",
               description = "Formats : JPEG, PNG, GIF, WEBP — Max : 5 MB",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> uploadCoverImage(
            @PathVariable Long id,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                articleService.uploadCoverImage(id, image, userDetails.getUsername()));
    }

    @DeleteMapping(ApiRoutes.ARTICLES_COVER)
    @PreAuthorize(SecuriteConstantes.AUTEUR_OU_ADMIN)
    @Operation(summary = "Supprimer l'image de couverture",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> removeCoverImage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                articleService.removeCoverImage(id, userDetails.getUsername()));
    }
}
