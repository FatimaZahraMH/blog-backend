package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(min = 2, max = 2000, message = "Le commentaire doit contenir entre 2 et 2000 caractères")
    private String content;
}
