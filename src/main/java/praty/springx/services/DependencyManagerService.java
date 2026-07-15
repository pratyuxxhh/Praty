package praty.springx.services;

import praty.modules.spring.DependencyUsageStore;
import praty.springx.config.SpringxConfig;
import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.dependency.BuildFileEditor;
import praty.springx.dependency.CachedDependencyCatalog;
import praty.springx.dependency.DependencyBrowser;
import praty.springx.dependency.DependencyCatalog;
import praty.springx.dependency.ProjectDetector;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;
import praty.springx.network.InitializrMetadata;
import praty.springx.plugin.Hook;
import praty.springx.plugin.PluginRegistry;
import praty.springx.terminal.TerminalUi;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Interactive add/remove dependency workflows.
 */
public final class DependencyManagerService {

    private final BuildFileEditor buildFileEditor;
    private final PluginRegistry pluginRegistry;

    public DependencyManagerService(BuildFileEditor buildFileEditor, PluginRegistry pluginRegistry) {
        this.buildFileEditor = Objects.requireNonNull(buildFileEditor);
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry);
    }

    public Result<List<DependencyRef>> addInteractive(
            TerminalUi ui,
            SpringxConfig config,
            InitializrMetadata metadata
    ) {
        Result<Path> projectDir = ProjectDetector.detectProjectDirectory();
        if (projectDir.isFail()) {
            return Result.fail(projectDir.error().orElseThrow());
        }
        return addInteractive(ui, config, metadata, projectDir.get());
    }

    public Result<List<DependencyRef>> addInteractive(
            TerminalUi ui,
            SpringxConfig config,
            InitializrMetadata metadata,
            Path projectDir
    ) {
        Path project = projectDir.toAbsolutePath().normalize();
        ui.info("Project: " + project);
        ui.info("Build tool: " + ProjectDetector.buildTool(project));

        DependencyCatalog catalog = new CachedDependencyCatalog(metadata);
        List<DependencyRef> installed = ProjectDetector.installedDependencies(project);
        List<Dependency> options = DependencyBrowser.forAdd(catalog, config, installed);
        if (options.isEmpty()) {
            return Result.fail(new SpringxException(
                    "No dependencies available to add.",
                    "Everything in the catalog is already installed.",
                    "Check pom.xml or build.gradle."
            ));
        }

        List<Dependency> selected = ui.multiSelect(
                "Add Dependencies",
                options,
                d -> d.name() + "  (" + d.id() + ")"
        );
        if (selected.isEmpty()) {
            return Result.fail(new SpringxException(
                    "Dependency install cancelled.",
                    "No dependencies selected.",
                    "Run praty spring add when ready."
            ));
        }

        List<DependencyRef> refs = selected.stream().map(d -> new DependencyRef(d.id())).toList();
        preview(ui, "Install", selected);
        if (!ui.confirm("Apply these dependency changes?", true)) {
            return cancelledList();
        }

        pluginRegistry.invoke(Hook.BEFORE_DEPENDENCY_INSTALL, new praty.springx.plugin.Plugin.PluginContext(null, refs));
        Result<Void> applied = buildFileEditor.add(project, refs);
        if (applied.isOk()) {
            pluginRegistry.invoke(Hook.AFTER_DEPENDENCY_INSTALL, new praty.springx.plugin.Plugin.PluginContext(null, refs));
            ui.success("Dependencies added successfully.");
            DependencyUsageStore.recordUsage(refs.stream().map(DependencyRef::id).toList());
            return Result.ok(refs);
        }
        return Result.fail(applied.error().orElseThrow());
    }

    public Result<List<DependencyRef>> removeInteractive(
            TerminalUi ui,
            SpringxConfig config,
            InitializrMetadata metadata
    ) {
        Result<Path> projectDir = ProjectDetector.detectProjectDirectory();
        if (projectDir.isFail()) {
            return Result.fail(projectDir.error().orElseThrow());
        }
        return removeInteractive(ui, config, metadata, projectDir.get());
    }

    public Result<List<DependencyRef>> removeInteractive(
            TerminalUi ui,
            SpringxConfig config,
            InitializrMetadata metadata,
            Path projectDir
    ) {
        Path project = projectDir.toAbsolutePath().normalize();
        ui.info("Project: " + project);

        DependencyCatalog catalog = new CachedDependencyCatalog(metadata);
        List<DependencyRef> installed = ProjectDetector.installedDependencies(project);
        if (installed.isEmpty()) {
            return Result.fail(new SpringxException(
                    "No removable dependencies found.",
                    "Could not detect Initializr-style dependencies in the build file.",
                    "Edit pom.xml or build.gradle manually for custom coordinates."
            ));
        }

        List<Dependency> options = DependencyBrowser.forRemove(catalog, installed);
        List<Dependency> selected = ui.multiSelect(
                "Remove Dependencies",
                options,
                d -> d.name() + "  (" + d.id() + ")"
        );
        if (selected.isEmpty()) {
            return cancelledList();
        }

        List<DependencyRef> refs = selected.stream().map(d -> new DependencyRef(d.id())).toList();
        preview(ui, "Remove", selected);
        if (!ui.confirm("Remove selected dependencies?", false)) {
            return cancelledList();
        }

        Result<Void> applied = buildFileEditor.remove(project, refs);
        if (applied.isOk()) {
            ui.success("Dependencies removed successfully.");
            return Result.ok(refs);
        }
        return Result.fail(applied.error().orElseThrow());
    }

    public static SpringxConfig bumpUsage(SpringxConfig config, List<String> dependencyIds) {
        Map<String, Integer> usage = new LinkedHashMap<>(config.usage());
        for (String id : dependencyIds) {
            usage.put(id, usage.getOrDefault(id, 0) + 1);
        }
        return new SpringxConfig(
                config.theme(),
                config.defaultJavaVersion(),
                config.defaultBuildTool(),
                config.defaultPackagePrefix(),
                config.preferredBootVersion(),
                config.favorites(),
                config.recentProjects(),
                usage,
                config.telemetryEnabled(),
                config.telemetryAsked()
        );
    }

    private static void preview(TerminalUi ui, String action, List<Dependency> dependencies) {
        ui.info(action + " preview:");
        for (Dependency dependency : dependencies) {
            ui.info("  - " + dependency.name() + " (" + dependency.id() + ")");
        }
    }

    private static Result<List<DependencyRef>> cancelledList() {
        return Result.fail(new SpringxException(
                "Dependency change cancelled.",
                "No changes were written.",
                "Run praty spring add or praty spring remove again."
        ));
    }
}
