package praty.springx.network;

import praty.springx.model.Dependency;

import java.util.List;

/**
 * Cached / fetched Spring Initializr metadata snapshot.
 */
public record InitializrMetadata(
        String bootVersionHint,
        List<String> bootVersions,
        List<String> javaVersions,
        List<Dependency> dependencies,
        long fetchedAtEpochMs
) {
    public InitializrMetadata {
        bootVersionHint = bootVersionHint == null ? "" : bootVersionHint;
        bootVersions = bootVersions == null ? List.of() : List.copyOf(bootVersions);
        javaVersions = javaVersions == null ? List.of() : List.copyOf(javaVersions);
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
    }

    public static InitializrMetadata empty() {
        return new InitializrMetadata("", List.of(), List.of(), List.of(), 0L);
    }

    public boolean isEmpty() {
        return bootVersions.isEmpty() && dependencies.isEmpty() && bootVersionHint.isBlank();
    }

    public List<String> dependencyIds() {
        return dependencies.stream().map(Dependency::id).toList();
    }
}
