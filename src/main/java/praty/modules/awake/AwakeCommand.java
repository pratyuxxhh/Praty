package praty.modules.awake;

import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.ArrayType;

import praty.command.Command;
import praty.command.CommandContext;

public class AwakeCommand implements Command {
    @Override
    public void execute(CommandContext ctx) {
        System.out.println("""

            
▀█▀▀▄              █              ▀                              ▀█         
 █▄▄▀ ▀█▄▀▄ ▀▀▀▄  ▀█▀▀ █  █      ▀█  ▄▀▀▀      ▀▀▀▄  █   █ ▀▀▀▄   █ ▄▀ ▄▀▀▀▄
 █     █  ▀ ▄▀▀█   █ ▄ ▀▄▄█       █   ▀▀▄      ▄▀▀█  █ ▄ █ ▄▀▀█   █▀▄  █▀▀▀▀
▀▀▀   ▀▀▀    ▀▀ ▀   ▀     █      ▀▀▀ ▀▀▀        ▀▀ ▀  ▀ ▀   ▀▀ ▀ ▀▀  ▀  ▀▀▀ 
                       ▀▀▀                                                  
        """);
    }

    // print random quotes
   
}