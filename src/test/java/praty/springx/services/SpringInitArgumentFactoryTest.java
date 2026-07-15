package praty.springx.services;

import org.junit.jupiter.api.Test;
import praty.springx.model.BuildTool;
import praty.springx.model.DependencyRef;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringInitArgumentFactoryTest {

    @Test
    void mapsProjectSpecToSpringInitArgs() {
        ProjectSpec spec = ProjectSpec.builder()
                .projectName("my-blog")
                .packageName("com.example.blog")
                .groupId("com.example")
                .artifactId("my-blog")
                .javaVersion(JavaVersion.JAVA_21)
                .buildTool(BuildTool.MAVEN)
                .packaging(Packaging.JAR)
                .bootVersion("3.4.1")
                .language(Language.JAVA)
                .dependencies(List.of(new DependencyRef("web"), new DependencyRef("data-jpa")))
                .targetDirectory("my-blog")
                .build();

        List<String> args = SpringInitArgumentFactory.toSpringInitArgs(spec);

        assertTrue(args.contains("init"));
        assertTrue(args.contains("--build=maven"));
        assertTrue(args.contains("--boot-version=3.4.1"));
        assertTrue(args.contains("--dependencies=web,data-jpa"));
        assertTrue(args.contains("--type=maven-project"));
        assertTrue(args.contains("my-blog"));
    }
}
