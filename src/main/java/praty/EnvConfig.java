package praty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvConfig {
    private static final Map<String, String> ENV = loadEnvFile();

    private static Map<String, String> loadEnvFile() {
        Map<String, String> values = new HashMap<>();
        File envFile = new File(System.getProperty("user.dir"), ".env");
        if (!envFile.exists()) {
            return values;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equals = line.indexOf('=');
                if (equals <= 0) {
                    continue;
                }
                String key = line.substring(0, equals).trim();
                String value = line.substring(equals + 1).trim();
                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                values.put(key, value);
            }
        } catch (IOException ignored) {
            // ignore invalid or unreadable .env file
        }
        return values;
    }

    public static String get(String key, String defaultValue) {
        String value = ENV.getOrDefault(key, System.getenv(key));
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
