package praty.modules.setup;

import org.junit.jupiter.api.Test;

import praty.modules.spring.NewProjectCommand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NewProjectCommandTest {

    @Test
    void buildSpringInitArgumentsIncludesSelectedOptions() {
        NewProjectCommand.ProjectConfig config = new NewProjectCommand.ProjectConfig();
        config.projectName = "demo-app";
        config.groupId = "com.example";
        config.artifactId = "demo-app";
        config.buildSystem = "maven";
        config.language = "java";
        config.packaging = "jar";
        config.javaVersion = "21";
        config.description = "Demo";
        config.version = "0.0.1-SNAPSHOT";
        config.dependencies = List.of("web", "data-jpa");
        config.projectType = "maven-project";
        config.targetDirectory = "demo-app";

        List<String> args = NewProjectCommand.buildSpringInitArguments(config);

        assertTrue(args.contains("init"));
        assertTrue(args.contains("--build=maven"));
        assertTrue(args.contains("--dependencies=web,data-jpa"));
        assertTrue(args.contains("--type=maven-project"));
        assertTrue(args.contains("demo-app"));
    }
}
