package praty.springx.commands;

import org.jline.reader.UserInterruptException;
import praty.command.CommandContext;
import praty.springx.core.AppContainer;
import praty.springx.core.Result;
import praty.springx.model.DependencyRef;
import praty.springx.network.InitializrMetadata;
import praty.springx.services.DependencyManagerService;
import praty.springx.terminal.JLineTerminalUi;

import java.util.List;

/**
 * Interactive dependency browser for the current Spring Boot project.
 */
public final class AddCommand implements SpringCommand {

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.arguments().contains("--help") || ctx.arguments().contains("-h")) {
            System.out.println("Usage: praty spring add");
            System.out.println("Browse and add dependencies to the current Maven/Gradle project.");
            return;
        }

        AppContainer container = AppContainer.get();
        try (JLineTerminalUi ui = JLineTerminalUi.create(container.theme())) {
            Result<InitializrMetadata> metadata = container.metadataService().load(ui);
            if (metadata.isFail()) {
                container.errorReporter().report(metadata);
                return;
            }
            Result<List<DependencyRef>> result = container.dependencyManagerService()
                    .addInteractive(ui, container.config(), metadata.get());
            if (result.isFail()) {
                container.errorReporter().report(result);
                return;
            }
            List<String> ids = result.get().stream().map(DependencyRef::id).toList();
            container.saveConfig(DependencyManagerService.bumpUsage(container.config(), ids));
        } catch (UserInterruptException e) {
            container.errorReporter().report(Result.fail(
                    "Dependency install cancelled.",
                    "Interrupted.",
                    "Run praty spring add when ready."
            ));
        } catch (RuntimeException e) {
            container.errorReporter().reportUnexpected(e);
        }
    }
}
