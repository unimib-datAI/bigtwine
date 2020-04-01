package it.unimib.disco.bigtwine.services.analysis.config;

/**
 * Application constants.
 */
public final class Constants {

    // Db
    public static final String ANALYSIS_DB_COLLECTION = "analyses";
    public static final String ANALYSIS_RESULTS_DB_COLLECTION = "analyses.results";
    public static final String ANALYSIS_SETTINGS_DB_COLLECTION = "analyses.settings";
    public static final String ANALYSIS_DEFAULT_SETTINGS_DB_COLLECTION = "analyses.default_settings";
    public static final String ANALYSIS_SETTING_COLLECTION_DB_COLLECTION = "analyses.setting_collection";

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {
    }
}
