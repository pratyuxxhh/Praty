package praty.modules.alias;

import praty.EnvConfig;
import praty.command.Command;
import praty.command.CommandContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class AliasCommand implements Command {
    private static final String USAGE = "Usage: praty alias \"my-command\" \"real-command\"\n"
            + "       praty alias -l";

    @Override
    public void execute(CommandContext ctx) {
        String action = ctx.action();
        if ("-l".equals(action) || "--list".equals(action)) {
            listAliases();
            return;
        }
        if (action.isBlank()) {
            System.out.println(USAGE);
            return;
        }
        saveAlias(action, ctx.arguments());
    }

    private void saveAlias(String name, List<String> arguments) {
        if (!AliasNames.isValid(name)) {
            System.out.println("Invalid alias name. Use letters, digits, '_' or '-', starting with a letter.");
            return;
        }
        String realCommand = String.join(" ", arguments).trim();
        if (realCommand.isEmpty()) {
            System.out.println(USAGE);
            return;
        }

        try {
            AliasStore store = new AliasStore(storePath());
            Map<String, String> aliases = store.load();
            aliases.put(name, realCommand);
            store.save(aliases);

            AliasShellInstaller installer = new AliasShellInstaller(binPath(), true);
            boolean pathUpdated = installer.install(name, realCommand);

            System.out.printf("Alias saved: %s -> %s%n", name, realCommand);
            System.out.println("Wrappers installed in: " + installer.binDir().toAbsolutePath());
            if (pathUpdated) {
                System.out.println("Added that folder to your user PATH. Open a new terminal or PowerShell, then run: " + name);
            } else {
                System.out.println("Open a new terminal or PowerShell, then run: " + name);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAliases() {
        try {
            Map<String, String> aliases = new AliasStore(storePath()).load();
            if (aliases.isEmpty()) {
                System.out.println("No aliases registered.");
                return;
            }
            aliases.forEach((name, command) -> System.out.println(name + " -> " + command));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Path storePath() {
        String fallback = Path.of(System.getProperty("user.home"), ".praty", "aliases.json").toString();
        return Path.of(EnvConfig.get("PRATY_ALIAS_STORE", fallback));
    }

    private static Path binPath() {
        String fallback = Path.of(System.getProperty("user.home"), ".praty", "bin").toString();
        return Path.of(EnvConfig.get("PRATY_ALIAS_BIN", fallback));
    }
}
