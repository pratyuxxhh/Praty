package praty.springx.plugin;

import praty.springx.model.ProjectSpec;

/**
 * Plugin SPI. Full loading in Phase 8.
 */
public interface Plugin {

    String id();

    String name();

    String version();

    default void onHook(Hook hook, PluginContext context) {
        // no-op
    }

    record PluginContext(ProjectSpec projectSpec, Object payload) {
    }
}
