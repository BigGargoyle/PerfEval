package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalInvalidConfigException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.USER_DIR;

/**
 * Parser for command line arguments.
 */
public class Parser {

    /**
     * Map of commands and their setup.
     */
    private static final Map<String, Supplier<CommandSetup>> COMMAND_PER_SETUP;
    static {
        COMMAND_PER_SETUP = Map.of(
                InitSetup.getCommandName(), InitSetup::new,
                EvaluateSetup.getCommandName(), EvaluateSetup::new,
                IndexNewSetup.getCommandName(), IndexNewSetup::new,
                IndexAllSetup.getCommandName(), IndexAllSetup::new,
                UndecidedSetup.getCommandName(), UndecidedSetup::new,
                ListResultsSetup.getCommandName(), ListResultsSetup::new
        );
    }

    /**
     * Parses the command line arguments.
     *
     * @param args Command line arguments.
     * @return Command to be executed.
     * @throws ParserException If there is an error with parsing the command line arguments.
     */
    public static Command getCommand(String[] args) throws ParserException {
        OptionParser parser = SetupUtilities.createParser();
        OptionSet options;
        try {
            //runtime exception -> unchecked
            options = parser.parse(args);
        } catch (joptsimple.OptionException e) {
            throw new ParserException("Error while parsing arguments: " + e.getMessage(), ExitCode.invalidArguments);
        }

        if (options.has("h") || options.has(SetupUtilities.HELP_FLAG)) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                throw new ParserException("Error while printing help: " + e.getMessage(), ExitCode.invalidArguments);
            }
            throw new ParserException("Help printed.", ExitCode.OK);
        }

        Path iniFilePath = USER_DIR.resolve(SetupUtilities.PERFEVAL_DIR).resolve(SetupUtilities.INI_FILE_NAME);
        PerfEvalConfig config;
        try {
            config = InitCommand.getConfig(iniFilePath);
        } catch (PerfEvalInvalidConfigException e) {
            return null;
        }
        try {
            for (var arg : options.nonOptionArguments()) {
                if (!COMMAND_PER_SETUP.containsKey(arg.toString())) {
                    continue;
                }
                CommandSetup commandSetup = COMMAND_PER_SETUP.get(arg.toString()).get();
                if (commandSetup != null) {
                    if (Files.isDirectory(USER_DIR.resolve(SetupUtilities.PERFEVAL_DIR)) || commandSetup instanceof InitSetup)
                        return commandSetup.setup(options, config);
                    else
                        throw new ParserException("PerfEval directory does not exist. Run 'init' command first.",
                                ExitCode.invalidArguments);
                }
            }
        } catch (DatabaseException e) {
            ParserException exception = new ParserException("Database error: " + e.getMessage());
            exception.exitCode = ExitCode.databaseError;
            exception.initCause(e);
            throw exception;
        } catch (AssertionError e) {
            throw new ParserException("One of versions has no known measurement results."
                    + System.lineSeparator() + "Assertion error: " + e.getMessage(), e);
        }
        throw new ParserException("No command found."
                + System.lineSeparator() + "Available commands are: "
                + Arrays.toString(COMMAND_PER_SETUP.keySet().stream().sorted().toArray()),
                ExitCode.invalidArguments);
    }
}
