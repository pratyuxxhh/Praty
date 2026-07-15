package praty.springx.services;

import praty.springx.core.Result;
import praty.springx.generator.ProjectGenerator;
import praty.springx.model.CreateResult;
import praty.springx.model.ProjectSpec;
import praty.springx.network.InitializrClient;
import praty.springx.plugin.Hook;
import praty.springx.plugin.PluginRegistry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Downloads a project from Initializr and extracts it locally.
 */
public final class DefaultProjectCreateService implements ProjectCreateService {

    private final InitializrClient initializrClient;
    private final ProjectGenerator projectGenerator;
    private final PluginRegistry pluginRegistry;

    public DefaultProjectCreateService(
            InitializrClient initializrClient,
            ProjectGenerator projectGenerator,
            PluginRegistry pluginRegistry
    ) {
        this.initializrClient = Objects.requireNonNull(initializrClient);
        this.projectGenerator = Objects.requireNonNull(projectGenerator);
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry);
    }

    @Override
    public Result<CreateResult> create(ProjectSpec spec) {
        pluginRegistry.invoke(Hook.BEFORE_CREATE, new praty.springx.plugin.Plugin.PluginContext(spec, null));

        Result<Path> downloaded = initializrClient.downloadProject(spec);
        if (downloaded.isFail()) {
            return Result.fail(downloaded.error().orElseThrow());
        }

        Path zipFile = downloaded.get();
        Result<CreateResult> extracted = projectGenerator.extract(zipFile, spec);
        if (extracted.isOk()) {
            try {
                Files.deleteIfExists(zipFile);
            } catch (Exception ignored) {
                // zip may already be deleted by generator
            }
            pluginRegistry.invoke(Hook.AFTER_CREATE, new praty.springx.plugin.Plugin.PluginContext(spec, extracted.get()));
        }
        return extracted;
    }
}
