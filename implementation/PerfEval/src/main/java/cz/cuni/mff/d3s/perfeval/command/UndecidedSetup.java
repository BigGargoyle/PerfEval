package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import joptsimple.OptionSet;
import org.apache.commons.lang.NotImplementedException;


public class UndecidedSetup implements  CommandSetup{

    static final String commandName = "list-undecided";

    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        /*FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
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
        }*/
        //TODO: implement
        throw new NotImplementedException("UndecidedSetup");
    }

    public static String getCommandName() {
        return commandName;
    }
}
