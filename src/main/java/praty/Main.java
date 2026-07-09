package praty;

import praty.command.Command;
import praty.command.CommandContext;
import praty.command.CommandRegistry;
import praty.command.Parser;
import praty.modules.app.AddNewApplicationToOpen;
import praty.modules.app.ListApplicationsCommand;
import praty.modules.app.OpenApplicationCommand;
import praty.modules.app.RemoveApplicationCommand;
import praty.modules.awake.AwakeCommand;
import praty.modules.cd.ToHomeDirectoryCommand;
import praty.modules.file.CopyCommand;
import praty.modules.file.DeleteCommand;
import praty.modules.file.MoveCommand;
import praty.modules.file.UnzipCommand;
import praty.modules.man.ShowManualCommand;
import praty.modules.restart.RestartCommand;
import praty.modules.shutdown.ShutdownCommand;
import praty.modules.sleep.SleepCommand;
import praty.modules.spring.NewProjectCommand;
import praty.modules.spring.ShowDependencies;
import praty.modules.updates.GetUpdateCommand;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        


        Parser parser = new Parser(args);
        String module = parser.module();
        String action = parser.action();
        List<String> rest = parser.arguments();

        if (module.isEmpty()) {
            System.out.println("No command provided. Try: awake, sleep, or file <action> ...");
            System.exit(1);
        }

        Command command = registry.get(module, action);
        if (command == null) {
            System.out.printf("Unknown command: %s %s%n", module, action);
            System.exit(2);
        }

        CommandContext ctx = new CommandContext(module, action, rest);
        command.execute(ctx);
    }

    public static final CommandRegistry registry = new CommandRegistry();

    static {

        // Register system commands
        registry.register("awake", "", new AwakeCommand());
        registry.register("sleep", "", new SleepCommand());
        registry.register("shutdown", "", new ShutdownCommand());
        registry.register("restart", "", new RestartCommand());

        
        registry.register("check", "update", new GetUpdateCommand());
        registry.register("check", "updates", new GetUpdateCommand());
        registry.register("get", "update", new GetUpdateCommand());
        registry.register("get", "updates", new GetUpdateCommand());

        // Register file module commands (module:action)
        registry.register("file", "cp", new CopyCommand());
        registry.register("file", "mv", new MoveCommand());
        registry.register("file", "-d", new DeleteCommand());
        registry.register("file", "unzip", new UnzipCommand());

        // register app module commands
        registry.register("app", "-add", new AddNewApplicationToOpen());
        registry.register("app", "-o", new OpenApplicationCommand());
        registry.register("app", "-open", new OpenApplicationCommand());
        registry.register("app", "-ls", new ListApplicationsCommand());
        registry.register("app", "-rm", new RemoveApplicationCommand());
        registry.register("app", "-r", new RemoveApplicationCommand());
        
        
        registry.register("cd", "~", new ToHomeDirectoryCommand());
        
        registry.register("spring", "setup", new NewProjectCommand());
        registry.register("spring", "deps", new ShowDependencies());
        registry.register("man", "", new ShowManualCommand());
    }
}

