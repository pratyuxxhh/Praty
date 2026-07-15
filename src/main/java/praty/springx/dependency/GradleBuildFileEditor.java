package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.DependencyRef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Edits {@code build.gradle} to add/remove Spring Boot starter dependencies.
 */
public final class GradleBuildFileEditor implements BuildFileEditor {

    @Override
    public Result<Void> add(Path projectDir, List<DependencyRef> deps) {
        Path buildGradle = projectDir.resolve("build.gradle");
        if (!Files.exists(buildGradle)) {
            return notGradle();
        }
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(buildGradle));
            int insertAt = findDependenciesInsertIndex(lines);
            if (insertAt < 0) {
                return Result.fail(new SpringxException(
                        "Unable to edit build.gradle.",
                        "No dependencies block found.",
                        "Add a dependencies { } block first."
                ));
            }
            String content = String.join("\n", lines);
            List<String> additions = new ArrayList<>();
            for (DependencyRef ref : deps) {
                MavenCoordinates coordinates = StarterCoordinates.resolve(ref.id());
                String declaration = coordinates.gradleDeclaration(mapGradleConfiguration(coordinates.scope()));
                if (!content.contains(coordinates.gradleNotation())) {
                    additions.add("    " + declaration);
                }
            }
            if (additions.isEmpty()) {
                return Result.okVoid();
            }
            lines.addAll(insertAt, additions);
            Files.write(buildGradle, lines);
            return Result.okVoid();
        } catch (IOException e) {
            return ioError(e);
        }
    }

    @Override
    public Result<Void> remove(Path projectDir, List<DependencyRef> deps) {
        Path buildGradle = projectDir.resolve("build.gradle");
        if (!Files.exists(buildGradle)) {
            return notGradle();
        }
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(buildGradle));
            for (DependencyRef ref : deps) {
                MavenCoordinates coordinates = StarterCoordinates.resolve(ref.id());
                lines.removeIf(line -> line.contains(coordinates.gradleNotation()));
            }
            Files.write(buildGradle, lines);
            return Result.okVoid();
        } catch (IOException e) {
            return ioError(e);
        }
    }

    @Override
    public boolean isMavenProject(Path projectDir) {
        return false;
    }

    @Override
    public boolean isGradleProject(Path projectDir) {
        return Files.exists(projectDir.resolve("build.gradle"))
                || Files.exists(projectDir.resolve("build.gradle.kts"));
    }

    private static int findDependenciesInsertIndex(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().startsWith("dependencies")) {
                return i + 1;
            }
        }
        return -1;
    }

    private static String mapGradleConfiguration(String scope) {
        if (scope == null || scope.isBlank()) {
            return "implementation";
        }
        return switch (scope.toLowerCase(Locale.ROOT)) {
            case "compileonly" -> "compileOnly";
            case "runtime" -> "runtimeOnly";
            case "test" -> "testImplementation";
            default -> "implementation";
        };
    }

    private static Result<Void> notGradle() {
        return Result.fail(new SpringxException(
                "No Gradle project found.",
                "build.gradle is missing.",
                "Run this command from a Gradle Spring Boot project."
        ));
    }

    private static Result<Void> ioError(IOException e) {
        return Result.fail(new SpringxException(
                "Unable to edit build.gradle.",
                Objects.toString(e.getMessage(), "I/O error."),
                "Check file permissions and try again."
        ));
    }
}
