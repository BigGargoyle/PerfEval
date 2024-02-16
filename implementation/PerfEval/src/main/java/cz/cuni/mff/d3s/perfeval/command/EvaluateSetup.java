package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.printers.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.evaluation.PerformanceEvaluator;
import cz.cuni.mff.d3s.perfeval.evaluation.StatisticTest;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;

import java.nio.file.Path;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;

/**
 * Setup for the evaluate command.
 */
public class EvaluateSetup implements CommandSetup {

    /**
     * Name of the command.
     */
    static final String commandName = "evaluate";
    @Override
    public Command setup(OptionSet options, PerfEvalConfig config) throws DatabaseException, ParserException {

        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(options);
        Path userDir = Path.of(System.getProperty("user.dir"));
        ResultPrinter printer = resolvePrinterForEvaluateCommand(options, userDir.resolve(PERFEVAL_DIR));
        StatisticTest statisticTest = resolveStatisticTest(options, config);
        PerformanceEvaluator evaluator = new PerformanceEvaluator(config.getMaxCIWidth(),
                config.getTolerance(), config.getMinTestCount(), config.getMaxTestCount(), statisticTest);
        return new EvaluateCLICommand(inputFiles, printer, evaluator, config.getMeasurementParser());

    }

    /**
     * Returns the name of the command.
     * @return Name of the command.
     */
    public static String getCommandName() {
        return commandName;
    }
}
