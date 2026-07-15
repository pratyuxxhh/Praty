package praty.springx.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.core.Result;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonConfigStoreTest {

    @TempDir
    Path tempDir;

    @Test
    void loadCreatesDefaultsWhenMissing() {
        Path file = tempDir.resolve("config.json");
        JsonConfigStore store = new JsonConfigStore(file);

        Result<SpringxConfig> loaded = store.load();

        assertTrue(loaded.isOk());
        assertEquals(JavaVersion.JAVA_21, loaded.get().defaultJavaVersion());
        assertTrue(file.toFile().exists());
    }

    @Test
    void saveAndReloadRoundTrip() {
        Path file = tempDir.resolve("config.json");
        JsonConfigStore store = new JsonConfigStore(file);

        SpringxConfig config = new SpringxConfig(
                "default",
                JavaVersion.JAVA_17,
                BuildTool.GRADLE,
                "com.acme",
                "3.4.1",
                List.of("web"),
                List.of("/tmp/demo"),
                Map.of("web", 2),
                false,
                true
        );

        assertTrue(store.save(config).isOk());
        Result<SpringxConfig> loaded = store.load();

        assertTrue(loaded.isOk());
        SpringxConfig reloaded = loaded.get();
        assertEquals(JavaVersion.JAVA_17, reloaded.defaultJavaVersion());
        assertEquals(BuildTool.GRADLE, reloaded.defaultBuildTool());
        assertEquals("com.acme", reloaded.defaultPackagePrefix());
        assertEquals("3.4.1", reloaded.preferredBootVersion());
        assertEquals(List.of("web"), reloaded.favorites());
        assertEquals(2, reloaded.usage().get("web"));
        assertFalse(reloaded.telemetryEnabled());
        assertTrue(reloaded.telemetryAsked());
    }
}
