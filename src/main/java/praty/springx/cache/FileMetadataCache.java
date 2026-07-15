package praty.springx.cache;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import praty.springx.config.SpringxPaths;
import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyCategory;
import praty.springx.network.InitializrMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * File-backed metadata cache at {@link SpringxPaths#initializrMetadataCache()}.
 */
public final class FileMetadataCache implements MetadataCache {

    private final Path cacheFile;
    private final ObjectMapper mapper;

    public FileMetadataCache() {
        this(SpringxPaths.initializrMetadataCache());
    }

    public FileMetadataCache(Path cacheFile) {
        this.cacheFile = Objects.requireNonNull(cacheFile, "cacheFile");
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public Optional<InitializrMetadata> get() {
        if (!Files.exists(cacheFile)) {
            return Optional.empty();
        }
        try {
            CacheDto dto = mapper.readValue(cacheFile.toFile(), CacheDto.class);
            return Optional.of(dto.toDomain());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Result<Void> put(InitializrMetadata metadata) {
        try {
            Files.createDirectories(cacheFile.getParent());
            mapper.writeValue(cacheFile.toFile(), CacheDto.fromDomain(metadata));
            return Result.okVoid();
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to write Initializr metadata cache.",
                    e.getMessage(),
                    "Check write permissions for " + cacheFile.getParent()
            ));
        }
    }

    @Override
    public Result<Void> clear() {
        try {
            Files.deleteIfExists(cacheFile);
            return Result.okVoid();
        } catch (IOException e) {
            return Result.fail(new SpringxException(
                    "Unable to clear Initializr metadata cache.",
                    e.getMessage(),
                    "Run: praty spring cache clean"
            ));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class CacheDto {
        public String bootVersionHint;
        public List<String> bootVersions;
        public List<String> javaVersions;
        public List<DependencyDto> dependencies;
        public List<String> dependencyIds;
        public long fetchedAtEpochMs;

        static CacheDto fromDomain(InitializrMetadata metadata) {
            CacheDto dto = new CacheDto();
            dto.bootVersionHint = metadata.bootVersionHint();
            dto.bootVersions = List.copyOf(metadata.bootVersions());
            dto.javaVersions = List.copyOf(metadata.javaVersions());
            dto.dependencies = metadata.dependencies().stream().map(DependencyDto::from).toList();
            dto.dependencyIds = metadata.dependencyIds();
            dto.fetchedAtEpochMs = metadata.fetchedAtEpochMs();
            return dto;
        }

        InitializrMetadata toDomain() {
            List<Dependency> deps;
            if (dependencies != null && !dependencies.isEmpty()) {
                deps = dependencies.stream().map(DependencyDto::toDomain).toList();
            } else if (dependencyIds != null && !dependencyIds.isEmpty()) {
                deps = dependencyIds.stream()
                        .map(id -> new Dependency(id, id, "", "", "", DependencyCategory.OTHER, ""))
                        .toList();
            } else {
                deps = List.of();
            }
            return new InitializrMetadata(
                    bootVersionHint,
                    bootVersions,
                    javaVersions,
                    deps,
                    fetchedAtEpochMs
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class DependencyDto {
        public String id;
        public String name;
        public String description;

        static DependencyDto from(Dependency dependency) {
            DependencyDto dto = new DependencyDto();
            dto.id = dependency.id();
            dto.name = dependency.name();
            dto.description = dependency.description();
            return dto;
        }

        Dependency toDomain() {
            return new Dependency(
                    id,
                    name == null || name.isBlank() ? id : name,
                    description == null ? "" : description,
                    "",
                    "",
                    DependencyCategory.OTHER,
                    ""
            );
        }
    }
}
