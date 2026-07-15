package praty.springx.dependency;

import java.util.Optional;

/**
 * Maven coordinates for an Initializr dependency id.
 */
public record MavenCoordinates(String groupId, String artifactId, String scope) {

    public MavenCoordinates(String groupId, String artifactId) {
        this(groupId, artifactId, null);
    }

    public String gradleNotation() {
        return groupId + ":" + artifactId;
    }

    public String gradleDeclaration(String configuration) {
        String scope = configuration == null || configuration.isBlank() ? "implementation" : configuration;
        return scope + " '" + gradleNotation() + "'";
    }
}
