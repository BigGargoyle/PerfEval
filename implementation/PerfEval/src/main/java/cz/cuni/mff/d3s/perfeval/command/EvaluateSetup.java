package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceEvaluator;
import cz.cuni.mff.d3s.perfeval.performancecomparators.StatisticTest;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;

public class EvaluateSetup implements  CommandSetup{

    static final String commandName = "evaluate";

    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException, ParserException {

        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = resolvePrinterForEvaluateCommand(options);
        StatisticTest statisticTest = resolveStatisticTest(options, config);
        PerformanceEvaluator evaluator = new PerformanceEvaluator(config.getCritValue(), config.getMaxCIWidth(), config.getTolerance(), config.getMaxTestCount(), statisticTest);


        return new EvaluateCLICommand(inputFiles, printer, evaluator, config.getMeasurementParser());

    }

    public static String getCommandName() {
        return commandName;
    }
}
