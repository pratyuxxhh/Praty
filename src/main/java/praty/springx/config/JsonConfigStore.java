package praty.springx.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.BuildTool;
import praty.springx.model.JavaVersion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JSON-backed {@link ConfigStore} at {@link SpringxPaths#configFile()}.
 */
public final class JsonConfigStore implements ConfigStore {

    private final Path configFile;
    private final ObjectMapper mapper;

    public JsonConfigStore() {
        this(SpringxPaths.configFile());
    }

    public JsonConfigStore(Path configFile) {
        this.configFile = Objects.requireNonNull(configFile, "configFile");
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public Result<SpringxConfig> load() {
        try {
            if (!Files.exists(configFile)) {
                SpringxConfig defaults = SpringxConfig.defaults();
                Result<Void> saved = save(defaults);
                if (saved.isFail()) {
                    return Result.fail(saved.error().orElseThrow());
                }
                return Result.ok(defaults);
            }
            ConfigDto dto = mapper.readValue(configFile.toFile(), ConfigDto.class);
            return Result.ok(dto.toDomain());
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to load springx config.",
                    e.getMessage(),
                    "Check permissions on " + configFile + " or delete it to regenerate defaults."
            ));
        }
    }

    @Override
    public Result<Void> save(SpringxConfig config) {
        try {
            Files.createDirectories(configFile.getParent());
            mapper.writeValue(configFile.toFile(), ConfigDto.fromDomain(config));
            return Result.okVoid();
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to save springx config.",
                    e.getMessage(),
                    "Check write permissions for " + configFile.getParent()
            ));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class ConfigDto {
        public String theme;
        public String defaultJavaVersion;
        public String defaultBuildTool;
        public String defaultPackagePrefix;
        public String preferredBootVersion;
        public List<String> favorites;
        public List<String> recentProjects;
        public Map<String, Integer> usage;
        public boolean telemetryEnabled;
        public boolean telemetryAsked;

        static ConfigDto fromDomain(SpringxConfig config) {
            ConfigDto dto = new ConfigDto();
            dto.theme = config.theme();
            dto.defaultJavaVersion = config.defaultJavaVersion().id();
            dto.defaultBuildTool = config.defaultBuildTool().id();
            dto.defaultPackagePrefix = config.defaultPackagePrefix();
            dto.preferredBootVersion = config.preferredBootVersion();
            dto.favorites = List.copyOf(config.favorites());
            dto.recentProjects = List.copyOf(config.recentProjects());
            dto.usage = new LinkedHashMap<>(config.usage());
            dto.telemetryEnabled = config.telemetryEnabled();
            dto.telemetryAsked = config.telemetryAsked();
            return dto;
        }

        SpringxConfig toDomain() {
            return new SpringxConfig(
                    theme,
                    parseJava(defaultJavaVersion),
                    parseBuild(defaultBuildTool),
                    defaultPackagePrefix,
                    preferredBootVersion,
                    favorites,
                    recentProjects,
                    usage,
                    telemetryEnabled,
                    telemetryAsked
            );
        }

        private static JavaVersion parseJava(String id) {
            if (id == null || id.isBlank()) {
                return JavaVersion.JAVA_21;
            }
            for (JavaVersion v : JavaVersion.values()) {
                if (v.id().equals(id.trim()) || v.name().equalsIgnoreCase(id.trim())) {
                    return v;
                }
            }
            return JavaVersion.JAVA_21;
        }

        private static BuildTool parseBuild(String id) {
            if (id == null || id.isBlank()) {
                return BuildTool.MAVEN;
            }
            String normalized = id.trim().toLowerCase();
            for (BuildTool tool : BuildTool.values()) {
                if (tool.id().equals(normalized) || tool.name().equalsIgnoreCase(normalized)) {
                    return tool;
                }
            }
            return BuildTool.MAVEN;
        }
    }
}
