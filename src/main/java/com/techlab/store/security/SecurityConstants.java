package com.techlab.store.security;

public final class SecurityConstants {

    // 1. Constructor privado para evitar que la clase sea instanciada
    private SecurityConstants() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no debe ser instanciada");
    }

    // 2. Tiempos de Expiración (Sincronizados)
    public static final long JWT_EXPIRATION_IN_SECONDS = 3600; // 1 Hora
    public static final long JWT_EXPIRATION_IN_MS = JWT_EXPIRATION_IN_SECONDS * 1000;
    public static final long COOKIE_EXPIRATION_IN_MS = JWT_EXPIRATION_IN_MS + 3600 * 24; 

    // 3. Cookies
    public static final String COOKIE_ACCESS_TOKEN_NAME = "accessToken";
    public static final String COOKIE_PATH = "/";

    // 4. Headers & JWT
    public static final String TOKEN_HEADER_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CLAIM_ROLES = "roles";

    // 5. Atributos de Request (para el Filtro y EntryPoint)
    public static final String ATTR_JWT_EXCEPTION = "jwt_exception";

    // 6. Roles por defecto
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String DEFAULT_ROLE = "ROLE_CLIENT";
}