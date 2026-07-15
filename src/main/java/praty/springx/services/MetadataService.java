package praty.springx.services;

import praty.springx.cache.MetadataCache;
import praty.springx.config.SpringxConfig;
import praty.springx.core.Result;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.Dependency;
import praty.springx.network.InitializrClient;
import praty.springx.network.InitializrMetadata;
import praty.springx.terminal.TerminalUi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Resolves Initializr metadata from network, cache, or mock fallback.
 */
public final class MetadataService {

    private final InitializrClient initializrClient;
    private final MetadataCache metadataCache;

    public MetadataService(InitializrClient initializrClient, MetadataCache metadataCache) {
        this.initializrClient = Objects.requireNonNull(initializrClient);
        this.metadataCache = Objects.requireNonNull(metadataCache);
    }

    public Result<InitializrMetadata> load(TerminalUi ui) {
        AtomicReference<Result<InitializrMetadata>> fetched = new AtomicReference<>();
        ui.withSpinner("Fetching Spring Boot metadata...", () -> fetched.set(initializrClient.fetchMetadata()));

        Result<InitializrMetadata> online = fetched.get();
        if (online != null && online.isOk()) {
            metadataCache.put(online.get());
            return online;
        }

        Optional<InitializrMetadata> cached = metadataCache.get();
        if (cached.isPresent() && !cached.get().isEmpty()) {
            ui.warn("Using cached Spring Boot metadata (offline).");
            return Result.ok(cached.get());
        }

        if (online != null && online.isFail()) {
            ui.warn(online.error().orElseThrow().getMessage());
        }

        ui.warn("Using built-in dependency catalog (offline fallback).");
        return Result.ok(offlineFallback());
    }

    public static List<String> bootVersions(SpringxConfig config, InitializrMetadata metadata) {
        List<String> versions;
        if (metadata != null && !metadata.bootVersions().isEmpty()) {
            versions = metadata.bootVersions();
        } else {
            versions = MockDependencyData.bootVersions();
        }
        return WizardDefaults.bootVersions(config, versions, metadata == null ? "" : metadata.bootVersionHint());
    }

    public static List<String> bootVersions(InitializrMetadata metadata) {
        if (metadata != null && !metadata.bootVersions().isEmpty()) {
            return metadata.bootVersions();
        }
        return MockDependencyData.bootVersions();
    }

    public static List<Dependency> dependencies(InitializrMetadata metadata) {
        if (metadata != null && !metadata.dependencies().isEmpty()) {
            return metadata.dependencies();
        }
        return MockDependencyData.all();
    }

    private static InitializrMetadata offlineFallback() {
        return new InitializrMetadata(
                MockDependencyData.bootVersions().getFirst(),
                MockDependencyData.bootVersions(),
                List.of("17", "21"),
                MockDependencyData.all(),
                System.currentTimeMillis()
        );
    }
}
