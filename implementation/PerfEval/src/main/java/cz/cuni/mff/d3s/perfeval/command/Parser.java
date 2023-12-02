package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalInvalidConfigException;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.CreateParser;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.INI_FILE_NAME;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.PERFEVAL_DIR;

public class Parser {

    static Map<String, Supplier<CommandSetup>> commandPerSetup;
    static {
        commandPerSetup = Map.of(
                InitSetup.getCommandName(), InitSetup::new,
                EvaluateSetup.getCommandName(), EvaluateSetup::new,
                IndexNewSetup.getCommandName(), IndexNewSetup::new,
                IndexAllSetup.getCommandName(),IndexAllSetup::new,
                UndecidedSetup.getCommandName(), UndecidedSetup::new
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
                CommandSetup commandSetup = commandPerSetup.get(arg.toString()).get();
                if (commandSetup != null) {
                    return commandSetup.setup(args, options, config);
                }
            }
        } catch (DatabaseException e){
            ParserException exception = new ParserException("Database error: " + e.getMessage());
            exception.exitCode = ExitCode.databaseError;
            exception.initCause(e);
            throw exception;
        } catch (AssertionError e){
            throw new ParserException("One of versions has no known measurement results."+
                    System.lineSeparator()+"Assertion error: " + e.getMessage(), e);
        } catch (PerfEvalCommandFailedException e) {
            ParserException exception = new ParserException(e.getMessage(), e.exitCode);
            exception.initCause(e);
            throw exception;
        }
        return null;
    }


}
