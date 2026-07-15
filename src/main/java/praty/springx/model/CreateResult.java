package praty.springx.model;

import java.nio.file.Path;

/**
 * Outcome of a successful project creation.
 */
public record CreateResult(
        Path projectDirectory,
        ProjectSpec spec
) {
}
