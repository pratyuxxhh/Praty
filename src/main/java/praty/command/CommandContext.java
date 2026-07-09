package praty.command;

import java.util.List;

public final class CommandContext {
    private final String module;
    private final String action;
    private final List<String> arguments;

    public CommandContext(String module, String action, List<String> arguments) {
        this.module = module == null ? "" : module;
        this.action = action == null ? "" : action;
        this.arguments = arguments == null ? List.of() : List.copyOf(arguments);
    }

    public String module() {
        return module;
    }

    public String action() {
        return action;
    }

    public List<String> arguments() {
        return arguments;
    }
}
