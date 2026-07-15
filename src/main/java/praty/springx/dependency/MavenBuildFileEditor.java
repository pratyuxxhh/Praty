package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.DependencyRef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Edits {@code pom.xml} to add/remove Spring Boot starter dependencies.
 */
public final class MavenBuildFileEditor implements BuildFileEditor {

    @Override
    public Result<Void> add(Path projectDir, List<DependencyRef> deps) {
        Path pom = projectDir.resolve("pom.xml");
        if (!Files.exists(pom)) {
            return notMaven();
        }
        try {
            String content = Files.readString(pom);
            int close = content.lastIndexOf("</dependencies>");
            if (close < 0) {
                return Result.fail(new SpringxException(
                        "Unable to edit pom.xml.",
                        "No <dependencies> section found.",
                        "Add a dependencies section to pom.xml first."
                ));
            }
            StringBuilder additions = new StringBuilder();
            for (DependencyRef ref : deps) {
                MavenCoordinates coordinates = StarterCoordinates.resolve(ref.id());
                if (containsDependency(content, coordinates)) {
                    continue;
                }
                additions.append(renderDependencyBlock(coordinates));
            }
            if (additions.isEmpty()) {
                return Result.okVoid();
            }
            String updated = content.substring(0, close) + additions + content.substring(close);
            Files.writeString(pom, updated);
            return Result.okVoid();
        } catch (IOException e) {
            return ioError(e);
        }
    }

    @Override
    public Result<Void> remove(Path projectDir, List<DependencyRef> deps) {
        Path pom = projectDir.resolve("pom.xml");
        if (!Files.exists(pom)) {
            return notMaven();
        }
        try {
            String content = Files.readString(pom);
            for (DependencyRef ref : deps) {
                MavenCoordinates coordinates = StarterCoordinates.resolve(ref.id());
                content = removeDependencyByArtifact(content, coordinates);
            }
            Files.writeString(pom, content);
            return Result.okVoid();
        } catch (IOException e) {
            return ioError(e);
        }
    }

    @Override
    public boolean isMavenProject(Path projectDir) {
        return Files.exists(projectDir.resolve("pom.xml"));
    }

    @Override
    public boolean isGradleProject(Path projectDir) {
        return false;
    }

    private static String renderDependencyBlock(MavenCoordinates coordinates) {
        StringBuilder block = new StringBuilder();
        block.append("\n        <dependency>\n");
        block.append("            <groupId>").append(coordinates.groupId()).append("</groupId>\n");
        block.append("            <artifactId>").append(coordinates.artifactId()).append("</artifactId>\n");
        if (coordinates.scope() != null && !coordinates.scope().isBlank()
                && !coordinates.scope().equalsIgnoreCase("compile")) {
            String mavenScope = mapScope(coordinates.scope());
            block.append("            <scope>").append(mavenScope).append("</scope>\n");
            if ("compileOnly".equals(coordinates.scope())) {
                block.append("            <optional>true</optional>\n");
            }
        }
        block.append("        </dependency>");
        return block.toString();
    }

    private static String mapScope(String scope) {
        return switch (scope.toLowerCase(Locale.ROOT)) {
            case "compileonly" -> "provided";
            case "runtime" -> "runtime";
            default -> scope;
        };
    }

    private static boolean containsDependency(String pom, MavenCoordinates coordinates) {
        return pom.contains("<artifactId>" + coordinates.artifactId() + "</artifactId>");
    }

    private static String removeDependencyByArtifact(String content, MavenCoordinates coordinates) {
        String escapedGroup = Pattern.quote(coordinates.groupId());
        String escapedArtifact = Pattern.quote(coordinates.artifactId());
        Pattern pattern = Pattern.compile(
                "(?s)\\s*<dependency>\\s*<groupId>" + escapedGroup + "</groupId>\\s*<artifactId>"
                        + escapedArtifact + "</artifactId>.*?</dependency>\\s*"
        );
        return pattern.matcher(content).replaceAll("");
    }

    private static Result<Void> notMaven() {
        return Result.fail(new SpringxException(
                "No Maven project found.",
                "pom.xml is missing.",
                "Run this command from a Maven Spring Boot project."
        ));
    }

    private static Result<Void> ioError(IOException e) {
        return Result.fail(new SpringxException(
                "Unable to edit pom.xml.",
                Objects.toString(e.getMessage(), "I/O error."),
                "Check file permissions and try again."
        ));
    }
}
