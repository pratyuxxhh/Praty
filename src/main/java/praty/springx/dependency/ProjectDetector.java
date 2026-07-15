package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects Spring Boot projects and scans installed Initializr-style dependencies.
 */
public final class ProjectDetector {

    private static final Pattern ARTIFACT = Pattern.compile("<artifactId>([^<]+)</artifactId>");
    private static final Pattern GRADLE_DEP = Pattern.compile(
            "(implementation|compileOnly|runtimeOnly|testImplementation)\\s+['\"]([^'\"]+)['\"]"
    );

    private ProjectDetector() {
    }

    public static Result<Path> detectProjectDirectory() {
        return detectProjectDirectory(Path.of("").toAbsolutePath().normalize());
    }

    public static Result<Path> detectProjectDirectory(Path start) {
        Path current = start.toAbsolutePath().normalize();
        for (int depth = 0; depth < 6; depth++) {
            if (Files.exists(current.resolve("pom.xml")) || Files.exists(current.resolve("build.gradle"))) {
                return Result.ok(current);
            }
            Path parent = current.getParent();
            if (parent == null) {
                break;
            }
            current = parent;
        }
        return Result.fail(new SpringxException(
                "No Spring Boot project found.",
                "Could not find pom.xml or build.gradle in the current directory.",
                "Run this command from inside a Spring Boot project."
        ));
    }

    public static BuildToolKind buildTool(Path projectDir) {
        if (Files.exists(projectDir.resolve("pom.xml"))) {
            return BuildToolKind.MAVEN;
        }
        if (Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"))) {
            return BuildToolKind.GRADLE;
        }
        return BuildToolKind.UNKNOWN;
    }

    public static List<DependencyRef> installedDependencies(Path projectDir) {
        return switch (buildTool(projectDir)) {
            case MAVEN -> scanMaven(projectDir.resolve("pom.xml"));
            case GRADLE -> scanGradle(projectDir.resolve("build.gradle"));
            default -> List.of();
        };
    }

    private static List<DependencyRef> scanMaven(Path pom) {
        if (!Files.exists(pom)) {
            return List.of();
        }
        try {
            String content = Files.readString(pom);
            String dependenciesSection = extractDependenciesSection(content);
            if (dependenciesSection.isBlank()) {
                return List.of();
            }
            Set<String> ids = new LinkedHashSet<>();
            Matcher matcher = ARTIFACT.matcher(dependenciesSection);
            while (matcher.find()) {
                StarterCoordinates.initializrIdForArtifact(matcher.group(1).trim())
                        .ifPresent(ids::add);
            }
            return ids.stream().map(DependencyRef::new).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private static List<DependencyRef> scanGradle(Path buildGradle) {
        if (!Files.exists(buildGradle)) {
            return List.of();
        }
        try {
            String content = Files.readString(buildGradle);
            Set<String> ids = new LinkedHashSet<>();
            Matcher matcher = GRADLE_DEP.matcher(content);
            while (matcher.find()) {
                String notation = matcher.group(2);
                String artifactId = notation.contains(":")
                        ? notation.substring(notation.lastIndexOf(':') + 1)
                        : notation;
                StarterCoordinates.initializrIdForArtifact(artifactId).ifPresent(ids::add);
            }
            return ids.stream().map(DependencyRef::new).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String extractDependenciesSection(String pom) {
        int start = pom.indexOf("<dependencies>");
        int end = pom.indexOf("</dependencies>");
        if (start < 0 || end < 0 || end <= start) {
            return "";
        }
        return pom.substring(start, end);
    }

    public enum BuildToolKind {
        MAVEN,
        GRADLE,
        UNKNOWN
    }
}
