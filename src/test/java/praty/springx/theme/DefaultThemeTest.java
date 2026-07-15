package praty.springx.theme;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultThemeTest {

    @Test
    void wrapsWithAnsiWhenEnabled() {
        DefaultTheme theme = new DefaultTheme(true);
        String colored = theme.success("ok");
        assertTrue(colored.contains("ok"));
        assertTrue(colored.contains("\u001B["));
    }

    @Test
    void skipsAnsiWhenDisabled() {
        DefaultTheme theme = new DefaultTheme(false);
        assertFalse(theme.error("boom").contains("\u001B["));
        assertTrue(theme.error("boom").equals("boom"));
    }
}
