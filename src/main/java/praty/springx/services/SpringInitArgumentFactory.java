package praty.springx.services;

import praty.springx.model.BuildTool;
import praty.springx.model.DependencyRef;
import praty.springx.model.ProjectSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Maps {@link ProjectSpec} to legacy Spring CLI {@code spring init} arguments (tests only).
 */
public final class SpringInitArgumentFactory {

    private SpringInitArgumentFactory() {
    }

    public static List<String> toSpringInitArgs(ProjectSpec spec) {
        List<String> args = new ArrayList<>();
        args.add("init");
        addOption(args, "--build", spec.buildTool().id());
        addOption(args, "--language", spec.language().id());
        addOption(args, "--packaging", spec.packaging().id());
        addOption(args, "--java-version", spec.javaVersion().id());
        addOption(args, "--group-id", spec.groupId());
        addOption(args, "--artifact-id", spec.artifactId());
        addOption(args, "--name", spec.projectName());
        addOption(args, "--description", spec.description());
        addOption(args, "--version", spec.version());
        addOption(args, "--package-name", spec.packageName());
        addOption(args, "--type", projectType(spec.buildTool()));
        if (spec.bootVersion() != null && !spec.bootVersion().isBlank()) {
            addOption(args, "--boot-version", spec.bootVersion());
        }
        if (!spec.dependencies().isEmpty()) {
            String deps = spec.dependencies().stream()
                    .map(DependencyRef::id)
                    .collect(Collectors.joining(","));
            args.add("--dependencies=" + deps);
        }
        String target = spec.effectiveTargetDirectory();
        args.add(target);
        return args;
    }

    private static String projectType(BuildTool buildTool) {
        return buildTool == BuildTool.GRADLE ? "gradle-project" : "maven-project";
    }

    private static void addOption(List<String> args, String flag, String value) {
        if (value != null && !value.isBlank()) {
            args.add(flag + "=" + value);
        }
    }

    public static List<String> springCommand(List<String> springArgs) {
        List<String> command = new ArrayList<>();
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            command.add("cmd.exe");
            command.add("/c");
            command.add("spring");
        } else {
            command.add("spring");
        }
        command.addAll(springArgs);
        return command;
    }
}
