package praty.springx.wizard;

import org.jline.reader.UserInterruptException;
import praty.springx.config.SpringxConfig;
import praty.springx.core.Result;
import praty.springx.core.SpringxException;
import praty.springx.dependency.MockDependencyData;
import praty.springx.model.BuildTool;
import praty.springx.model.Dependency;
import praty.springx.model.DependencyRef;
import praty.springx.model.JavaVersion;
import praty.springx.model.Language;
import praty.springx.model.Packaging;
import praty.springx.model.ProjectSpec;
import praty.springx.services.WizardDefaults;
import praty.springx.terminal.TerminalUi;
import praty.springx.validation.ProjectValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Interactive new-project wizard (Vite-style) using {@link TerminalUi}.
 * Boot versions and dependencies come from Initializr metadata (live, cache, or fallback).
 */
public final class InteractiveNewProjectWizard implements NewProjectWizard {

    private final TerminalUi ui;
    private final ProjectValidator validator;
    private final SpringxConfig defaults;
    private final List<Dependency> dependencyCatalog;
    private final List<String> bootVersions;

    public InteractiveNewProjectWizard(TerminalUi ui, ProjectValidator validator, SpringxConfig defaults) {
        this(ui, validator, defaults, MockDependencyData.all(), MockDependencyData.bootVersions());
    }

    public InteractiveNewProjectWizard(
            TerminalUi ui,
            ProjectValidator validator,
            SpringxConfig defaults,
            List<Dependency> dependencyCatalog,
            List<String> bootVersions
    ) {
        this.ui = ui;
        this.validator = validator;
        this.defaults = defaults;
        this.dependencyCatalog = dependencyCatalog;
        this.bootVersions = bootVersions;
    }

    @Override
    public Result<ProjectSpec> run() {
        try {
            ui.info("Create a Spring Boot project");
            ui.info("Arrow keys · Enter · Esc · / to search dependencies");

            String projectName = promptProjectName();
            if (projectName == null) {
                return cancelled();
            }

            String packageName = promptPackageName(projectName);
            if (packageName == null) {
                return cancelled();
            }

            Optional<JavaVersion> javaVersion = ui.select(
                    "Java Version",
                    WizardDefaults.javaVersions(defaults),
                    JavaVersion::displayName
            );
            if (javaVersion.isEmpty()) {
                return cancelled();
            }

            Optional<BuildTool> buildTool = ui.select(
                    "Build Tool",
                    WizardDefaults.buildTools(defaults),
                    BuildTool::displayName
            );
            if (buildTool.isEmpty()) {
                return cancelled();
            }

            Optional<Packaging> packaging = ui.select(
                    "Packaging",
                    List.of(Packaging.values()),
                    Packaging::displayName
            );
            if (packaging.isEmpty()) {
                return cancelled();
            }

            Optional<String> bootVersion = ui.select(
                    "Spring Boot Version",
                    bootVersions,
                    v -> v
            );
            if (bootVersion.isEmpty()) {
                return cancelled();
            }

            Optional<Language> language = ui.select(
                    "Language",
                    List.of(Language.values()),
                    Language::displayName
            );
            if (language.isEmpty()) {
                return cancelled();
            }

            List<Dependency> selectedDeps = ui.multiSelect(
                    "Choose Dependencies",
                    dependencyCatalog,
                    d -> d.name() + "  " + dimSecondary(d.id())
            );
            List<DependencyRef> deps = new ArrayList<>();
            for (Dependency dependency : selectedDeps) {
                deps.add(new DependencyRef(dependency.id()));
            }

            String groupId = deriveGroupId(packageName);
            String artifactId = ProjectSpec.normalizeArtifactId(projectName);
            String targetDir = artifactId;

            ProjectSpec spec = ProjectSpec.builder()
                    .projectName(projectName)
                    .packageName(packageName)
                    .groupId(groupId)
                    .artifactId(artifactId)
                    .javaVersion(javaVersion.get())
                    .buildTool(buildTool.get())
                    .packaging(packaging.get())
                    .bootVersion(bootVersion.get())
                    .language(language.get())
                    .dependencies(deps)
                    .targetDirectory(targetDir)
                    .description("Demo project for Spring Boot")
                    .version("0.0.1-SNAPSHOT")
                    .build();

            if (ui instanceof praty.springx.terminal.JLineTerminalUi jline) {
                jline.renderer().summaryCard(spec);
            }

            boolean confirmed = ui.confirm("Create this project?", true);
            if (!confirmed) {
                return cancelled();
            }
            return Result.ok(spec);
        } catch (UserInterruptException e) {
            return cancelled();
        } catch (RuntimeException e) {
            return Result.fail(new SpringxException(
                    "Wizard interrupted.",
                    e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(),
                    "Run praty spring setup again."
            ));
        }
    }

    private String promptProjectName() {
        while (true) {
            String name = ui.textInput(
                    "Project Name",
                    "my-blog",
                    "Example: my-blog"
            );
            Result<Void> valid = validator.validateProjectName(name);
            if (valid.isOk()) {
                return name.trim();
            }
            ui.error(valid.error().orElseThrow().formatForTerminal());
        }
    }

    private String promptPackageName(String projectName) {
        String suggested = defaults.defaultPackagePrefix() + "." + toArtifactId(projectName).replace('-', '_');
        while (true) {
            String pkg = ui.textInput(
                    "Package Name",
                    suggested,
                    "Example: com.example.blog"
            );
            Result<Void> valid = validator.validatePackageName(pkg);
            if (valid.isOk()) {
                return pkg.trim();
            }
            ui.error(valid.error().orElseThrow().formatForTerminal());
        }
    }

    private static Result<ProjectSpec> cancelled() {
        return Result.fail(new SpringxException(
                "Project creation cancelled.",
                "Wizard was cancelled.",
                "Run praty spring setup when you are ready."
        ));
    }

    private static String deriveGroupId(String packageName) {
        String[] parts = packageName.split("\\.");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[1];
        }
        return packageName;
    }

    private static String toArtifactId(String projectName) {
        return ProjectSpec.normalizeArtifactId(projectName);
    }

    private static String dimSecondary(String text) {
        return "(" + text + ")";
    }
}
