package praty.modules.awake;

import praty.command.Command;
import praty.command.CommandContext;

public class AwakeCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        System.out.println("  ____            _            _                           _        \n" +
                " |  _ \\ _ __ __ _| |_ _   _   (_)___     __ ___      ____ _| | _____ \n" +
                " | |_) | '__/ _` | __| | | |  | / __|   / _` \\ \\ /\\ / / _` | |/ / _ \\\n" +
                " |  __/| | | (_| | |_| |_| |  | \\__ \\  | (_| |\\ V  V / (_| |   <  __/\n" +
                " |_|   |_|  \\__,_|\\__|\\__, |  |_|___/   \\__,_| \\_/\\_/ \\__,_|_|\\_\\___|\n" +
                "                      |___/                                        ");
    }
}