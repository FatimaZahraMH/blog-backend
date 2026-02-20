package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String title;

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;

    @Size(max = 500, message = "Le résumé ne peut pas dépasser 500 caractères")
    private String summary;

    private boolean published = false;

    private List<String> tags;
}
