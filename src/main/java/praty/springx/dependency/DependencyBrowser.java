package praty.springx.dependency;

import praty.springx.config.SpringxConfig;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds prioritized dependency lists for the interactive browser.
 */
public final class DependencyBrowser {

    private DependencyBrowser() {
    }

    public static List<Dependency> forAdd(
            DependencyCatalog catalog,
            SpringxConfig config,
            List<DependencyRef> installed
    ) {
        Set<String> installedIds = installed.stream().map(DependencyRef::id).collect(Collectors.toSet());
        List<DependencyRef> installedRefs = List.copyOf(installed);
        Set<String> seen = new LinkedHashSet<>();
        List<Dependency> ordered = new ArrayList<>();

        appendUnique(ordered, seen, favorites(catalog, config));
        appendUnique(ordered, seen, recent(catalog, config));
        appendUnique(ordered, seen, catalog.recommend(installedRefs));
        for (Dependency dependency : catalog.all()) {
            if (!installedIds.contains(dependency.id())) {
                appendUnique(ordered, seen, List.of(dependency));
            }
        }
        return ordered;
    }

    public static List<Dependency> forRemove(DependencyCatalog catalog, List<DependencyRef> installed) {
        Map<String, Dependency> byId = catalog.all().stream()
                .collect(Collectors.toMap(Dependency::id, d -> d, (a, b) -> a));
        List<Dependency> result = new ArrayList<>();
        for (DependencyRef ref : installed) {
            Dependency dependency = byId.get(ref.id());
            if (dependency != null) {
                result.add(dependency);
            } else {
                result.add(new Dependency(
                        ref.id(),
                        ref.id(),
                        "",
                        "",
                        "",
                        praty.springx.model.DependencyCategory.OTHER,
                        ""
                ));
            }
        }
        return result.stream()
                .sorted(Comparator.comparing(Dependency::name))
                .toList();
    }

    private static List<Dependency> favorites(DependencyCatalog catalog, SpringxConfig config) {
        return resolveIds(catalog, config.favorites());
    }

    private static List<Dependency> recent(DependencyCatalog catalog, SpringxConfig config) {
        List<String> recentIds = config.usage().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .toList();
        return resolveIds(catalog, recentIds);
    }

    private static List<Dependency> resolveIds(DependencyCatalog catalog, List<String> ids) {
        List<Dependency> result = new ArrayList<>();
        for (String id : ids) {
            catalog.findById(id).optional().ifPresent(result::add);
        }
        return result;
    }

    private static void appendUnique(List<Dependency> target, Set<String> seen, List<Dependency> source) {
        for (Dependency dependency : source) {
            String key = dependency.id().toLowerCase(Locale.ROOT);
            if (seen.add(key)) {
                target.add(dependency);
            }
        }
    }
}
