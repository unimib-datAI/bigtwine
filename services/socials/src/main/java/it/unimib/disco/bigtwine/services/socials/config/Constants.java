package it.unimib.disco.bigtwine.services.socials.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";
    public static final String LOGIN_DISALLOWED_REGEX = "[^_.@A-Za-z0-9-]";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String GATEWAY_SERVICE_ID = "apigateway";
    
    private Constants() {
    }
}
