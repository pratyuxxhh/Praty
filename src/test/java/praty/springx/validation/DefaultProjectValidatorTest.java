package praty.springx.validation;

import org.junit.jupiter.api.Test;
import praty.springx.core.Result;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultProjectValidatorTest {

    private final DefaultProjectValidator validator = new DefaultProjectValidator();

    @Test
    void acceptsValidProjectNames() {
        assertTrue(validator.validateProjectName("my-blog").isOk());
        assertTrue(validator.validateProjectName("Demo_App").isOk());
    }

    @Test
    void rejectsInvalidProjectNames() {
        assertTrue(validator.validateProjectName("").isFail());
        assertTrue(validator.validateProjectName("1bad").isFail());
        assertTrue(validator.validateProjectName("has space").isFail());
    }

    @Test
    void acceptsValidPackages() {
        Result<Void> result = validator.validatePackageName("com.example.blog");
        assertTrue(result.isOk());
    }

    @Test
    void rejectsInvalidPackages() {
        assertTrue(validator.validatePackageName("Com.Example").isFail());
        assertTrue(validator.validatePackageName("").isFail());
        assertTrue(validator.validatePackageName(".leading").isFail());
    }
}
