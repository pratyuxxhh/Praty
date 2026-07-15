package praty.springx.cli;

/**
 * Helpers for resolving {@code praty spring <action>} routing.
 * Commands remain registered in {@code praty.Main}; this package may grow
 * shared flag parsing in later phases.
 */
public final class SpringCli {

    public static final String MODULE = "spring";
    public static final String ACTION_SETUP = "setup";
    public static final String ACTION_DEPS = "deps";

    private SpringCli() {
    }
}
