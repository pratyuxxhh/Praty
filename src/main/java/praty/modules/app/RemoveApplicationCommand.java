package praty.modules.app;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import praty.command.Command;
import praty.command.CommandContext;

public class RemoveApplicationCommand implements Command {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        if (args.isEmpty()) {
            System.out.println("Usage: praty app -r <key>");
            return;
        }

        String key = args.get(0);

        File file = new File("C:\\Users\\ishuk\\apps.json");

        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, String> apps = mapper.readValue(
                    file,
                    new TypeReference<Map<String, String>>() {
                    });

            if (!apps.containsKey(key)) {
                System.out.println("Key not found: " + key);
                return;
            }

            apps.remove(key);

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, apps);

            System.out.println("Removed: " + key);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
