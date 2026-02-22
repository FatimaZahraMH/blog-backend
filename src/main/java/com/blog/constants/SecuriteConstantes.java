package com.blog.constants;



public final class SecuriteConstantes {

    private SecuriteConstantes() {}

    public static final String AUTEUR_OU_ADMIN = "hasAnyRole('AUTEUR', 'ADMIN')";
    public static final String ADMIN_SEULEMENT = "hasRole('ADMIN')";
    public static final String AUTHENTIFIE     = "isAuthenticated()";
}
