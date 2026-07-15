package praty.springx.template;

import praty.springx.core.Result;
import praty.springx.model.ProjectSpec;

/**
 * Project template SPI. Implemented in Phase 7.
 */
public interface Template {

    String id();

    String name();

    String description();

    /**
     * Applies template overlays (folders, Docker, README, etc.) after base project creation.
     */
    Result<Void> apply(ProjectSpec spec);
}
