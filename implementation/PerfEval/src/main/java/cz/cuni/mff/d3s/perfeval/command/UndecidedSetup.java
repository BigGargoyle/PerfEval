package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.UndecidedPrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceComparator;
import cz.cuni.mff.d3s.perfeval.performancecomparators.TTestPerformanceComparator;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import joptsimple.OptionSet;


import java.time.Duration;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.resolveDuration;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.resolveInputFilesWithRespectToInputtedVersions;


public class UndecidedSetup implements  CommandSetup{

    static final String commandName = "list-undecided";

    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = new UndecidedPrinter(System.out);
        try {
            // tTest is able to response that there are not enough samples
            Duration maxTestDuration = resolveDuration(options, config);
            // TTestPerformanceComparator only, because it is the only one that can return undecided result -> too few samples
            PerformanceComparator comparator = new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), config.getTolerance(), maxTestDuration);
            // Undecided printer -> printing only undecided results
            return new EvaluateCLICommand(inputFiles, printer, comparator, config.getMeasurementParser());
        } catch (ParserException e) {
            throw new DatabaseException("Cannot parse max test duration: " + e.getMessage(), e, ExitCode.invalidArguments);
        }
    }

    public static String getCommandName() {
        return commandName;
    }
}
