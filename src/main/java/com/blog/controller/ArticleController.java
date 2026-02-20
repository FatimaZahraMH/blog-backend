package com.blog.controller;

import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleSearchFilter;
import com.blog.dto.response.ArticleResponse;
import com.blog.dto.response.PageResponse;
import com.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Gestion des articles du blog")
public class ArticleController {

    private final ArticleService articleService;


    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un article par ID")
    public ResponseEntity<ArticleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Récupérer un article par slug (URL)")
    public ResponseEntity<ArticleResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticleBySlug(slug));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Articles d'un auteur avec pagination")
    public ResponseEntity<PageResponse<ArticleResponse>> getByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId, page, size));
    }


    @GetMapping("/search")
    @Operation(summary = "Recherche avancée d'articles",
               description = "Combinaison possible : keyword + tags + auteur + date + image")
    public ResponseEntity<PageResponse<ArticleResponse>> search(
            @Parameter(description = "Mot-clé dans titre/contenu/résumé")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Username de l'auteur")
            @RequestParam(required = false) String authorUsername,

            @Parameter(description = "Filtrer par tag(s)")
            @RequestParam(required = false) java.util.List<String> tags,

            @Parameter(description = "true = publiés, false = brouillons")
            @RequestParam(required = false) Boolean published,

            @Parameter(description = "Articles créés après (ISO 8601)")
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime createdAfter,

            @Parameter(description = "Articles créés avant (ISO 8601)")
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            java.time.LocalDateTime createdBefore,

            @Parameter(description = "true = seulement articles avec image de couverture")
            @RequestParam(required = false) Boolean hasCoverImage,

            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(defaultValue = "createdAt")  String sortBy) {


        ArticleSearchFilter filter = new ArticleSearchFilter();
        filter.setKeyword(keyword);
        filter.setAuthorUsername(authorUsername);
        filter.setTags(tags);
        filter.setPublished(published);
        filter.setCreatedAfter(createdAfter);
        filter.setCreatedBefore(createdBefore);
        filter.setHasCoverImage(hasCoverImage);

        return ResponseEntity.ok(articleService.searchArticles(filter, page, size, sortBy));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @Operation(summary = "Créer un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> create(
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @Operation(summary = "Modifier un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                articleService.updateArticle(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @Operation(summary = "Supprimer un article", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }



    @PostMapping(value = "/{id}/cover-image",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
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

    @DeleteMapping("/{id}/cover-image")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @Operation(summary = "Supprimer l'image de couverture",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ArticleResponse> removeCoverImage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                articleService.removeCoverImage(id, userDetails.getUsername()));
    }
}
