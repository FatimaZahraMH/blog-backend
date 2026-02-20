package com.blog.service.impl;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.PageResponse;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.Role;
import com.blog.entity.User;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.UnauthorizedException;
import com.blog.mapper.CommentMapper;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponse addComment(Long articleId, CommentRequest request, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "username", username));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .article(article)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Commentaire ajouté sur l'article {} par {}", articleId, username);
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, String username) {
        Comment comment = getCommentOrThrow(commentId);
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "username", username));
        checkOwnership(comment, currentUser);

        comment.setContent(request.getContent());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = getCommentOrThrow(commentId);
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "username", username));
        checkOwnership(comment, currentUser);
        commentRepository.delete(comment);
        log.info("Commentaire {} supprimé par {}", commentId, username);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsByArticle(Long articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByArticleId(articleId, pageable);
        return PageResponse.<CommentResponse>builder()
                .content(commentPage.getContent().stream().map(commentMapper::toResponse).toList())
                .page(commentPage.getNumber())
                .size(commentPage.getSize())
                .totalElements(commentPage.getTotalElements())
                .totalPages(commentPage.getTotalPages())
                .first(commentPage.isFirst())
                .last(commentPage.isLast())
                .build();
    }

    private Comment getCommentOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire", "id", id));
    }

    private void checkOwnership(Comment comment, User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = comment.getAuthor().getId().equals(user.getId());
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce commentaire");
        }
    }
}
