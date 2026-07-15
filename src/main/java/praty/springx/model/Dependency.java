package praty.springx.model;

/**
 * Full dependency metadata from Initializr / local catalog.
 */
public record Dependency(
        String id,
        String name,
        String description,
        String groupId,
        String artifactId,
        DependencyCategory category,
        String requiredBootVersion
) {
}
