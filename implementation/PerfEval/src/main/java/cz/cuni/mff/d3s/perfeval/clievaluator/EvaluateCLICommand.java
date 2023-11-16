package cz.cuni.mff.d3s.perfeval.clievaluator;

import cz.cuni.mff.d3s.perfeval.Command;
import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.ParserFactory;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonResultCollection;
import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.PerformanceComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluateCLICommand implements Command {
    static final int TWO = 2;

    public EvaluateCLICommand(FileWithResultsData[][] inputFiles, ResultPrinter resultPrinter, PerformanceComparator performanceComparator, MeasurementParser parser) {
        this.inputFiles = inputFiles;
        assert inputFiles.length == TWO;
        this.resultPrinter = resultPrinter;
        this.performanceComparator = performanceComparator;
        this.parser = parser;
    }

    FileWithResultsData[][] inputFiles;
    ResultPrinter resultPrinter;
    PerformanceComparator performanceComparator;

    MeasurementParser parser;

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

    private MeasurementComparisonResultCollection evaluateResults(FileWithResultsData[][] filesWithResultsData, PerformanceComparator performanceComparator) {
        assert filesWithResultsData.length == TWO;
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
