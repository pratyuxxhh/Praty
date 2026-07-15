package praty.springx.validation;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.ProjectSpec;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Warns when a new project would be created inside an existing tool/repo checkout.
 */
public final class ProjectLocationGuard {

    private ProjectLocationGuard() {
    }

    public static Result<Void> validateTargetLocation(ProjectSpec spec) {
        return validateTargetLocation(spec, Path.of("").toAbsolutePath().normalize());
    }

    public static Result<Void> validateTargetLocation(ProjectSpec spec, Path workingDirectory) {
        Path target = resolveTarget(spec);
        Path cwd = workingDirectory.toAbsolutePath().normalize();

        if (isInsideOrEquals(cwd, target) && looksLikePratyCheckout(cwd)) {
            return Result.fail(new SpringxException(
                    "Refusing to create a Spring Boot project inside the PRATY CLI repository.",
                    "Target folder " + target + " is inside " + cwd + ".",
                    "Run praty spring setup from a parent folder such as Desktop\\programs."
            ));
        }

        if (Files.exists(target) && Files.exists(target.resolve("pom.xml"))) {
            return Result.fail(new SpringxException(
                    "Target directory already contains a Maven project.",
                    target + " already has a pom.xml.",
                    "Choose a different project name."
            ));
        }

        if (Files.exists(target) && (Files.exists(target.resolve("build.gradle")) || Files.exists(target.resolve("build.gradle.kts")))) {
            return Result.fail(new SpringxException(
                    "Target directory already contains a Gradle project.",
                target + " already has a build.gradle or build.gradle.kts.",
                    "Choose a different project name."
            ));
        }

        return Result.okVoid();
    }

    private static Path resolveTarget(ProjectSpec spec) {
        String folder = spec.effectiveTargetDirectory();
        return Path.of(folder).toAbsolutePath().normalize();
    }

    private static boolean looksLikePratyCheckout(Path directory) {
        return Files.isDirectory(directory.resolve("src/main/java/praty"))
                && Files.exists(directory.resolve("pom.xml"));
    }

    private static boolean isInsideOrEquals(Path parent, Path child) {
        return child.startsWith(parent);
    }
}
