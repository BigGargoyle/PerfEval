package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.UndecidedPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceEvaluator;
import cz.cuni.mff.d3s.perfeval.performancecomparators.StatisticTest;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;


public class UndecidedSetup implements  CommandSetup{

    static final String commandName = "list-undecided";

    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        StatisticTest statTest = resolveStatisticTest(options, config);
        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = new UndecidedPrinter(System.out);
        PerformanceEvaluator evaluator = new PerformanceEvaluator(config.getCritValue(), config.getMaxCIWidth(), config.getTolerance(), config.getMaxTestCount(), statTest);
        return new EvaluateCLICommand(inputFiles, printer, evaluator, config.getMeasurementParser());
    }

    public static String getCommandName() {
        return commandName;
    }
}
