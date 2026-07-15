package praty.springx.network;

import praty.springx.model.Dependency;
import praty.springx.model.DependencyCategory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parses Spring Initializr metadata JSON into {@link InitializrMetadata}.
 */
public final class InitializrMetadataParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private InitializrMetadataParser() {
    }

    public static InitializrMetadata parse(String json) {
        try {
            JsonNode root = MAPPER.readTree(json);
            String defaultBoot = textOrEmpty(root.path("bootVersion").path("default"));
            List<String> bootVersions = readVersionIds(root.path("bootVersion").path("values"));
            List<String> javaVersions = readVersionIds(root.path("javaVersion").path("values"));
            List<Dependency> dependencies = new ArrayList<>();
            collectDependencies(root.path("dependencies").path("values"), dependencies);
            dependencies.sort(Comparator.comparing(Dependency::name));
            return new InitializrMetadata(
                    defaultBoot,
                    bootVersions,
                    javaVersions,
                    dependencies,
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Initializr metadata JSON.", e);
        }
    }

    private static void collectDependencies(JsonNode nodes, List<Dependency> out) {
        if (!nodes.isArray()) {
            return;
        }
        for (JsonNode node : nodes) {
            if (node.has("values")) {
                collectDependencies(node.path("values"), out);
                continue;
            }
            String id = textOrEmpty(node.path("id"));
            if (id.isBlank()) {
                continue;
            }
            String name = textOrEmpty(node.path("name"));
            if (name.isBlank()) {
                name = id;
            }
            String description = textOrEmpty(node.path("description"));
            out.add(new Dependency(id, name, description, "", "", DependencyCategory.OTHER, ""));
        }
    }

    private static List<String> readVersionIds(JsonNode values) {
        Set<String> ids = new LinkedHashSet<>();
        if (values.isArray()) {
            for (JsonNode value : values) {
                String id = textOrEmpty(value.path("id"));
                if (!id.isBlank()) {
                    ids.add(id);
                }
            }
        }
        return List.copyOf(ids);
    }

    private static String textOrEmpty(JsonNode node) {
        return node.isMissingNode() || node.isNull() ? "" : node.asText("");
    }
}
