package praty.modules.app;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import praty.command.Command;
import praty.command.CommandContext;

public class ListApplicationsCommand implements Command {

    @Override
    public void execute(CommandContext ctx) {
        String appStorePath = praty.EnvConfig.get("PRATY_APP_STORE", "C:\\Users\\ishuk\\apps.json");
        File file = new File(appStorePath);

        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, String> apps = mapper.readValue(
                    file,
                    new TypeReference<Map<String, String>>() {
                    });

            if (apps.isEmpty()) {
                System.out.println("No applications registered.");
                return;
            }

            apps.forEach((key, value) -> System.out.println(key + " -> " + value));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
