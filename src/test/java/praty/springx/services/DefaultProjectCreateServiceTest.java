package praty.springx.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.core.Result;
import praty.springx.generator.ZipProjectGenerator;
import praty.springx.model.BuildTool;
import praty.springx.model.DependencyRef;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;
import praty.springx.network.InitializrClient;
import praty.springx.network.InitializrMetadata;
import praty.springx.plugin.NoopPluginRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultProjectCreateServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void downloadsAndExtractsProject() throws IOException {
        Path target = tempDir.resolve("work/demo-app");
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("demo-app")
                .targetDirectory(target.toString())
                .packageName("com.example.demo")
                .groupId("com.example")
                .artifactId("demo-app")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .dependencies(List.of(new DependencyRef("web")))
                .build();

        InitializrClient client = new InitializrClient() {
            @Override
            public Result<InitializrMetadata> fetchMetadata() {
                return Result.ok(InitializrMetadata.empty());
            }

            @Override
            public Result<Path> downloadProject(ProjectSpec request) {
                try {
                    Path zip = tempDir.resolve("starter.zip");
                    writeZip(zip, "demo-app/pom.xml", "<project/>");
                    return Result.ok(zip);
                } catch (IOException e) {
                    return Result.fail("Unable to create zip.", e.getMessage(), "");
                }
            }
        };

        DefaultProjectCreateService service = new DefaultProjectCreateService(
                client,
                new ZipProjectGenerator(),
                new NoopPluginRegistry()
        );

        Result<?> created = service.create(spec);
        assertTrue(created.isOk());
        assertTrue(Files.exists(target.resolve("pom.xml")));
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
