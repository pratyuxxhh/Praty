package praty.springx.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.cache.FileMetadataCache;
import praty.springx.config.JsonConfigStore;
import praty.springx.config.SpringxConfig;
import praty.springx.generator.ZipProjectGenerator;
import praty.springx.model.JavaVersion;
import praty.springx.network.HttpInitializrClient;
import praty.springx.plugin.NoopPluginRegistry;
import praty.springx.theme.ThemeRegistry;
import praty.springx.validation.DefaultProjectValidator;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppContainerTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        AppContainer.reset();
    }

    @Test
    void wiresCollaboratorsAndPersistsConfig() {
        Path configFile = tempDir.resolve("config.json");
        Path cacheFile = tempDir.resolve("cache.json");

        AppContainer container = AppContainer.create(
                new JsonConfigStore(configFile),
                new FileMetadataCache(cacheFile),
                new HttpInitializrClient(),
                new ZipProjectGenerator(),
                new DefaultProjectValidator(),
                new ThemeRegistry(),
                new NoopPluginRegistry(),
                new ErrorReporter()
        );

        assertTrue(container.projectValidator().validateProjectName("demo").isOk());
        assertEquals("default", container.theme().name());
        assertNotNull(container.projectCreateService());
        assertNotNull(container.metadataService());

        SpringxConfig updated = new SpringxConfig(
                "default",
                JavaVersion.JAVA_17,
                container.config().defaultBuildTool(),
                "com.acme",
                "",
                container.config().favorites(),
                container.config().recentProjects(),
                container.config().usage(),
                false,
                false
        );
        assertTrue(container.saveConfig(updated).isOk());
        assertEquals(JavaVersion.JAVA_17, container.config().defaultJavaVersion());

        AppContainer.install(container);
        assertSame(container, AppContainer.get());
    }
}
