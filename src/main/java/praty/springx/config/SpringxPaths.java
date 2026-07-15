package praty.springx.config;

import java.nio.file.Path;

/**
 * Well-known paths for springx under ~/.praty/springx/.
 */
public final class SpringxPaths {

    private SpringxPaths() {
    }

    public static Path home() {
        return Path.of(System.getProperty("user.home"), ".praty", "springx");
    }

    public static Path configFile() {
        return home().resolve("config.json");
    }

    public static Path cacheDir() {
        return home().resolve("cache");
    }

    public static Path initializrMetadataCache() {
        return cacheDir().resolve("initializr-metadata.json");
    }
}
