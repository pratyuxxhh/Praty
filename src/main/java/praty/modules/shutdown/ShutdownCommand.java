package praty.modules.shutdown;

import java.io.IOException;

import praty.command.Command;
import praty.command.CommandContext;

public class ShutdownCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        try {
            new ProcessBuilder(
                    "shutdown",
                    "/s",
                    "/t",
                    "0").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
