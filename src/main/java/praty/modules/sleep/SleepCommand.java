package praty.modules.sleep;

import java.io.IOException;

import praty.command.Command;
import praty.command.CommandContext;

public class SleepCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        try {
            Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("Okay , good night !!");
    }
}
