package praty.springx.services;

import praty.springx.core.Result;
import praty.springx.model.CreateResult;
import praty.springx.model.ProjectSpec;

/**
 * Creates a Spring Boot project from a {@link ProjectSpec}.
 * Implemented in Phase 4.
 */
public interface ProjectCreateService {

    Result<CreateResult> create(ProjectSpec spec);
}
