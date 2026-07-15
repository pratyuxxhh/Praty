package praty.springx.plugin;

import java.util.List;

/**
 * Discovers and invokes plugins. Stub until Phase 8.
 */
public interface PluginRegistry {

    List<Plugin> all();

    void invoke(Hook hook, Plugin.PluginContext context);
}
