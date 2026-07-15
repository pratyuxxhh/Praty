package praty.springx.services;

import praty.springx.config.SpringxConfig;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies user defaults when presenting wizard choices.
 */
public final class WizardDefaults {

    private WizardDefaults() {
    }

    public static List<JavaVersion> javaVersions(SpringxConfig config) {
        return prioritize(config.defaultJavaVersion(), List.of(JavaVersion.values()));
    }

    public static List<BuildTool> buildTools(SpringxConfig config) {
        return prioritize(config.defaultBuildTool(), List.of(BuildTool.values()));
    }

    public static List<String> bootVersions(SpringxConfig config, List<String> available, String metadataHint) {
        String preferred = config.preferredBootVersion();
        if (preferred == null || preferred.isBlank()) {
            preferred = metadataHint == null ? "" : metadataHint;
        }
        return prioritize(preferred, available);
    }

    public static SpringxConfig recordRecentProject(SpringxConfig config, String projectPath) {
        List<String> recent = new ArrayList<>();
        recent.add(projectPath);
        for (String existing : config.recentProjects()) {
            if (!existing.equals(projectPath)) {
                recent.add(existing);
            }
            if (recent.size() >= 10) {
                break;
            }
        }
        return new SpringxConfig(
                config.theme(),
                config.defaultJavaVersion(),
                config.defaultBuildTool(),
                config.defaultPackagePrefix(),
                config.preferredBootVersion(),
                config.favorites(),
                recent,
                config.usage(),
                config.telemetryEnabled(),
                config.telemetryAsked()
        );
    }

    private static <T> List<T> prioritize(T preferred, List<T> all) {
        Set<T> ordered = new LinkedHashSet<>();
        if (preferred != null) {
            ordered.add(preferred);
        }
        ordered.addAll(all);
        return List.copyOf(ordered);
    }

    private static List<String> prioritize(String preferred, List<String> all) {
        Set<String> ordered = new LinkedHashSet<>();
        if (preferred != null && !preferred.isBlank()) {
            ordered.add(preferred);
            for (String version : all) {
                if (version.startsWith(preferred) || preferred.startsWith(version)) {
                    ordered.add(version);
                }
            }
        }
        ordered.addAll(all);
        return List.copyOf(ordered);
    }
}
