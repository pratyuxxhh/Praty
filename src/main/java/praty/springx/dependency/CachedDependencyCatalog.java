package praty.springx.dependency;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;
import praty.springx.network.InitializrMetadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Catalog backed by cached/live Initializr metadata with mock fallback.
 */
public final class CachedDependencyCatalog implements DependencyCatalog {

    private final Map<String, Dependency> byId;

    public CachedDependencyCatalog(InitializrMetadata metadata) {
        List<Dependency> source = metadata != null && !metadata.dependencies().isEmpty()
                ? metadata.dependencies()
                : MockDependencyData.all();
        Map<String, Dependency> map = new LinkedHashMap<>();
        for (Dependency dependency : source) {
            map.put(dependency.id(), enrichCoordinates(dependency));
        }
        this.byId = Map.copyOf(map);
    }

    @Override
    public List<Dependency> all() {
        return byId.values().stream()
                .sorted(Comparator.comparing(Dependency::name))
                .toList();
    }

    @Override
    public List<Dependency> search(String query) {
        if (query == null || query.isBlank()) {
            return all();
        }
        String q = query.toLowerCase(Locale.ROOT).trim();
        List<Dependency> matches = new ArrayList<>();
        for (Dependency dependency : all()) {
            if (dependency.id().toLowerCase(Locale.ROOT).contains(q)
                    || dependency.name().toLowerCase(Locale.ROOT).contains(q)
                    || dependency.description().toLowerCase(Locale.ROOT).contains(q)
                    || dependency.category().name().toLowerCase(Locale.ROOT).contains(q)) {
                matches.add(dependency);
            }
        }
        return matches;
    }

    @Override
    public List<Dependency> recommend(List<DependencyRef> selected) {
        return DependencyRecommender.recommend(selected, all());
    }

    @Override
    public Result<Dependency> findById(String id) {
        Dependency dependency = byId.get(id);
        if (dependency == null) {
            return Result.fail(new SpringxException(
                    "Unknown dependency.",
                    "No dependency with id: " + id,
                    "Run praty spring add and search the catalog."
            ));
        }
        return Result.ok(dependency);
    }

    private static Dependency enrichCoordinates(Dependency dependency) {
        if (dependency.groupId() != null && !dependency.groupId().isBlank()) {
            return dependency;
        }
        MavenCoordinates coordinates = StarterCoordinates.resolve(dependency.id());
        return new Dependency(
                dependency.id(),
                dependency.name(),
                dependency.description(),
                coordinates.groupId(),
                coordinates.artifactId(),
                dependency.category(),
                dependency.requiredBootVersion()
        );
    }
}
