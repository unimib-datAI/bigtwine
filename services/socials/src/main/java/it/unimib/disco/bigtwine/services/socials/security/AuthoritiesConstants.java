package it.unimib.disco.bigtwine.services.socials.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ANALYSIS_ADMIN = "ROLE_ANALYSIS_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String USER_PRO = "ROLE_USER_PRO";

    public static final String USER_SOCIAL = "ROLE_USER_SOCIAL";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}
