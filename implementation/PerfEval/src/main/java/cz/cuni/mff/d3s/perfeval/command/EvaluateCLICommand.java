package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParserException;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementPrinterException;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.evaluation.PerformanceEvaluator;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementComparisonResultCollection;
import cz.cuni.mff.d3s.perfeval.printers.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;

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
     * @param performanceEvaluator comparator for performance tests
     * @param parser                parser for performance tests result files
     */
    public EvaluateCLICommand(FileWithResultsData[][] inputFiles, ResultPrinter resultPrinter, PerformanceEvaluator performanceEvaluator, MeasurementParser parser) throws ParserException {
        this.inputFiles = inputFiles.clone();
        if(inputFiles.length != TWO){
            throw new ParserException("There must be two versions of performance tests for evaluation.");
        }
        if(inputFiles[0].length == 0 || inputFiles[1].length == 0){
            throw new ParserException("There must be at least one file of each version with results of performance tests for evaluation.");
        }
        this.resultPrinter = resultPrinter;
        this.performanceEvaluator = performanceEvaluator;
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
     * Evaluator for performance tests
     */
    PerformanceEvaluator performanceEvaluator;

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
                MeasurementComparisonResultCollection comparisonResults = evaluateResults(parser, inputFiles, performanceEvaluator);
            resultPrinter.PrintResults(comparisonResults);
            for (MeasurementComparisonRecord record : comparisonResults) {
                if (!record.testVerdict()) return ExitCode.atLeastOneWorseResult;
            }
        } catch (AssertionError | MeasurementPrinterException e) {
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
     * @param performanceEvaluator comparator for performance tests
     * @return collection of results of performance tests evaluation
     */
    private static MeasurementComparisonResultCollection evaluateResults(MeasurementParser parser, FileWithResultsData[][] filesWithResultsData, PerformanceEvaluator performanceEvaluator) throws PerfEvalCommandFailedException {
        assert filesWithResultsData.length == TWO;
        assert parser != null;
        List<Samples> olderMeasurements;
        List<Samples> newerMeasurements;
        try{
            olderMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[0]).map(FileWithResultsData::path).toArray(String[]::new));
            newerMeasurements = parser.getTestsFromFiles(Arrays.stream(filesWithResultsData[1]).map(FileWithResultsData::path).toArray(String[]::new));
        }
        catch (MeasurementParserException e){
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.evaluationFailed);
            exception.initCause(e);
            throw exception;
        }
        if(!compareTestsFromListsOfMeasurements(newerMeasurements, olderMeasurements)){
            throw new PerfEvalCommandFailedException("Lists of tests are not the same in both versions.", ExitCode.evaluationFailed);
        }
        MeasurementComparisonResultCollection resultCollection = new MeasurementComparisonResultCollection(filesWithResultsData);
        //fills resultCollection with results of comparison of performance tests
        resultCollection.addAll(compareTestsWithStatistic(olderMeasurements, newerMeasurements, performanceEvaluator));
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
     * @param performanceEvaluator comparator for performance tests
     * @return list of results of comparison of performance tests
     */
    private static List<MeasurementComparisonRecord> compareTestsWithStatistic(List<Samples> olderMeasurements, List<Samples> newerMeasurements, PerformanceEvaluator performanceEvaluator) {
        List<MeasurementComparisonRecord> resultsOfComparison = new ArrayList<>();
        for (int i = 0; i < newerMeasurements.size(); i++) {
            resultsOfComparison.add(performanceEvaluator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i)));
        }
        return resultsOfComparison;
    }
}
