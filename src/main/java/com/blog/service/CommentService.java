package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.PageResponse;

public interface CommentService {
    CommentResponse addComment(Long articleId, CommentRequest request, String username);
    CommentResponse updateComment(Long commentId, CommentRequest request, String username);
    void deleteComment(Long commentId, String username);
    PageResponse<CommentResponse> getCommentsByArticle(Long articleId, int page, int size);
}
