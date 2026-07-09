package praty.modules.restart;

import java.io.IOException;

public class RestartCommand  implements praty.command.Command {
    @Override
    public void execute(praty.command.CommandContext ctx) {
        try {
            new ProcessBuilder(
                    "shutdown",
                    "/r",
                    "/t",
                    "0").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
