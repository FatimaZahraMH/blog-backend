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

## 🔌 Endpoints principaux

###  Endpoints publics (sans authentification)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/articles/**` | Consultation des articles |
| `GET` | `/api/comments/**` | Consultation des commentaires |
| `GET` | `/images/**` | Accès aux images uploadées |
| `POST` | `/api/auth/**` | Authentification (login, register, refresh) |
| `GET` | `/api-docs/**` | Documentation OpenAPI |
| `GET` | `/swagger-ui/**` | Interface Swagger UI |
| `GET` | `/swagger-ui.html` | Page Swagger UI |

###  Commentaires (utilisateurs authentifiés)
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `POST` | `/api/articles/{id}/comments` | Ajouter un commentaire | USER, AUTHOR, ADMIN |
| `PUT` | `/api/comments/{id}` | Modifier un commentaire | USER, AUTHOR, ADMIN |
| `DELETE` | `/api/comments/{id}` | Supprimer un commentaire | USER, AUTHOR, ADMIN |

###  Articles (AUTHOR et ADMIN uniquement)
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `POST` | `/api/articles` | Créer un nouvel article | AUTHOR, ADMIN |
| `POST` | `/api/articles/{id}/cover-image` | Uploader une image de couverture | AUTHOR, ADMIN |
| `PUT` | `/api/articles/{id}` | Modifier un article | AUTHOR, ADMIN |
| `DELETE` | `/api/articles/{id}` | Supprimer un article | AUTHOR, ADMIN |

###  Administration (ADMIN uniquement)
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `*` | `/api/admin/**` | Toutes les opérations d'administration | ADMIN |

###  Autres endpoints
| Méthode | Endpoint | Description | Rôle requis |
|---------|----------|-------------|-------------|
| `*` | `/**` | Tous les autres endpoints non listés | Authentification requise |


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

Créez un fichier `application.yml` à la racine du projet :

```yaml
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
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true

jwt:
  secret: ton_secret_jwt                # Remplace par une clé aléatoire pour JWT
  expiration: 86400000                   # 24 heures en millisecondes
  refresh-expiration: 604800000          # 7 jours en millisecondes

app:
  upload:
    dir: uploads/images
    base-url: http://localhost:8080/images
