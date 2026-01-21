package EcoRevive;

public class Constants {
    // Window Settings
    public static final int WINDOW_WIDTH = 1100;
    public static final int WINDOW_HEIGHT = 800;
    public static final String APP_TITLE = "EcoRevive: Smart E-Waste Recycling (JavaFX)";
    
    // CSS Classes
    public static final String STYLE_ROOT = "root";
    public static final String STYLE_NAV_BAR = "nav-bar";
    public static final String STYLE_CARD = "card";
    public static final String STYLE_HEADER_LABEL = "header-label";
    public static final String STYLE_STATUS_BADGE = "status-badge";
    public static final String STYLE_LIGHT_THEME = "light-theme";
    public static final String STYLE_CUSTOM_DIALOG = "custom-dialog";

    // Categories
    public static final String CAT_MOBILE = "Mobile";
    public static final String CAT_LAPTOP = "Laptop";
    public static final String CAT_TABLET = "Tablet";
    public static final String CAT_TV = "TV";
    public static final String CAT_APPLIANCE = "Appliance";
    public static final String CAT_OTHER = "Other";

    // Conditions
    public static final String COND_NEW = "New";
    public static final String COND_USED = "Used";
    public static final String COND_BROKEN = "Broken";
    public static final String COND_REFURBISHED = "Refurbished";

    // Eco Points Scoring
    public static final int POINTS_HIGH_VALUE = 50;
    public static final int POINTS_MEDIUM_VALUE = 20;
    public static final int POINTS_STANDARD = 10;
    public static final int POINTS_PER_KG = 5;
    
    public static final double MULTIPLIER_NEW = 2.0;
    public static final double MULTIPLIER_REFURBISHED = 1.5;
    public static final double MULTIPLIER_USED = 1.0;
    public static final double MULTIPLIER_BROKEN = 0.5;

    private Constants() {
        // Prevent instantiation
    }
}
