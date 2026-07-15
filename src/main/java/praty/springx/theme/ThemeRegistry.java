package praty.springx.theme;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves themes by name.
 */
public final class ThemeRegistry {

    private final Map<String, Theme> themes = new ConcurrentHashMap<>();

    public ThemeRegistry() {
        register(new DefaultTheme());
    }

    public void register(Theme theme) {
        themes.put(theme.name(), theme);
    }

    public Theme get(String name) {
        return themes.getOrDefault(name == null || name.isBlank() ? "default" : name, themes.get("default"));
    }

    public Optional<Theme> find(String name) {
        return Optional.ofNullable(themes.get(name));
    }
}
