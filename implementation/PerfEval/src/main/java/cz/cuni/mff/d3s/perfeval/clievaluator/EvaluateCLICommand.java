package cz.cuni.mff.d3s.perfeval.clievaluator;

import cz.cuni.mff.d3s.perfeval.command.Command;
import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonResultCollection;
import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command for evaluating results of performance tests
 */
public class EvaluateCLICommand implements Command {
    /**
     * Number of input files for evaluation (two versions of performance tests)
     */
    static final int TWO = 2;

    /**
     * Constructor for EvaluateCLICommand
     *
     * @param inputFiles            files with results of performance tests
     * @param resultPrinter         printer for results
     * @param performanceComparator comparator for performance tests
     * @param parser                parser for performance tests result files
     */
    public EvaluateCLICommand(FileWithResultsData[][] inputFiles, ResultPrinter resultPrinter, PerformanceComparator performanceComparator, MeasurementParser parser) {
        this.inputFiles = inputFiles;
        assert inputFiles.length == TWO;
        this.resultPrinter = resultPrinter;
        this.performanceComparator = performanceComparator;
        this.parser = parser;
    }

    /**
     * 2D array of file with results of performance tests metadata
     * First dimension is for versions of performance tests
     * Second dimension is for files with results of performance tests of one version
     */
    FileWithResultsData[][] inputFiles;
    /**
     * Printer for results
     */
    ResultPrinter resultPrinter;
    /**
     * Comparator for performance tests
     */
    PerformanceComparator performanceComparator;

    /**
     * Parser for performance tests result files
     */
    MeasurementParser parser;

    /**
     * Executes evaluation of performance tests results
     *
     * @return exit code of evaluation, OK if evaluation was successful, atLeastOneWorseResult if at least one test was worse in newer version
     * @throws PerfEvalCommandFailedException if evaluation fails
     */
    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            MeasurementComparisonResultCollection comparisonResults = evaluateResults(inputFiles, performanceComparator);
            resultPrinter.PrintResults(comparisonResults);
            for (MeasurementComparisonRecord record : comparisonResults) {
                if (!record.testVerdict()) return ExitCode.atLeastOneWorseResult;
            }
        } catch (AssertionError e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.evaluationFailed);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }

    /**
     * Evaluates results of performance tests
     *
     * @param filesWithResultsData  files with results of performance tests metadata
     * @param performanceComparator comparator for performance tests
     * @return collection of results of performance tests evaluation
     */
    private MeasurementComparisonResultCollection evaluateResults(FileWithResultsData[][] filesWithResultsData, PerformanceComparator performanceComparator) {
        assert filesWithResultsData.length == TWO;
        assert parser != null;
        List<Samples> olderMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[0]).map(FileWithResultsData::path).toArray(String[]::new));
        List<Samples> newerMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[1]).map(FileWithResultsData::path).toArray(String[]::new));
        assert compareTestsFromListsOfMeasurements(newerMeasurements, olderMeasurements);
        MeasurementComparisonResultCollection resultCollection = new MeasurementComparisonResultCollection(filesWithResultsData);
        //fills resultCollection with results of comparison of performance tests
        resultCollection.addAll(compareTestsWithStatistic(olderMeasurements, newerMeasurements, performanceComparator));
        return resultCollection;
    }

    /**
     * Compares tests from two lists of measurements, checks if they are the lists of the same tests
     *
     * @param measurements1 first list of measurements
     * @param measurements2 second list of measurements
     * @return true if lists of measurements are the same, false otherwise
     */
    private static boolean compareTestsFromListsOfMeasurements(List<Samples> measurements1, List<Samples> measurements2) {
        if (measurements1.size() != measurements2.size())
            return false;
        for (int i = 0; i < measurements1.size(); i++) {
            if (measurements1.get(i).getName().compareTo(measurements2.get(i).getName()) != 0)
                return false;
        }
        return true;
    }

    /**
     * Compares tests from two lists of measurements with statistic method (statistic method is implemented in performance comparator
     *
     * @param olderMeasurements     first list of measurements
     * @param newerMeasurements     second list of measurements
     * @param performanceComparator comparator for performance tests
     * @return list of results of comparison of performance tests
     */
    private static List<MeasurementComparisonRecord> compareTestsWithStatistic(List<Samples> olderMeasurements, List<Samples> newerMeasurements, PerformanceComparator performanceComparator) {
        List<MeasurementComparisonRecord> resultsOfComparison = new ArrayList<>();
        for (int i = 0; i < newerMeasurements.size(); i++) {
            resultsOfComparison.add(performanceComparator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i)));
        }
        return resultsOfComparison;
    }
}
