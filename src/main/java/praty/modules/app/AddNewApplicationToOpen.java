package praty.modules.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import praty.command.Command;
import praty.command.CommandContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class AddNewApplicationToOpen implements Command {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        if (args.size() < 2) {
            System.out.println("Usage: praty app -add <key> <path>");
            return;
        }

        String key = args.get(0);
        String value = args.get(1);

        String appStorePath = praty.EnvConfig.get("PRATY_APP_STORE", "C:\\Users\\ishuk\\apps.json");
        File file = new File(appStorePath);
        System.out.println(file.getAbsolutePath());
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, String> apps;

            if (file.exists()) {
                apps = mapper.readValue(
                        file,
                        new TypeReference<Map<String, String>>() {
                        });
            } else {
                apps = new HashMap<>();
            }

            apps.put(key, value);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, apps);

            System.out.println("File saved: " + file.getAbsolutePath());
            System.out.println("Exists: " + file.exists());
            System.out.println("Size: " + file.length());

            System.out.printf("Added -> key: %s\tvalue: %s%n", key, value);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
