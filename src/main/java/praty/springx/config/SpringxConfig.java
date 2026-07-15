package praty.springx.config;

import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;

import java.util.List;
import java.util.Map;

/**
 * User preferences stored at ~/.praty/springx/config.json.
 */
public record SpringxConfig(
        String theme,
        JavaVersion defaultJavaVersion,
        BuildTool defaultBuildTool,
        String defaultPackagePrefix,
        String preferredBootVersion,
        List<String> favorites,
        List<String> recentProjects,
        Map<String, Integer> usage,
        boolean telemetryEnabled,
        boolean telemetryAsked
) {
    public SpringxConfig {
        favorites = favorites == null ? List.of() : List.copyOf(favorites);
        recentProjects = recentProjects == null ? List.of() : List.copyOf(recentProjects);
        usage = usage == null ? Map.of() : Map.copyOf(usage);
        theme = theme == null || theme.isBlank() ? "default" : theme;
        defaultPackagePrefix = defaultPackagePrefix == null ? "com.example" : defaultPackagePrefix;
        preferredBootVersion = preferredBootVersion == null ? "" : preferredBootVersion;
    }

    public static SpringxConfig defaults() {
        return new SpringxConfig(
                "default",
                JavaVersion.JAVA_21,
                BuildTool.MAVEN,
                "com.example",
                "",
                List.of(),
                List.of(),
                Map.of(),
                false,
                false
        );
    }
}
