package praty.springx.generator;

import praty.springx.core.Result;
import praty.springx.model.CreateResult;
import praty.springx.model.ProjectSpec;

import java.nio.file.Path;

/**
 * Extracts and post-processes an Initializr ZIP.
 * Implemented in Phase 4.
 */
public interface ProjectGenerator {

    Result<CreateResult> extract(Path zipFile, ProjectSpec spec);
}
