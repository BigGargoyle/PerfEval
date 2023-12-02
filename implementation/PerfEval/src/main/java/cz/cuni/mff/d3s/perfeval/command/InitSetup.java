package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.ParserFactory;
import joptsimple.OptionSet;

import java.nio.file.Path;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.BENCHMARK_PARSER_PARAMETER;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.DATABASE_FILE_NAME;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.FORCE_FLAG;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.GIT_IGNORE_FILE_NAME;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.INI_FILE_NAME;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.PERFEVAL_DIR;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.benchmarkParserOption;

public class InitSetup implements CommandSetup{

    static final String commandName = "init";
    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws PerfEvalCommandFailedException {
        Path workingDir = Path.of(args[0]);
        Path perfevalDirPath = workingDir.resolve(PERFEVAL_DIR);
        Path gitIgnorePath = perfevalDirPath.resolve(GIT_IGNORE_FILE_NAME);
        Path iniFilePath = perfevalDirPath.resolve(INI_FILE_NAME);
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DATABASE_FILE_NAME)};
        Path[] gitIgnoredFiles = new Path[]{
                iniFilePath,
                emptyFiles[0]
        };
        MeasurementParser parser = null;
        if(options.has(BENCHMARK_PARSER_PARAMETER)){
            parser = ParserFactory.getParser(benchmarkParserOption.value(options));
        }

        if(parser==null)
            throw new PerfEvalCommandFailedException(
                    "Parser cannot be resolved. Default parser will be used."+System.lineSeparator()+
                            "Possible parsers are: " + ParserFactory.getPossibleNames(),
                    ExitCode.invalidArguments);


        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath,
                emptyFiles, gitIgnoredFiles, config, options.has(FORCE_FLAG));
    }

    @Override
    public String getCommandName() {
        return commandName;
    }
}
