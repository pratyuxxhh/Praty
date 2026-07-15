package praty.springx.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.config.SpringxConfig;
import praty.springx.core.Result;
import praty.springx.dependency.CompositeBuildFileEditor;
import praty.springx.dependency.DependencyBrowser;
import praty.springx.dependency.CachedDependencyCatalog;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;
import praty.springx.network.InitializrMetadata;
import praty.springx.plugin.NoopPluginRegistry;
import praty.springx.terminal.ScriptedTerminalUi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyManagerServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void addsDependencyToMavenProject() throws Exception {
        Files.writeString(tempDir.resolve("pom.xml"), """
                <project>
                  <dependencies>
                    <dependency>
                      <groupId>org.springframework.boot</groupId>
                      <artifactId>spring-boot-starter</artifactId>
                    </dependency>
                  </dependencies>
                </project>
                """);

        InitializrMetadata metadata = new InitializrMetadata(
                "3.4.1",
                List.of("3.4.1"),
                List.of("21"),
                MockDependencyData.all(),
                System.currentTimeMillis()
        );

        CachedDependencyCatalog catalog = new CachedDependencyCatalog(metadata);
        List<Dependency> options = DependencyBrowser.forAdd(catalog, SpringxConfig.defaults(), List.of());
        int webIndex = 0;
        for (int i = 0; i < options.size(); i++) {
            if ("web".equals(options.get(i).id())) {
                webIndex = i;
                break;
            }
        }

        ScriptedTerminalUi ui = new ScriptedTerminalUi()
                .enqueueMultiSelect(List.of(webIndex))
                .enqueueConfirm(true);

        DependencyManagerService service = new DependencyManagerService(
                new CompositeBuildFileEditor(),
                new NoopPluginRegistry()
        );

        Result<List<DependencyRef>> result = service.addInteractive(
                ui,
                SpringxConfig.defaults(),
                metadata,
                tempDir
        );
        assertTrue(result.isOk());
        assertTrue(Files.readString(tempDir.resolve("pom.xml")).contains("spring-boot-starter-web"));
    }
}
