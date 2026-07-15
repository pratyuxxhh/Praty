package praty.springx.network;

import org.junit.jupiter.api.Test;
import praty.springx.model.BuildTool;
import praty.springx.model.DependencyRef;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpInitializrClientTest {

    @Test
    void formBodyUsesMavenProjectTypeForMavenSelection() {
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("My App")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("4.0.0")
                .language(Language.JAVA)
                .dependencies(List.of(new DependencyRef("web"), new DependencyRef("actuator")))
                .build();

        String body = HttpInitializrClient.buildStarterFormBody(spec);

        assertTrue(body.contains("type=maven-project"));
        assertTrue(body.contains("dependencies=web%2Cactuator"));
        assertTrue(body.contains("bootVersion=4.0.0"));
        assertTrue(body.contains("baseDir=my-app"));
        assertTrue(body.contains("artifactId=my-app"));
    }

    @Test
    void formBodyUsesGradleProjectTypeForGradleSelection() {
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("my-app")
                .packageName("com.example.myapp")
                .groupId("com.example")
                .artifactId("my-app")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.GRADLE)
                .packaging(Packaging.JAR)
                .bootVersion("4.0.0")
                .language(Language.JAVA)
                .build();

        String body = HttpInitializrClient.buildStarterFormBody(spec);

        assertTrue(body.contains("type=gradle-project"));
    }
}
