package praty.springx.plugin;

/**
 * Lifecycle hooks plugins can observe.
 */
public enum Hook {
    BEFORE_CREATE,
    AFTER_CREATE,
    BEFORE_DEPENDENCY_INSTALL,
    AFTER_DEPENDENCY_INSTALL,
    BEFORE_GENERATE,
    AFTER_GENERATE
}
