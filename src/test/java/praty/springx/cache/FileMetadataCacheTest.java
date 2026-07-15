package praty.springx.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyCategory;
import praty.springx.network.InitializrMetadata;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileMetadataCacheTest {

    @TempDir
    Path tempDir;

    @Test
    void putGetAndClear() {
        Path file = tempDir.resolve("initializr-metadata.json");
        FileMetadataCache cache = new FileMetadataCache(file);

        InitializrMetadata metadata = new InitializrMetadata(
                "3.4.1",
                List.of("3.4.1", "3.3.5"),
                List.of("17", "21"),
                List.of(new Dependency("web", "Spring Web", "Web starter", "", "", DependencyCategory.WEB, "")),
                1_700_000_000_000L
        );

        assertTrue(cache.put(metadata).isOk());
        assertTrue(cache.get().isPresent());
        assertEquals("3.4.1", cache.get().orElseThrow().bootVersionHint());
        assertEquals("web", cache.get().orElseThrow().dependencies().getFirst().id());

        assertTrue(cache.clear().isOk());
        assertTrue(cache.get().isEmpty());
    }
}
