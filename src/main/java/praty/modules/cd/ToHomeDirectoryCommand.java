package praty.modules.cd;

import java.awt.Desktop;
import java.io.File;

import praty.EnvConfig;
import praty.command.Command;
import praty.command.CommandContext;

public class ToHomeDirectoryCommand implements Command {

    private static final String HOME = EnvConfig.get("PRATY_HOME_DIR", "C:\\Users\\ishuk\\OneDrive\\Desktop");

    @Override
    public void execute(CommandContext ctx) {
        try {
            Desktop.getDesktop().open(new File(HOME));
            System.out.println("Opened home directory: " + HOME);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}