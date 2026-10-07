package praty.modules.man;

import praty.Main;
import praty.command.Command;
import praty.command.CommandContext;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ShowManualCommand implements Command {
    private static final Map<String, String> DESCRIPTIONS = createDescriptions();

    @Override
    public void execute(CommandContext ctx) {
        System.out.println("PRATY Manual:");
        System.out.println("Usage: praty <module> <action> [arguments]");
        System.out.println("Available commands:");

        Main.registry.all().keySet().stream()
                .sorted(Comparator.naturalOrder())
                .forEach(key -> {
                    String description = DESCRIPTIONS.getOrDefault(key, "No description available.");
                    if (key.contains(":")) {
                        String[] parts = key.split(":", 2);
                        System.out.printf("  %-18s - %s%n", parts[0] + " " + parts[1], description);
                    } else {
                        System.out.printf("  %-18s - %s%n", key, description);
                    }
                });
    }

    private static Map<String, String> createDescriptions() {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("awake", "Wake up the system.");
        descriptions.put("sleep", "Put the system to sleep.");
        descriptions.put("shutdown", "Shutdown the system.");
        descriptions.put("restart", "Restart the system.");

        descriptions.put("check:update", "Check for updates.");
        descriptions.put("check:updates", "Check for updates.");
        descriptions.put("get:update", "Check for updates.");
        descriptions.put("get:updates", "Check for updates.");

        descriptions.put("file:cp", "Copy a file.");
        descriptions.put("file:mv", "Move a file.");
        descriptions.put("file:-d", "Delete a file.");
        descriptions.put("file:unzip", "Unzip an archive.");

        descriptions.put("app:-add", "Register a new app shortcut.");
        descriptions.put("app:-o", "Open a registered application.");
        descriptions.put("app:-open", "Open a registered application.");
        descriptions.put("app:-ls", "List registered applications.");
        descriptions.put("app:-rm", "Remove a registered application.");
        descriptions.put("app:-r", "Remove a registered application.");

        descriptions.put("cd:~", "Open the home directory.");
        descriptions.put("spring:setup", "Setup a new Spring Boot project.");
        descriptions.put("spring:deps", "Show Spring dependency usage counts.");
        descriptions.put("man", "Show this manual.");
        descriptions.put("alias", "Create a shell alias, or list them with -l.");
        return descriptions;
    }
}
        