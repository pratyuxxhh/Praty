package praty.springx.network;

import praty.springx.core.Result;
import praty.springx.model.ProjectSpec;

import java.nio.file.Path;

/**
 * Client for Spring Initializr HTTP API.
 * Implemented in Phase 4.
 */
public interface InitializrClient {

    Result<InitializrMetadata> fetchMetadata();

    /**
     * Downloads a project ZIP to a temporary path.
     */
    Result<Path> downloadProject(ProjectSpec spec);
}
