package praty.springx.model;

/**
 * Reference to an Initializr dependency by id (e.g. "web", "data-mongodb").
 */
public record DependencyRef(String id) {
    public DependencyRef {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Dependency id must not be blank");
        }
        id = id.trim();
    }
}
