package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.model.DependencyRef;

import java.nio.file.Path;
import java.util.List;

/**
 * Edits pom.xml or build.gradle to add/remove dependencies.
 * Implemented in Phase 5.
 */
public interface BuildFileEditor {

    Result<Void> add(Path projectDir, List<DependencyRef> deps);

    Result<Void> remove(Path projectDir, List<DependencyRef> deps);

    boolean isMavenProject(Path projectDir);

    boolean isGradleProject(Path projectDir);
}
