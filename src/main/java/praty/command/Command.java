package praty.command;

public interface Command {
    /**
     * Execute the command with a CommandContext that contains module, action and arguments.
     */
    void execute(CommandContext ctx);
}
