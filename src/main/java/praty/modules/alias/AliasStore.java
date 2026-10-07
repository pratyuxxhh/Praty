package praty.modules.alias;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

final class AliasStore {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Path file;

    AliasStore(Path file) {
        this.file = file;
    }

    Map<String, String> load() throws IOException {
        if (!Files.exists(file)) {
            return new LinkedHashMap<>();
        }
        Map<String, String> loaded = MAPPER.readValue(file.toFile(), new TypeReference<>() {
        });
        return loaded == null ? new LinkedHashMap<>() : new LinkedHashMap<>(loaded);
    }

    void save(Map<String, String> aliases) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), aliases);
    }
}
