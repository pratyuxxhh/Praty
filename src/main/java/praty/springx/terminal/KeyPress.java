package praty.springx.terminal;

/**
 * Key press with optional character payload (for typing / search).
 */
public record KeyPress(KeyEvent event, char ch) {

    public static KeyPress of(KeyEvent event) {
        return new KeyPress(event, '\0');
    }

    public static KeyPress character(char ch) {
        return new KeyPress(KeyEvent.CHAR, ch);
    }
}
