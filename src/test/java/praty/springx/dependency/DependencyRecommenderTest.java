package praty.springx.dependency;

import org.junit.jupiter.api.Test;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyRecommenderTest {

    @Test
    void recommendsValidationWhenSecuritySelected() {
        List<Dependency> catalog = MockDependencyData.all();
        List<Dependency> recommended = DependencyRecommender.recommend(
                List.of(new DependencyRef("security")),
                catalog
        );
        assertTrue(recommended.stream().anyMatch(d -> d.id().equals("validation")));
        assertTrue(recommended.stream().anyMatch(d -> d.id().equals("actuator")));
    }
}
