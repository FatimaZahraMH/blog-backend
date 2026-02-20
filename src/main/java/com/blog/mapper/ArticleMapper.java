package com.blog.mapper;

import com.blog.dto.response.ArticleResponse;
import com.blog.entity.Article;
import com.blog.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ArticleMapper {

    @Mapping(target = "tags", expression = "java(mapTags(article.getTags()))")
    @Mapping(target = "commentCount", expression = "java(article.getComments().size())")
    @Mapping(target = "coverImageUrl", source = "coverImageUrl")
    ArticleResponse toResponse(Article article);

    default List<String> mapTags(List<Tag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                   .map(Tag::getName)
                   .collect(Collectors.toList());
    }
}
