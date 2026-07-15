package praty.springx.plugin;

import praty.springx.model.ProjectSpec;

public interface Plugin {

    String id();

    String name();

    String version();

    default void onHook(Hook hook, PluginContext context) {
    }

    record PluginContext(ProjectSpec projectSpec, Object payload) {
    }
}
