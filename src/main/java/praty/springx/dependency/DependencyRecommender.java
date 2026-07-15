package praty.springx.dependency;

import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Smart dependency recommendations based on current project selections.
 */
public final class DependencyRecommender {

    private static final Map<String, List<String>> RULES = Map.ofEntries(
            Map.entry("security", List.of("oauth2-client", "validation", "actuator")),
            Map.entry("data-mongodb", List.of("validation", "lombok", "docker-compose", "actuator", "data-redis")),
            Map.entry("spring-ai-openai", List.of("spring-ai-ollama", "validation", "actuator")),
            Map.entry("spring-ai-ollama", List.of("validation", "actuator")),
            Map.entry("web", List.of("validation", "actuator", "devtools")),
            Map.entry("webflux", List.of("validation", "actuator", "data-redis")),
            Map.entry("data-jpa", List.of("validation", "h2", "actuator")),
            Map.entry("kafka", List.of("validation", "actuator"))
    );

    private DependencyRecommender() {
    }

    public static List<Dependency> recommend(List<DependencyRef> selected, List<Dependency> catalog) {
        Map<String, Dependency> byId = catalog.stream()
                .collect(Collectors.toMap(Dependency::id, Function.identity(), (a, b) -> a));
        Set<String> already = selected.stream().map(DependencyRef::id).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> recommended = new LinkedHashSet<>();

        for (DependencyRef ref : selected) {
            List<String> suggestions = RULES.get(ref.id().toLowerCase(Locale.ROOT));
            if (suggestions == null) {
                continue;
            }
            for (String id : suggestions) {
                if (!already.contains(id) && byId.containsKey(id)) {
                    recommended.add(id);
                }
            }
        }

        List<Dependency> result = new ArrayList<>();
        for (String id : recommended) {
            result.add(byId.get(id));
        }
        return result;
    }
}
