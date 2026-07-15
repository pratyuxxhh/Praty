package praty.springx.plugin;

import java.util.List;

public final class NoopPluginRegistry implements PluginRegistry {

    @Override
    public List<Plugin> all() {
        return List.of();
    }

    @Override
    public void invoke(Hook hook, Plugin.PluginContext context) {
    }
}
