package com.blog.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

public record PageParametres(

        @Parameter(description = "Numéro de page (0-based)")
        @Min(0)
        @DefaultValue("0")
        Integer page,

        @Parameter(description = "Taille de page (max 100)")
        @Min(1) @Max(100)
        @DefaultValue("10")
        Integer taille,

        @Parameter(description = "Champ de tri")
        @DefaultValue("createdAt")
        String trierPar ){
    public PageParametres {
        if (page == null || page < 0)      page = 0;
        if (taille == null || taille < 1)  taille = 10;
        if (taille > 100)                  taille = 100;

        Set<String> champsAutorises = Set.of("createdAt", "updatedAt", "title", "published");
        if (trierPar == null || !champsAutorises.contains(trierPar)) {
            trierPar = "createdAt";
        }
    }
    public static PageParametres defaut() {
        return new PageParametres(0, 10, "createdAt");
    }
}