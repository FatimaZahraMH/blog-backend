package com.blog.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String summary;
    private boolean published;
    private String coverImageUrl;
    private UserResponse author;
    private List<String> tags;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
