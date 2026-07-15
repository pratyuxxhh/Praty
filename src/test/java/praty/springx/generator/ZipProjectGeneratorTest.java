package praty.springx.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZipProjectGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void extractsNestedProjectZip() throws IOException {
        Path workDir = tempDir.resolve("work");
        Files.createDirectories(workDir);
        Path target = workDir.resolve("demo-app");
        Path zip = tempDir.resolve("nested.zip");
        writeZip(zip, "demo-app/pom.xml", "<project/>");

        ProjectSpec spec = projectSpec(target, "demo-app");
        ZipProjectGenerator generator = new ZipProjectGenerator();
        var result = generator.extract(zip, spec);
        assertTrue(result.isOk());
        assertTrue(Files.exists(target.resolve("pom.xml")));
        assertEquals(target.normalize(), result.get().projectDirectory().normalize());
    }

    @Test
    void extractsFlatProjectZipIntoNamedFolder() throws IOException {
        Path workDir = tempDir.resolve("work");
        Files.createDirectories(workDir);
        Path target = workDir.resolve("my-blog");
        Path zip = tempDir.resolve("flat.zip");
        writeZip(zip, "build.gradle.kts", "plugins { java }");

        ProjectSpec spec = projectSpec(target, "my-blog");
        ZipProjectGenerator generator = new ZipProjectGenerator();
        var result = generator.extract(zip, spec);
        assertTrue(result.isOk());
        assertTrue(Files.exists(target.resolve("build.gradle.kts")));
        assertEquals(target.normalize(), result.get().projectDirectory().normalize());
    }

    @Test
    void findProjectRootHandlesFlatAndNestedLayouts() throws IOException {
        Path staging = tempDir.resolve("staging-flat");
        Files.createDirectories(staging);
        Files.writeString(staging.resolve("pom.xml"), "<project/>");
        assertEquals(staging, ZipProjectGenerator.findProjectRoot(staging, "demo"));

        Path nested = tempDir.resolve("staging-nested");
        Path nestedProject = nested.resolve("demo-app");
        Files.createDirectories(nestedProject);
        Files.writeString(nestedProject.resolve("pom.xml"), "<project/>");
        assertEquals(nestedProject, ZipProjectGenerator.findProjectRoot(nested, "demo-app"));
    }

    private static ProjectSpec projectSpec(Path target, String name) {
        return ProjectSpec.builder()
                .projectName(name)
                .targetDirectory(target.toString())
                .packageName("com.example.demo")
                .groupId("com.example")
                .artifactId(name)
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .build();
    }

    private static void writeZip(Path zip, String entryName, String content) throws IOException {
        try (OutputStream out = Files.newOutputStream(zip);
             ZipOutputStream zos = new ZipOutputStream(out)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(content.getBytes());
            zos.closeEntry();
        }
    }
}
