package com.blog.mapper;

import com.blog.dto.response.CommentResponse;
import com.blog.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "articleId", source = "article.id")
    CommentResponse toResponse(Comment comment);
}
