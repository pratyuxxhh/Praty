package praty.springx.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectLocationGuardTest {

    @TempDir
    Path tempDir;

    @Test
    void blocksCreatingInsidePratyCheckout() throws Exception {
        Path checkout = tempDir.resolve("PRATY");
        Files.createDirectories(checkout.resolve("src/main/java/praty"));
        Files.writeString(checkout.resolve("pom.xml"), "<project/>");

        ProjectSpec spec = ProjectSpec.builder()
                .projectName("my-blog")
                .targetDirectory(checkout.resolve("my-blog").toString())
                .packageName("com.example.blog")
                .groupId("com.example")
                .artifactId("my-blog")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .build();

        assertTrue(ProjectLocationGuard.validateTargetLocation(spec, checkout).isFail());
    }

    @Test
    void allowsCreatingSiblingProjectOutsideCheckout() throws Exception {
        Path parent = tempDir.resolve("programs");
        Path checkout = parent.resolve("PRATY");
        Files.createDirectories(checkout.resolve("src/main/java/praty"));
        Files.writeString(checkout.resolve("pom.xml"), "<project/>");

        ProjectSpec spec = ProjectSpec.builder()
                .projectName("my-blog")
                .targetDirectory(parent.resolve("my-blog").toString())
                .packageName("com.example.blog")
                .groupId("com.example")
                .artifactId("my-blog")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .build();

        assertTrue(ProjectLocationGuard.validateTargetLocation(spec, parent).isOk());
    }
}
