package com.blog.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


public record ArticleSearchFilter(

        @Parameter(description = "Mot-clé dans titre/contenu/résumé")
        @Size(min = 2, max = 100)
        String keyword,

        @Parameter(description = "Username de l'auteur")
        String authorUsername,

        @Parameter(description = "Filtrer par tag(s)")
        List<String> tags,

        @Parameter(description = "true = publiés, false = brouillons")
        Boolean published,

        @Parameter(description = "Articles créés après (ISO 8601)")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdAfter,

        @Parameter(description = "Articles créés avant (ISO 8601)")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdBefore,

        @Parameter(description = "true = avec image de couverture uniquement")
        Boolean hasCoverImage

) {}