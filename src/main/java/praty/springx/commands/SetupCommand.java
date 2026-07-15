package praty.springx.commands;

import org.jline.reader.UserInterruptException;
import praty.command.CommandContext;
import praty.modules.spring.DependencyUsageStore;
import praty.springx.core.AppContainer;
import praty.springx.core.Result;
import praty.springx.model.CreateResult;
import praty.springx.model.DependencyRef;
import praty.springx.model.ProjectSpec;
import praty.springx.network.InitializrMetadata;
import praty.springx.services.MetadataService;
import praty.springx.services.WizardDefaults;
import praty.springx.terminal.JLineTerminalUi;
import praty.springx.terminal.TerminalUi;
import praty.springx.validation.ProjectLocationGuard;
import praty.springx.wizard.InteractiveNewProjectWizard;
import praty.springx.wizard.NewProjectWizard;

import java.util.List;

/**
 * Interactive setup: JLine wizard + Initializr HTTP project creation.
 */
public final class SetupCommand implements SpringCommand {

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.arguments().contains("--help") || ctx.arguments().contains("-h")) {
            System.out.println("Usage: praty spring setup");
            System.out.println("Interactive wizard to create a Spring Boot project.");
            return;
        }

        AppContainer container = AppContainer.get();
        try (JLineTerminalUi ui = JLineTerminalUi.create(container.theme())) {
            Result<InitializrMetadata> metadataResult = container.metadataService().load(ui);
            if (metadataResult.isFail()) {
                container.errorReporter().report(metadataResult);
                return;
            }
            InitializrMetadata metadata = metadataResult.get();

            NewProjectWizard wizard = new InteractiveNewProjectWizard(
                    ui,
                    container.projectValidator(),
                    container.config(),
                    MetadataService.dependencies(metadata),
                    MetadataService.bootVersions(container.config(), metadata)
            );
            Result<ProjectSpec> result = wizard.run();
            if (result.isFail()) {
                container.errorReporter().report(result);
                return;
            }
            ProjectSpec spec = result.get();
            Result<Void> location = ProjectLocationGuard.validateTargetLocation(spec);
            if (location.isFail()) {
                container.errorReporter().report(location);
                return;
            }
            createWithInitializr(ui, container, spec);
            List<String> depIds = spec.dependencies().stream().map(DependencyRef::id).toList();
            DependencyUsageStore.recordUsage(depIds);
            container.saveConfig(WizardDefaults.recordRecentProject(
                    container.config(),
                    spec.effectiveTargetDirectory()
            ));
        } catch (UserInterruptException e) {
            container.errorReporter().report(Result.fail(
                    "Project creation cancelled.",
                    "Interrupted.",
                    "Run praty spring setup when you are ready."
            ));
        } catch (RuntimeException e) {
            container.errorReporter().reportUnexpected(e);
        }
    }

    private void createWithInitializr(TerminalUi ui, AppContainer container, ProjectSpec spec) {
        try (TerminalUi.ProgressHandle progress = ui.progress("Creating Project...")) {
            progress.update(0.1, "Creating Project...");
            progress.update(0.35, "Downloading Spring Boot...");
            Result<CreateResult> created = container.projectCreateService().create(spec);
            progress.update(0.75, "Extracting...");
            progress.update(1.0, "Configuring " + spec.buildTool().displayName() + "...");
            if (created.isOk()) {
                progress.complete("Project Created Successfully");
                ui.info("Location: " + created.get().projectDirectory());
            } else {
                container.errorReporter().report(created);
            }
        }
    }
}
