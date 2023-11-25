package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.resultdatabase.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalInvalidConfigException;

import java.nio.file.Path;
import java.util.Map;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;

public class Parser {

    static Map<String, CommandSetup> commandPerSetup;
    static {
        commandPerSetup = Map.of(
                new InitSetup().getCommandName(), new InitSetup(),
                new EvaluateSetup().getCommandName(), new EvaluateSetup(),
                new IndexNewSetup().getCommandName(), new IndexNewSetup(),
                new IndexAllSetup().getCommandName(), new IndexAllSetup(),
                new UndecidedSetup().getCommandName(), new UndecidedSetup()
        );
    }

    public static Command getCommand(String[] args) {
        OptionParser parser = CreateParser();
        OptionSet options = parser.parse(args);
        Path iniFilePath = Path.of(args[0]).resolve(PERFEVAL_DIR).resolve(INI_FILE_NAME);
        PerfEvalConfig config;
        try {
            config = InitCommand.getConfig(iniFilePath);
        } catch (PerfEvalInvalidConfigException e) {
            return null;
        }
        try {
            for (var arg : options.nonOptionArguments()) {
                CommandSetup commandSetup = commandPerSetup.get(arg.toString());
                if (commandSetup != null) {
                    return commandSetup.setup(args, options, config);
                }
            }
        } catch (DatabaseException e){
            System.err.println(e.getMessage());
        } catch (AssertionError e){
            System.err.println("One of versions has no known measurement results.");
        }
        return null;
    }


}
