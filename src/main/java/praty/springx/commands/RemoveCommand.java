package praty.springx.commands;

import org.jline.reader.UserInterruptException;
import praty.command.CommandContext;
import praty.springx.core.AppContainer;
import praty.springx.core.Result;
import praty.springx.network.InitializrMetadata;
import praty.springx.services.DependencyManagerService;
import praty.springx.terminal.JLineTerminalUi;

/**
 * Interactive dependency removal for the current Spring Boot project.
 */
public final class RemoveCommand implements SpringCommand {

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.arguments().contains("--help") || ctx.arguments().contains("-h")) {
            System.out.println("Usage: praty spring remove");
            System.out.println("Browse and remove dependencies from the current Maven/Gradle project.");
            return;
        }

        AppContainer container = AppContainer.get();
        try (JLineTerminalUi ui = JLineTerminalUi.create(container.theme())) {
            Result<InitializrMetadata> metadata = container.metadataService().load(ui);
            if (metadata.isFail()) {
                container.errorReporter().report(metadata);
                return;
            }
            Result<?> result = container.dependencyManagerService()
                    .removeInteractive(ui, container.config(), metadata.get());
            if (result.isFail()) {
                container.errorReporter().report(result);
            }
        } catch (UserInterruptException e) {
            container.errorReporter().report(Result.fail(
                    "Dependency removal cancelled.",
                    "Interrupted.",
                    "Run praty spring remove when ready."
            ));
        } catch (RuntimeException e) {
            container.errorReporter().reportUnexpected(e);
        }
    }
}
