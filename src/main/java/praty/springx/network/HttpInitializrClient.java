package praty.springx.network;

import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.BuildTool;
import praty.springx.model.DependencyRef;
import praty.springx.model.ProjectSpec;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HTTP client for {@code https://start.spring.io}.
 */
public final class HttpInitializrClient implements InitializrClient {

    private final URI metadataUri;
    private final URI starterUri;
    private final HttpClient httpClient;

    public HttpInitializrClient() {
        this("https://start.spring.io");
    }

    public HttpInitializrClient(String baseUrl) {
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.metadataUri = URI.create(normalized);
        this.starterUri = URI.create(normalized + "/starter.zip");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    HttpInitializrClient(URI metadataUri, URI starterUri, HttpClient httpClient) {
        this.metadataUri = Objects.requireNonNull(metadataUri);
        this.starterUri = Objects.requireNonNull(starterUri);
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    @Override
    public Result<InitializrMetadata> fetchMetadata() {
        try {
            HttpRequest request = HttpRequest.newBuilder(metadataUri)
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Result.fail(new SpringxException(
                        "Unable to download Spring Boot metadata.",
                        "Initializr returned HTTP " + response.statusCode() + ".",
                        "Run praty spring doctor"
                ));
            }
            return Result.ok(InitializrMetadataParser.parse(response.body()));
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to download Spring Boot metadata.",
                    e.getMessage() == null ? "Network error." : e.getMessage(),
                    "Run praty spring doctor"
            ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail(new SpringxException(
                    "Unable to download Spring Boot metadata.",
                    "Request interrupted.",
                    "Run praty spring setup again."
            ));
        } catch (IllegalArgumentException e) {
            return Result.fail(new SpringxException(
                    "Unable to parse Spring Boot metadata.",
                    e.getMessage() == null ? "Invalid metadata response." : e.getMessage(),
                    "Run praty spring cache clean and try again."
            ));
        }
    }

    @Override
    public Result<Path> downloadProject(ProjectSpec spec) {
        try {
            String formBody = buildStarterFormBody(spec);
            HttpRequest request = HttpRequest.newBuilder(starterUri)
                    .timeout(Duration.ofMinutes(2))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/octet-stream")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                String body = response.body() == null ? "" : new String(response.body());
                return Result.fail(new SpringxException(
                        "Unable to download Spring Boot project.",
                        "Initializr returned HTTP " + response.statusCode() + (body.isBlank() ? "." : ": " + body),
                        "Check your selections and try again."
                ));
            }
            Path zip = Files.createTempFile("springx-project-", ".zip");
            Files.write(zip, response.body());
            return Result.ok(zip);
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to download Spring Boot project.",
                    e.getMessage() == null ? "Network error." : e.getMessage(),
                    "Run praty spring doctor"
            ));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail(new SpringxException(
                    "Unable to download Spring Boot project.",
                    "Download interrupted.",
                    "Run praty spring setup again."
            ));
        }
    }

    /**
     * Initializr binds {@code /starter.zip} from form fields, not JSON request bodies.
     */
    static String buildStarterFormBody(ProjectSpec spec) {
        StringBuilder body = new StringBuilder();
        appendFormParam(body, "type", projectType(spec.buildTool()));
        appendFormParam(body, "language", spec.language().id());
        appendFormParam(body, "bootVersion", spec.bootVersion());
        appendFormParam(body, "baseDir", spec.effectiveTargetDirectory());
        appendFormParam(body, "groupId", spec.groupId());
        appendFormParam(body, "artifactId", spec.artifactId());
        appendFormParam(body, "name", spec.projectName());
        appendFormParam(body, "description", spec.description());
        appendFormParam(body, "packageName", spec.packageName());
        appendFormParam(body, "packaging", spec.packaging().id());
        appendFormParam(body, "javaVersion", spec.javaVersion().id());
        appendFormParam(body, "version", spec.version());
        if (!spec.dependencies().isEmpty()) {
            String deps = spec.dependencies().stream()
                    .map(DependencyRef::id)
                    .collect(Collectors.joining(","));
            appendFormParam(body, "dependencies", deps);
        }
        return body.toString();
    }

    private static void appendFormParam(StringBuilder body, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!body.isEmpty()) {
            body.append('&');
        }
        body.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
        body.append('=');
        body.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }

    private static String projectType(BuildTool buildTool) {
        return buildTool == BuildTool.GRADLE ? "gradle-project" : "maven-project";
    }
}
