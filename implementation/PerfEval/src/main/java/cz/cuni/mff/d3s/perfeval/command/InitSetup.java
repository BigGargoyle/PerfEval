package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalInvalidConfigException;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.ParserFactory;
import joptsimple.OptionSet;

import java.nio.file.Path;

/**
 * Command for initializing a new PerfEval system instance.
 */
public class InitSetup implements CommandSetup {

    /**
     * Name of the command.
     */
    static final String commandName = "init";

    /**
     * Sets up the command.
     *
     * @param options Command line options.
     * @param config  Configuration of the program.
     * @return Command to be executed.
     * @throws ParserException If there is an error with the parser.
     */
    @Override
    public Command setup(OptionSet options, PerfEvalConfig config) throws ParserException {
        Path perfevalDirPath = SetupUtilities.USER_DIR.resolve(SetupUtilities.PERFEVAL_DIR);
        Path gitIgnorePath = perfevalDirPath.resolve(SetupUtilities.GIT_IGNORE_FILE_NAME);
        Path iniFilePath = perfevalDirPath.resolve(SetupUtilities.INI_FILE_NAME);
        MeasurementParser parser = null;
        if (options.has(SetupUtilities.BENCHMARK_PARSER_PARAMETER)) {
            parser = ParserFactory.getParser(SetupUtilities.benchmarkParserOption.value(options));
        }

        if (parser == null) {
            throw new ParserException(
                    "Parser cannot be resolved. Default parser will be used." + System.lineSeparator()
                            + "Possible parsers are: " + ParserFactory.getPossibleNames() + System.lineSeparator()
                            + "Parser name can be specified by --" + SetupUtilities.BENCHMARK_PARSER_PARAMETER,
                    ExitCode.invalidArguments);
        }


        try {
            config.setMeasurementParser(parser);
        } catch (PerfEvalInvalidConfigException e) {
            throw new ParserException(e.toString(), ExitCode.invalidArguments);
        }

        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath,
                config, options.has(SetupUtilities.FORCE_FLAG));
    }

    public static String getCommandName() {
        return commandName;
    }
}
