package praty.springx.dependency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.model.DependencyRef;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenBuildFileEditorTest {

    @TempDir
    Path tempDir;

    @Test
    void addsAndRemovesStarterDependency() throws Exception {
        Path pom = tempDir.resolve("pom.xml");
        Files.writeString(pom, """
                <project>
                  <dependencies>
                    <dependency>
                      <groupId>org.springframework.boot</groupId>
                      <artifactId>spring-boot-starter</artifactId>
                    </dependency>
                  </dependencies>
                </project>
                """);

        MavenBuildFileEditor editor = new MavenBuildFileEditor();
        assertTrue(editor.add(tempDir, List.of(new DependencyRef("web"))).isOk());
        String afterAdd = Files.readString(pom);
        assertTrue(afterAdd.contains("spring-boot-starter-web"));

        assertTrue(editor.remove(tempDir, List.of(new DependencyRef("web"))).isOk());
        String afterRemove = Files.readString(pom);
        assertFalse(afterRemove.contains("spring-boot-starter-web"));
        assertTrue(afterRemove.contains("spring-boot-starter"));
    }
}
