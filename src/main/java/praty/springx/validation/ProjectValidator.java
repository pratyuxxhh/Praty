package praty.springx.validation;

import praty.springx.core.Result;

/**
 * Validates project naming and package conventions.
 */
public interface ProjectValidator {

    Result<Void> validateProjectName(String name);

    Result<Void> validatePackageName(String packageName);
}
