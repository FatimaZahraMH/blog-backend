# Blog Backend - Spring Boot

Backend REST API pour une application de blog  avec Spring Boot, PostgreSQL et JWT.

---

## Technologies utilisées

- **Spring Boot 3.2.3** – Framework Java  
- **PostgreSQL** – Base de données  
- **Liquibase** – Migrations de schéma  
- **JWT (jjwt 0.12.5)** – Authentification  
- **MapStruct** – Mapping entités ↔ DTOs  
- **QueryDSL** – Requêtes dynamiques  
- **Swagger/OpenAPI** – Documentation API  

---

## Endpoints principaux

| Méthode | Endpoint | Description |
|---------|---------|-------------|
| GET     | `/api/v1/articles/search` | Recherche avancée |
| POST    | `/api/v1/articles` | Créer un article (ROLE: AUTHOR) |
| POST    | `/api/v1/articles/{id}/cover-image` | Upload d’une image de couverture |
| GET     | `/api/v1/articles/{id}/comments` | Récupérer les commentaires |

---

## Rôles

| Rôle   | Permissions |
|--------|-------------|
| USER   | Lire + commenter |
| AUTHOR | + Créer/modifier ses articles |
| ADMIN  | Tout gérer |

---

## Recherche avancée (QueryDSL)

Exemple de requête :  

GET /api/v1/articles/search?keyword=spring&tags=java&hasCoverImage=true
---
keyword - Recherche dans titre et contenu

tags - Filtrage par tags (séparés par des virgules)

hasCoverImage - Articles avec image de couverture 

authorId - Articles d'un auteur spécifique

## Configuration
Créez un fichier application.yml à la racine du projet :

server:
  port: 8080

spring:
  application:
    name: blog-backend

  datasource:
    url: jdbc:postgresql://localhost:5432/blogdb
    username: ton_utilisateur           # Remplace par ton username PostgreSQL
    password: ton_mot_de_passe          # Remplace par ton mot de passe
    driver-class-name: org.postgresql.Driver

spring:
  jpa:
    hibernate:
      ddl-auto: validate        
    show-sql: true              
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect  # Dialecte PostgreSQL
        format_sql: true       
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml  # Fichier de migration
    enabled: true

jwt:
  secret: ton_secret_jwt                # Remplace par une clé aléatoire pour JWT
  expiration: 86400000
  refresh-expiration: 604800000

app:
  upload:
    dir: uploads/images
    base-url: http://localhost:8080/images