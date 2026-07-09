package praty.modules.file;

import praty.command.Command;
import praty.command.CommandContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MoveCommand implements Command {

    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        if (args.size() < 2) {
            System.out.println("Usage: praty file mv <source> <destination>");
            return;
        }

        Path source = Path.of(args.get(0));
        Path destination = Path.of(args.get(1));

        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.printf("Moved %s -> %s%n", source, destination);
        } catch (IOException e) {
            System.out.println("Move failed: " + e.getMessage());
        }
    }
}
