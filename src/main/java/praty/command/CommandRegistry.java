package praty.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry that stores commands by keys. Keys are either:
 * - module (for single-word commands), or
 * - module:action (for module-action commands)
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    public void register(String module, String action, Command command) {
        String key = keyFor(module, action);
        commands.put(key, command);
    }

    public Command get(String module, String action) {
        String key = keyFor(module, action);
        // try module:action first, then module-only
        Command cmd = commands.get(key);
        if (cmd != null) return cmd;
        return commands.get(module);
    }

    public boolean has(String module, String action) {
        return get(module, action) != null;
    }

    public Map<String, Command> all() {
        return Map.copyOf(commands);
    }

    private String keyFor(String module, String action) {
        if (action == null || action.isEmpty()) return module == null ? "" : module;
        return module + ":" + action;
    }
}

