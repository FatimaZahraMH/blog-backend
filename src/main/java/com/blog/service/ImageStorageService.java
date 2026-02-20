package com.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageStorageService {


    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    @Value("${app.upload.base-url:http://localhost:8080/images}")
    private String baseUrl;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024;

    @PostConstruct
    public void init() {
        try {

            Files.createDirectories(Paths.get(uploadDir));
            log.info("Dossier d'upload initialisé : {}", uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload", e);
        }
    }


    public String store(MultipartFile file) {

        validateFile(file);


        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension    = getExtension(originalName);
        String uniqueName   = UUID.randomUUID().toString() + "." + extension;


        try {
            Path targetPath = Paths.get(uploadDir).resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Image sauvegardée : {}", uniqueName);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
        }


        return baseUrl + "/" + uniqueName;
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        Path filePath   = Paths.get(uploadDir).resolve(fileName);

        try {
            Files.deleteIfExists(filePath);
            log.info("Image supprimée : {}", fileName);
        } catch (IOException e) {
            log.warn("Impossible de supprimer l'image : {}", fileName);
        }
    }


    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                "Type de fichier non autorisé. Formats acceptés : JPEG, PNG, GIF, WEBP"
            );
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException(
                "Le fichier est trop grand. Taille max : 5 MB"
            );
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex + 1).toLowerCase() : "jpg";
    }
}
