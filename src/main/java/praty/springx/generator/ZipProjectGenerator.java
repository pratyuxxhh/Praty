package praty.springx.generator;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.CreateResult;
import praty.springx.model.ProjectSpec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extracts an Initializr project ZIP into the target directory.
 */
public final class ZipProjectGenerator implements ProjectGenerator {

    @Override
    public Result<CreateResult> extract(Path zipFile, ProjectSpec spec) {
        Path targetDir = resolveTargetDir(spec);
        Path staging = null;
        try {
            if (Files.exists(targetDir) && isNonEmpty(targetDir)) {
                return Result.fail(new SpringxException(
                        "Target directory already exists.",
                        targetDir + " is not empty.",
                        "Choose a different project name or remove the folder."
                ));
            }
            staging = Files.createTempDirectory("springx-extract-");
            unzip(zipFile, staging);
            Path projectRoot = findProjectRoot(staging, spec.projectName());
            installProjectRoot(projectRoot, targetDir);
            Files.deleteIfExists(zipFile);
            if (!Files.exists(targetDir) || !hasBuildFile(targetDir)) {
                return Result.fail(new SpringxException(
                        "Project extraction failed.",
                        "Expected folder was not created: " + targetDir,
                        "Try again or run praty spring doctor."
                ));
            }
            return Result.ok(new CreateResult(targetDir, spec));
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to extract project archive.",
                    e.getMessage() == null ? "I/O error." : e.getMessage(),
                    "Check disk space and folder permissions."
            ));
        } finally {
            if (staging != null) {
                deleteRecursively(staging);
            }
        }
    }

    private static Path resolveTargetDir(ProjectSpec spec) {
        String folder = spec.effectiveTargetDirectory();
        return Path.of(folder).toAbsolutePath().normalize();
    }

    /**
     * Initializr archives may be flat ({@code pom.xml} at zip root) or nested ({@code my-app/pom.xml}).
     */
    static Path findProjectRoot(Path staging, String projectName) throws IOException {
        Path named = staging.resolve(projectName);
        if (Files.isDirectory(named) && hasBuildFile(named)) {
            return named;
        }
        if (hasBuildFile(staging)) {
            return staging;
        }
        List<Path> directories;
        try (Stream<Path> stream = Files.list(staging)) {
            directories = stream.filter(Files::isDirectory).sorted(Comparator.comparing(Path::getFileName)).toList();
        }
        for (Path directory : directories) {
            if (hasBuildFile(directory)) {
                return directory;
            }
        }
        if (directories.size() == 1) {
            return directories.getFirst();
        }
        return staging;
    }

    static void installProjectRoot(Path projectRoot, Path targetDir) throws IOException {
        Files.createDirectories(targetDir.getParent() == null ? targetDir : targetDir.getParent());
        if (projectRoot.normalize().equals(targetDir.normalize())) {
            Files.createDirectories(targetDir);
            return;
        }
        if (projectRoot.getFileName() != null
                && projectRoot.getFileName().equals(targetDir.getFileName())
                && Files.isDirectory(projectRoot)) {
            if (Files.exists(targetDir)) {
                deleteRecursively(targetDir);
            }
            Files.move(projectRoot, targetDir, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        Files.createDirectories(targetDir);
        try (Stream<Path> entries = Files.list(projectRoot)) {
            for (Path entry : entries.toList()) {
                Path destination = targetDir.resolve(entry.getFileName());
                if (Files.exists(destination)) {
                    deleteRecursively(destination);
                }
                Files.move(entry, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static boolean hasBuildFile(Path directory) {
        return Files.exists(directory.resolve("pom.xml"))
                || Files.exists(directory.resolve("build.gradle"))
                || Files.exists(directory.resolve("build.gradle.kts"));
    }

    private static boolean isNonEmpty(Path directory) throws IOException {
        try (Stream<Path> stream = Files.list(directory)) {
            return stream.findAny().isPresent();
        }
    }

    private static void unzip(Path zipFile, Path extractRoot) throws IOException {
        try (InputStream in = Files.newInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path out = extractRoot.resolve(entry.getName()).normalize();
                if (!out.startsWith(extractRoot)) {
                    throw new IOException("Zip entry escapes target directory: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    Files.copy(zis, out, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private static void deleteRecursively(Path root) {
        if (!Files.exists(root)) {
            return;
        }
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }
}
