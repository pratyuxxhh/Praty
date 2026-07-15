package praty.springx.config;

import praty.springx.core.Result;

/**
 * Loads and persists {@link SpringxConfig}.
 * Implemented in Phase 2.
 */
public interface ConfigStore {

    Result<SpringxConfig> load();

    Result<Void> save(SpringxConfig config);
}
