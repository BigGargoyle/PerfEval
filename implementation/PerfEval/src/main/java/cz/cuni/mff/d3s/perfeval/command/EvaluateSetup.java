package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceComparator;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;

public class EvaluateSetup implements  CommandSetup{

    static final String commandName = "evaluate";

    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {

        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = resolvePrinterForEvaluateCommand(options);
        PerformanceComparator comparator = resolvePerformanceComparatorForEvaluateCommand(options, config);

        return new EvaluateCLICommand(inputFiles, printer, comparator, config.getMeasurementParser());

    }

    public static String getCommandName() {
        return commandName;
    }
}
