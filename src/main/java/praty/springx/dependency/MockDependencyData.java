package praty.springx.dependency;

import praty.springx.model.Dependency;
import praty.springx.model.DependencyCategory;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Built-in dependency list used by the wizard until live Initializr metadata (Phase 4/5).
 */
public final class MockDependencyData {

    private MockDependencyData() {
    }

    public static List<Dependency> all() {
        return List.of(
                dep("web", "Spring Web", "Build web, including RESTful, applications using Spring MVC.", DependencyCategory.WEB),
                dep("webflux", "Spring Reactive Web", "Reactive web applications with Spring WebFlux and Netty.", DependencyCategory.REACTIVE),
                dep("data-jpa", "Spring Data JPA", "Persist data in SQL stores with Java Persistence API.", DependencyCategory.DATABASE),
                dep("data-mongodb", "Spring Data MongoDB", "Store data permanently with MongoDB.", DependencyCategory.DATABASE),
                dep("data-redis", "Spring Data Redis", "Redis key-value data store support.", DependencyCategory.CACHING),
                dep("security", "Spring Security", "Highly customizable authentication and access-control.", DependencyCategory.SECURITY),
                dep("oauth2-client", "OAuth2 Client", "OAuth 2.0 / OpenID Connect client support.", DependencyCategory.SECURITY),
                dep("validation", "Validation", "Bean Validation with Hibernate Validator.", DependencyCategory.WEB),
                dep("actuator", "Spring Boot Actuator", "Production-ready endpoints for monitoring.", DependencyCategory.MONITORING),
                dep("lombok", "Lombok", "Java annotation library to reduce boilerplate.", DependencyCategory.DEVELOPER_TOOLS),
                dep("devtools", "Spring Boot DevTools", "Hot swapping and live reload for development.", DependencyCategory.DEVELOPER_TOOLS),
                dep("thymeleaf", "Thymeleaf", "Modern server-side Java template engine.", DependencyCategory.TEMPLATE_ENGINES),
                dep("amqp", "Spring for RabbitMQ", "Messaging with RabbitMQ using Spring AMQP.", DependencyCategory.MESSAGING),
                dep("kafka", "Spring for Apache Kafka", "Publish/subscribe messaging with Kafka.", DependencyCategory.MESSAGING),
                dep("graphql", "Spring for GraphQL", "Build GraphQL applications with Spring.", DependencyCategory.GRAPHQL),
                dep("docker-compose", "Docker Compose Support", "Integration with Docker Compose for local services.", DependencyCategory.DEVELOPER_TOOLS),
                dep("spring-ai-openai", "Spring AI OpenAI", "Spring AI integration for OpenAI models.", DependencyCategory.AI),
                dep("spring-ai-ollama", "Spring AI Ollama", "Spring AI integration for local Ollama models.", DependencyCategory.AI),
                dep("testcontainers", "Testcontainers", "Throwaway containers for integration testing.", DependencyCategory.TESTING),
                dep("h2", "H2 Database", "In-memory / lightweight database for development.", DependencyCategory.DATABASE)
        );
    }

    public static List<String> bootVersions() {
        return List.of("3.4.1", "3.3.7", "3.2.12");
    }

    public static List<Dependency> search(String query) {
        if (query == null || query.isBlank()) {
            return all();
        }
        String q = query.toLowerCase(Locale.ROOT).trim();
        return all().stream()
                .filter(d -> d.id().toLowerCase(Locale.ROOT).contains(q)
                        || d.name().toLowerCase(Locale.ROOT).contains(q)
                        || d.description().toLowerCase(Locale.ROOT).contains(q)
                        || d.category().name().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }

    private static Dependency dep(String id, String name, String description, DependencyCategory category) {
        return new Dependency(id, name, description, "", "", category, "");
    }
}
