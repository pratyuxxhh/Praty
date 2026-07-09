package praty.modules.file;

import praty.command.Command;
import praty.command.CommandContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DeleteCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        if (args.size() < 1) {
            System.out.println("Usage: praty file d <source>");
            return;
        }

        Path source = Path.of(args.get(0));

        try {
            Files.delete(source);
            System.out.printf("Deleted  file at %s \n", source);
        } catch (IOException e) {
            System.out.println("Move failed: " + e.getMessage());
        }
    }
}
