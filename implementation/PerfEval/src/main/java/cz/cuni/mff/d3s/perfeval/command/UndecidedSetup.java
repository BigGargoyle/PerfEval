package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.printers.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.printers.UndecidedPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.evaluation.PerformanceEvaluator;
import cz.cuni.mff.d3s.perfeval.evaluation.StatisticTest;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;


public class UndecidedSetup implements CommandSetup {

    static final String commandName = "list-undecided";

    @Override
    public Command setup(OptionSet options, PerfEvalConfig config) throws DatabaseException, ParserException {
        StatisticTest statTest = SetupUtilities.resolveStatisticTest(options, config);
        FileWithResultsData[][] inputFiles = SetupUtilities.resolveInputFilesWithRespectToInputtedVersions(options);
        ResultPrinter printer = new UndecidedPrinter(System.out);
        PerformanceEvaluator evaluator = new PerformanceEvaluator(config.getMaxCIWidth(), config.getTolerance(),
                config.getMinTestCount(), config.getMaxTestCount(), statTest);
        return new EvaluateCLICommand(inputFiles, printer, evaluator, config.getMeasurementParser(),
                config.isHighRunDemandAlert(), config.isImprovedPerformanceAlert());
    }

    public static String getCommandName() {
        return commandName;
    }
}
