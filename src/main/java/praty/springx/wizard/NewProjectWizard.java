package praty.springx.wizard;

import praty.springx.core.Result;
import praty.springx.model.ProjectSpec;

/**
 * Interactive new-project wizard invoked by {@code praty spring setup}.
 * Implemented in Phase 3–4.
 */
public interface NewProjectWizard {

    /**
     * Runs the full wizard and returns a completed {@link ProjectSpec},
     * or a failure if the user cancels / an error occurs.
     */
    Result<ProjectSpec> run();
}
