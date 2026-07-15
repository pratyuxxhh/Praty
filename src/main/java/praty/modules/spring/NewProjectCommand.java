package praty.modules.spring;

import praty.command.Command;
import praty.command.CommandContext;
import praty.springx.commands.SetupCommand;

/**
 * Public create entry: {@code praty spring setup}.
 *
 * <p>Delegates to {@link SetupCommand} (JLine wizard + Initializr HTTP API).
 * See {@code docs/springx/ARCHITECTURE.md}.
 */
public class NewProjectCommand implements Command {

    private final SetupCommand setupCommand = new SetupCommand();

    @Override
    public void execute(CommandContext ctx) {
        setupCommand.execute(ctx);
    }

    /**
     * Kept for existing unit tests that assert argument building.
     */
    public static java.util.List<String> buildSpringInitArguments(ProjectConfig config) {
        java.util.List<String> args = new java.util.ArrayList<>();
        args.add("init");
        addOption(args, "--build", config.buildSystem);
        addOption(args, "--language", config.language);
        addOption(args, "--packaging", config.packaging);
        addOption(args, "--java-version", config.javaVersion);
        addOption(args, "--group-id", config.groupId);
        addOption(args, "--artifact-id", config.artifactId);
        addOption(args, "--name", config.projectName);
        addOption(args, "--description", config.description);
        addOption(args, "--version", config.version);
        addOption(args, "--package-name", config.packageName);
        addOption(args, "--type", config.projectType);
        if (config.dependencies != null && !config.dependencies.isEmpty()) {
            args.add("--dependencies=" + String.join(",", config.dependencies));
        }
        args.add(config.targetDirectory == null || config.targetDirectory.isBlank()
                ? config.projectName
                : config.targetDirectory);
        return args;
    }

    private static void addOption(java.util.List<String> args, String flag, String value) {
        if (value != null && !value.isBlank()) {
            args.add(flag + "=" + value);
        }
    }

    public static final class ProjectConfig {
        public String projectName = "demo-app";
        public String groupId = "com.example";
        public String artifactId = "demo-app";
        public String buildSystem = "maven";
        public String language = "java";
        public String packaging = "jar";
        public String javaVersion = "21";
        public String description = "Demo Spring Boot application";
        public String version = "0.0.1-SNAPSHOT";
        public String projectType = "maven-project";
        public String targetDirectory = "demo-app";
        public java.util.List<String> dependencies = java.util.List.of();
        public String packageName = "com.example.demoapp";
    }
}
