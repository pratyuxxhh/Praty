package praty.springx.validation;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;

import java.util.regex.Pattern;

/**
 * Default naming rules for Spring Boot projects.
 */
public final class DefaultProjectValidator implements ProjectValidator {

    private static final Pattern PROJECT_NAME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_-]{0,63}$");
    private static final Pattern PACKAGE_NAME = Pattern.compile(
            "^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$"
    );

    @Override
    public Result<Void> validateProjectName(String name) {
        if (name == null || name.isBlank()) {
            return Result.fail(new SpringxException(
                    "Invalid project name.",
                    "Project name is empty.",
                    "Enter a name like my-blog or demo-app."
            ));
        }
        String trimmed = name.trim();
        if (!PROJECT_NAME.matcher(trimmed).matches()) {
            return Result.fail(new SpringxException(
                    "Invalid project name.",
                    "Name must start with a letter and contain only letters, digits, hyphens, or underscores.",
                    "Example: my-blog"
            ));
        }
        return Result.okVoid();
    }

    @Override
    public Result<Void> validatePackageName(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return Result.fail(new SpringxException(
                    "Invalid package name.",
                    "Package name is empty.",
                    "Enter a Java package like com.example.blog."
            ));
        }
        String trimmed = packageName.trim();
        if (!PACKAGE_NAME.matcher(trimmed).matches()) {
            return Result.fail(new SpringxException(
                    "Invalid package name.",
                    "Package must be lowercase dotted identifiers (e.g. com.example.blog).",
                    "Avoid uppercase letters and reserved path segments."
            ));
        }
        return Result.okVoid();
    }
}
