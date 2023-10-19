package cz.cuni.mff.hrdydo.perfevalCLIEvaluator;

import cz.cuni.mff.hrdydo.Command;
import cz.cuni.mff.hrdydo.evaluation.MeasurementComparisonResultCollection;
import cz.cuni.mff.hrdydo.evaluation.ResultPrinter;
import cz.cuni.mff.hrdydo.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.hrdydo.ExitCode;
import cz.cuni.mff.hrdydo.measurementFactory.MeasurementParser;
import cz.cuni.mff.hrdydo.Samples;
import cz.cuni.mff.hrdydo.measurementFactory.ParserFactory;
import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalCommandFailedException;
import cz.cuni.mff.hrdydo.performanceComparatorFactory.PerformanceComparator;
import cz.cuni.mff.hrdydo.resultDatabase.FileWithResultsData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluateCLICommand implements Command {
    static final int TWO = 2;

    public EvaluateCLICommand(FileWithResultsData[][] inputFiles, ResultPrinter resultPrinter, PerformanceComparator performanceComparator) {
        this.inputFiles = inputFiles;
        assert inputFiles.length == TWO;
        this.resultPrinter = resultPrinter;
        this.performanceComparator = performanceComparator;
    }

    FileWithResultsData[][] inputFiles;
    ResultPrinter resultPrinter;
    PerformanceComparator performanceComparator;

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            MeasurementComparisonResultCollection comparisonResults = evaluateResults(inputFiles, performanceComparator);
            resultPrinter.PrintResults(comparisonResults);
            for (MeasurementComparisonRecord record: comparisonResults) {
                if(!record.testVerdict()) return ExitCode.atLeastOneWorseResult;
            }
        } catch (AssertionError e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.evaluationFailed);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }

    private static MeasurementComparisonResultCollection evaluateResults(FileWithResultsData[][] filesWithResultsData, PerformanceComparator performanceComparator) {
        assert filesWithResultsData.length == TWO;
        MeasurementParser parser = ParserFactory.recognizeParserFactory(filesWithResultsData[0][0].path());
        assert parser != null;
        List<Samples> olderMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[0]).map(FileWithResultsData::path).toArray(String[]::new));
        List<Samples> newerMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[1]).map(FileWithResultsData::path).toArray(String[]::new));
        assert compareTestsFromListsOfMeasurements(newerMeasurements, olderMeasurements);
        MeasurementComparisonResultCollection resultCollection = new MeasurementComparisonResultCollection(filesWithResultsData);
        resultCollection.addAll(compareTestsWithStatistic(olderMeasurements, newerMeasurements, performanceComparator));
        return resultCollection;
    }

    private static boolean compareTestsFromListsOfMeasurements(List<Samples> measurements1, List<Samples> measurements2) {
        if (measurements1.size() != measurements2.size())
            return false;
        for (int i = 0; i < measurements1.size(); i++) {
            if (measurements1.get(i).getName().compareTo(measurements2.get(i).getName()) != 0)
                return false;
        }
        return true;
    }

    private static List<MeasurementComparisonRecord> compareTestsWithStatistic(List<Samples> olderMeasurements, List<Samples> newerMeasurements, PerformanceComparator performanceComparator) {
        List<MeasurementComparisonRecord> resultsOfComparison = new ArrayList<>();
        for (int i = 0; i < newerMeasurements.size(); i++) {
            resultsOfComparison.add(performanceComparator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i)));
        }
        return resultsOfComparison;
    }
}
