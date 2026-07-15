package praty.springx.cache;

import praty.springx.core.Result;
import praty.springx.network.InitializrMetadata;

import java.util.Optional;

/**
 * Offline-friendly cache for Initializr metadata.
 */
public interface MetadataCache {

    Optional<InitializrMetadata> get();

    Result<Void> put(InitializrMetadata metadata);

    Result<Void> clear();
}
