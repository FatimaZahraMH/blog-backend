package com.blog.constants;

public final class ApiRoutes {

    private ApiRoutes() {}

    // Base
    public static final String API = "/api";

    // Articles
    public static final String ARTICLES        = API + "/articles";
    public static final String ARTICLES_ID     = "/{id}";
    public static final String ARTICLES_SLUG   = "/slug/{slug}";
    public static final String ARTICLES_AUTHOR = "/author/{authorId}";
    public static final String ARTICLES_SEARCH = "/search";
    public static final String ARTICLES_COVER  = "/{id}/cover-image";

    // Auth
    public static final String AUTH          = API + "/auth";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_LOGIN    = "/login";

    // Comments
    public static final String COMMENTS         = API + "/comments";
    public static final String COMMENTS_ID      = "/comments/{commentId}";
    public static final String ARTICLE_COMMENTS = "/articles/{articleId}/comments";
}