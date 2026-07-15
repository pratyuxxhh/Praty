package praty.springx.plugin;

import java.util.List;

/**
 * No-op plugin registry until Phase 8.
 */
public final class NoopPluginRegistry implements PluginRegistry {

    @Override
    public List<Plugin> all() {
        return List.of();
    }

    @Override
    public void invoke(Hook hook, Plugin.PluginContext context) {
        // no plugins registered
    }
}
