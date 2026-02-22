package com.blog.controller;

import com.blog.constants.ApiRoutes;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.PageResponse;
import com.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.API)
@RequiredArgsConstructor
@Tag(name = "Commentaires", description = "Gestion des commentaires")
public class CommentController {

    private final CommentService commentService;

    @GetMapping(ApiRoutes.ARTICLE_COMMENTS)
    @Operation(summary = "Récupérer les commentaires d'un article")
    public ResponseEntity<PageResponse<CommentResponse>> getByArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId, page, size));
    }

    @PostMapping(ApiRoutes.ARTICLE_COMMENTS)
    @Operation(summary = "Ajouter un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(articleId, request, userDetails.getUsername()));
    }

    @PutMapping(ApiRoutes.COMMENTS_ID)
    @Operation(summary = "Modifier un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                commentService.updateComment(commentId, request, userDetails.getUsername()));
    }

    @DeleteMapping(ApiRoutes.COMMENTS_ID)
    @Operation(summary = "Supprimer un commentaire", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
