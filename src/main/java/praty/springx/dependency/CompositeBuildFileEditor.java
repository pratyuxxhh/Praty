package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.model.DependencyRef;

import java.nio.file.Path;
import java.util.List;

/**
 * Delegates build-file edits to Maven or Gradle editors based on project type.
 */
public final class CompositeBuildFileEditor implements BuildFileEditor {

    private final MavenBuildFileEditor maven = new MavenBuildFileEditor();
    private final GradleBuildFileEditor gradle = new GradleBuildFileEditor();

    @Override
    public Result<Void> add(Path projectDir, List<DependencyRef> deps) {
        return resolve(projectDir).add(projectDir, deps);
    }

    @Override
    public Result<Void> remove(Path projectDir, List<DependencyRef> deps) {
        return resolve(projectDir).remove(projectDir, deps);
    }

    @Override
    public boolean isMavenProject(Path projectDir) {
        return maven.isMavenProject(projectDir);
    }

    @Override
    public boolean isGradleProject(Path projectDir) {
        return gradle.isGradleProject(projectDir);
    }

    private BuildFileEditor resolve(Path projectDir) {
        return switch (ProjectDetector.buildTool(projectDir)) {
            case MAVEN -> maven;
            case GRADLE -> gradle;
            default -> maven;
        };
    }
}
