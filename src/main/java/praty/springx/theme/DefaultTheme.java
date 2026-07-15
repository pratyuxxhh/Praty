package praty.springx.theme;

/**
 * Default ANSI theme: blue info, green success, yellow warn, red error, gray secondary.
 */
public final class DefaultTheme implements Theme {

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[90m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";

    private final boolean colorEnabled;

    public DefaultTheme() {
        this(true);
    }

    public DefaultTheme(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public String info(String text) {
        return color(BLUE, text);
    }

    @Override
    public String success(String text) {
        return color(GREEN, text);
    }

    @Override
    public String warn(String text) {
        return color(YELLOW, text);
    }

    @Override
    public String error(String text) {
        return color(RED, text);
    }

    @Override
    public String secondary(String text) {
        return color(GRAY, text);
    }

    @Override
    public String accent(String text) {
        return color(CYAN + BOLD, text);
    }

    @Override
    public String iconSuccess() {
        return color(GREEN, "✔");
    }

    @Override
    public String iconError() {
        return color(RED, "✖");
    }

    @Override
    public String iconWarn() {
        return color(YELLOW, "⚠");
    }

    @Override
    public String iconInfo() {
        return color(BLUE, "ℹ");
    }

    @Override
    public String divider() {
        return color(GRAY, "────────────────────────────────────────");
    }

    private String color(String code, String text) {
        if (!colorEnabled || text == null) {
            return text;
        }
        return code + text + RESET;
    }
}
