package praty.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple parser that extracts module, action, and remaining arguments.
 *
 * Examples:
 * - praty awake -> module="awake", action="", arguments=[]
 * - praty file copy a b -> module="file", action="copy", arguments=["a","b"]
 */
public class Parser {
    private final String[] args;

    public Parser(String[] args) {
        this.args = args == null ? new String[0] : args;
    }

    public String module() {
        return args.length > 0 ? args[0] : "";
    }

    public String action() {
        return args.length > 1 ? args[1] : "";
    }

    public List<String> arguments() {
        if (args.length <= 2) return List.of();
        return new ArrayList<>(Arrays.asList(args).subList(2, args.length));
    }

    public CommandContext toContext() {
        return new CommandContext(module(), action(), arguments());
    }
}

