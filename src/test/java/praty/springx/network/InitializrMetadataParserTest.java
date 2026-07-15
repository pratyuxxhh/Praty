package praty.springx.network;

import org.junit.jupiter.api.Test;
import praty.springx.model.DependencyCategory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InitializrMetadataParserTest {

    @Test
    void parsesBootVersionsAndDependencies() {
        String json = """
                {
                  "bootVersion": {
                    "default": "3.4.1",
                    "values": [
                      {"id": "3.4.1", "name": "3.4.1"},
                      {"id": "3.3.7", "name": "3.3.7"}
                    ]
                  },
                  "javaVersion": {
                    "values": [
                      {"id": "17", "name": "17"},
                      {"id": "21", "name": "21"}
                    ]
                  },
                  "dependencies": {
                    "values": [
                      {
                        "name": "Web",
                        "values": [
                          {"id": "web", "name": "Spring Web", "description": "Build web apps"}
                        ]
                      },
                      {"id": "data-jpa", "name": "Spring Data JPA", "description": "JPA support"}
                    ]
                  }
                }
                """;

        InitializrMetadata metadata = InitializrMetadataParser.parse(json);

        assertEquals("3.4.1", metadata.bootVersionHint());
        assertTrue(metadata.bootVersions().contains("3.4.1"));
        assertTrue(metadata.javaVersions().contains("21"));
        assertEquals(2, metadata.dependencies().size());
        assertEquals("web", metadata.dependencies().stream()
                .filter(d -> d.id().equals("web"))
                .findFirst()
                .orElseThrow()
                .id());
        assertEquals(DependencyCategory.OTHER, metadata.dependencies().getFirst().category());
    }
}
