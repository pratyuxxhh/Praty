package praty.modules.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyUsageStore {
    private static final Path STORE_FILE = Path.of(System.getProperty("user.home"), ".praty", "dependency-usage.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Map<String, Integer> loadCounts() {
        try {
            if (Files.exists(STORE_FILE)) {
                return MAPPER.readValue(STORE_FILE.toFile(), new TypeReference<Map<String, Integer>>() {});
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read dependency usage history: " + e.getMessage());
        }
        return new HashMap<>();
    }

    public static void saveCounts(Map<String, Integer> counts) {
        try {
            Files.createDirectories(STORE_FILE.getParent());
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(STORE_FILE.toFile(), counts);
        } catch (IOException e) {
            System.err.println("Warning: could not save dependency usage history: " + e.getMessage());
        }
    }

    public static void recordUsage(List<String> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return;
        }
        Map<String, Integer> counts = loadCounts();
        for (String dependency : dependencies) {
            if (dependency == null || dependency.isBlank()) {
                continue;
            }
            String normalized = dependency.trim();
            counts.put(normalized, counts.getOrDefault(normalized, 0) + 1);
        }
        saveCounts(counts);
    }
}
