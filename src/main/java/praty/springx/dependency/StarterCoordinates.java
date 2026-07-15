package praty.springx.dependency;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Maps Initializr dependency ids to Maven coordinates used in pom.xml / build.gradle.
 */
public final class StarterCoordinates {

    private static final String BOOT_GROUP = "org.springframework.boot";

    private static final Map<String, MavenCoordinates> KNOWN = new HashMap<>();

    static {
        starter("web", "spring-boot-starter-web");
        starter("webflux", "spring-boot-starter-webflux");
        starter("data-jpa", "spring-boot-starter-data-jpa");
        starter("data-mongodb", "spring-boot-starter-data-mongodb");
        starter("data-redis", "spring-boot-starter-data-redis");
        starter("security", "spring-boot-starter-security");
        starter("oauth2-client", "spring-boot-starter-oauth2-client");
        starter("validation", "spring-boot-starter-validation");
        starter("actuator", "spring-boot-starter-actuator");
        starter("devtools", "spring-boot-devtools");
        starter("thymeleaf", "spring-boot-starter-thymeleaf");
        starter("amqp", "spring-boot-starter-amqp");
        starter("kafka", "spring-boot-starter-kafka");
        starter("graphql", "spring-boot-starter-graphql");
        starter("docker-compose", "spring-boot-docker-compose");
        starter("testcontainers", "spring-boot-testcontainers");

        KNOWN.put("lombok", new MavenCoordinates("org.projectlombok", "lombok", "compileOnly"));
        KNOWN.put("h2", new MavenCoordinates("com.h2database", "h2", "runtime"));
        KNOWN.put("spring-ai-openai", new MavenCoordinates("org.springframework.ai", "spring-ai-openai-spring-boot-starter"));
        KNOWN.put("spring-ai-ollama", new MavenCoordinates("org.springframework.ai", "spring-ai-ollama-spring-boot-starter"));
    }

    private StarterCoordinates() {
    }

    public static MavenCoordinates resolve(String dependencyId) {
        String id = normalize(dependencyId);
        MavenCoordinates known = KNOWN.get(id);
        if (known != null) {
            return known;
        }
        if (id.startsWith("spring-ai-")) {
            return new MavenCoordinates("org.springframework.ai", id + "-spring-boot-starter");
        }
        return new MavenCoordinates(BOOT_GROUP, "spring-boot-starter-" + id.replace('_', '-'));
    }

    public static Optional<String> initializrIdForArtifact(String artifactId) {
        if (artifactId == null || artifactId.isBlank()) {
            return Optional.empty();
        }
        for (Map.Entry<String, MavenCoordinates> entry : KNOWN.entrySet()) {
            if (entry.getValue().artifactId().equals(artifactId)) {
                return Optional.of(entry.getKey());
            }
        }
        if (artifactId.startsWith("spring-boot-starter-")) {
            return Optional.of(artifactId.substring("spring-boot-starter-".length()));
        }
        if (artifactId.equals("spring-boot-devtools")) {
            return Optional.of("devtools");
        }
        return Optional.empty();
    }

    private static void starter(String id, String artifactId) {
        KNOWN.put(id, new MavenCoordinates(BOOT_GROUP, artifactId));
    }

    private static String normalize(String dependencyId) {
        return dependencyId == null ? "" : dependencyId.trim().toLowerCase(Locale.ROOT);
    }
}
