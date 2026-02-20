package com.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private UserResponse author;
    private Long articleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
