package praty.springx.dependency;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockDependencyDataTest {

    @Test
    void searchFindsMongoDependencies() {
        assertFalse(MockDependencyData.search("mongo").isEmpty());
        assertTrue(MockDependencyData.search("mongo").stream()
                .anyMatch(d -> d.id().contains("mongo")));
    }

    @Test
    void bootVersionsArePresent() {
        assertTrue(MockDependencyData.bootVersions().contains("3.4.1"));
    }
}
