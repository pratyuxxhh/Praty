package praty.modules.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import praty.command.Command;
import praty.command.CommandContext;

import java.io.File;
import java.util.List;
import java.util.Map;

public class OpenApplicationCommand implements Command {

    @Override
    public void execute(CommandContext ctx) {
        List<String> args = ctx.arguments();

        String key = args.get(0);

        try {
            ObjectMapper mapper = new ObjectMapper();

            String appStorePath = praty.EnvConfig.get("PRATY_APP_STORE", "C:\\Users\\ishuk\\apps.json");
            Map<String, String> apps = mapper.readValue(
                    new File(appStorePath),
                    new TypeReference<Map<String, String>>() {
                    });

            String path = apps.get(key);

            if (path == null) {
                System.out.println("No application found for key: " + key);
                return;
            }

            File exe = new File(path);

            if (!exe.exists()) {
                System.out.println("Application not found: " + path);
                return;
            }

            Runtime.getRuntime().exec(path);

            System.out.println("Application opened: " + key);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
