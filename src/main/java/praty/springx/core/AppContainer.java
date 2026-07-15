package praty.springx.core;

import praty.springx.cache.FileMetadataCache;
import praty.springx.cache.MetadataCache;
import praty.springx.config.ConfigStore;
import praty.springx.config.JsonConfigStore;
import praty.springx.config.SpringxConfig;
import praty.springx.dependency.CompositeBuildFileEditor;
import praty.springx.generator.ProjectGenerator;
import praty.springx.generator.ZipProjectGenerator;
import praty.springx.network.HttpInitializrClient;
import praty.springx.network.InitializrClient;
import praty.springx.plugin.NoopPluginRegistry;
import praty.springx.plugin.PluginRegistry;
import praty.springx.services.DefaultProjectCreateService;
import praty.springx.services.DependencyManagerService;
import praty.springx.services.MetadataService;
import praty.springx.services.ProjectCreateService;
import praty.springx.theme.Theme;
import praty.springx.theme.ThemeRegistry;
import praty.springx.validation.DefaultProjectValidator;
import praty.springx.validation.ProjectValidator;

/**
 * Lightweight composition root for the springx engine.
 */
public final class AppContainer {

    private static volatile AppContainer instance;

    private final ConfigStore configStore;
    private final MetadataCache metadataCache;
    private final InitializrClient initializrClient;
    private final MetadataService metadataService;
    private final ProjectCreateService projectCreateService;
    private final DependencyManagerService dependencyManagerService;
    private final ProjectValidator projectValidator;
    private final ThemeRegistry themeRegistry;
    private final PluginRegistry pluginRegistry;
    private final ErrorReporter errorReporter;
    private SpringxConfig config;

    private AppContainer(
            ConfigStore configStore,
            MetadataCache metadataCache,
            InitializrClient initializrClient,
            ProjectGenerator projectGenerator,
            ProjectValidator projectValidator,
            ThemeRegistry themeRegistry,
            PluginRegistry pluginRegistry,
            ErrorReporter errorReporter
    ) {
        this.configStore = configStore;
        this.metadataCache = metadataCache;
        this.initializrClient = initializrClient;
        this.metadataService = new MetadataService(initializrClient, metadataCache);
        this.projectCreateService = new DefaultProjectCreateService(
                initializrClient,
                projectGenerator,
                pluginRegistry
        );
        this.dependencyManagerService = new DependencyManagerService(
                new CompositeBuildFileEditor(),
                pluginRegistry
        );
        this.projectValidator = projectValidator;
        this.themeRegistry = themeRegistry;
        this.pluginRegistry = pluginRegistry;
        this.errorReporter = errorReporter;
        this.config = loadConfigOrDefaults(configStore, errorReporter);
    }

    public static AppContainer get() {
        if (instance == null) {
            synchronized (AppContainer.class) {
                if (instance == null) {
                    instance = createDefault();
                }
            }
        }
        return instance;
    }

    public static AppContainer createDefault() {
        return new AppContainer(
                new JsonConfigStore(),
                new FileMetadataCache(),
                new HttpInitializrClient(),
                new ZipProjectGenerator(),
                new DefaultProjectValidator(),
                new ThemeRegistry(),
                new NoopPluginRegistry(),
                new ErrorReporter()
        );
    }

    public static AppContainer create(
            ConfigStore configStore,
            MetadataCache metadataCache,
            InitializrClient initializrClient,
            ProjectGenerator projectGenerator,
            ProjectValidator projectValidator,
            ThemeRegistry themeRegistry,
            PluginRegistry pluginRegistry,
            ErrorReporter errorReporter
    ) {
        return new AppContainer(
                configStore,
                metadataCache,
                initializrClient,
                projectGenerator,
                projectValidator,
                themeRegistry,
                pluginRegistry,
                errorReporter
        );
    }

    public static void install(AppContainer container) {
        synchronized (AppContainer.class) {
            instance = container;
        }
    }

    public static void reset() {
        synchronized (AppContainer.class) {
            instance = null;
        }
    }

    public ConfigStore configStore() {
        return configStore;
    }

    public MetadataCache metadataCache() {
        return metadataCache;
    }

    public InitializrClient initializrClient() {
        return initializrClient;
    }

    public MetadataService metadataService() {
        return metadataService;
    }

    public ProjectCreateService projectCreateService() {
        return projectCreateService;
    }

    public DependencyManagerService dependencyManagerService() {
        return dependencyManagerService;
    }

    public ProjectValidator projectValidator() {
        return projectValidator;
    }

    public ThemeRegistry themeRegistry() {
        return themeRegistry;
    }

    public PluginRegistry pluginRegistry() {
        return pluginRegistry;
    }

    public ErrorReporter errorReporter() {
        return errorReporter;
    }

    public SpringxConfig config() {
        return config;
    }

    public Theme theme() {
        return themeRegistry.get(config.theme());
    }

    public Result<SpringxConfig> reloadConfig() {
        Result<SpringxConfig> loaded = configStore.load();
        if (loaded.isOk()) {
            this.config = loaded.get();
        }
        return loaded;
    }

    public Result<Void> saveConfig(SpringxConfig updated) {
        Result<Void> saved = configStore.save(updated);
        if (saved.isOk()) {
            this.config = updated;
        }
        return saved;
    }

    private static SpringxConfig loadConfigOrDefaults(ConfigStore store, ErrorReporter reporter) {
        Result<SpringxConfig> loaded = store.load();
        if (loaded.isOk()) {
            return loaded.get();
        }
        reporter.report(loaded);
        return SpringxConfig.defaults();
    }
}
