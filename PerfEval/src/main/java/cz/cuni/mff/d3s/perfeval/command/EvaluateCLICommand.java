package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.evaluation.ComparisonResult;
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
import org.apache.commons.math3.stat.StatUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

/**
 * Command for evaluating results of performance tests.
 */
public class EvaluateCLICommand implements Command {
    /**
     * Number of input files for evaluation (two versions of performance tests).
     */
    private static final int TWO = 2;

    /**
     * Constructor for EvaluateCLICommand.
     *
     * @param inputFiles            files with results of performance tests
     * @param resultPrinter         printer for results
     * @param performanceEvaluator comparator for performance tests
     * @param parser                parser for performance tests result files
     */
    public EvaluateCLICommand(FileWithResultsData[][] inputFiles, ResultPrinter resultPrinter,
                              PerformanceEvaluator performanceEvaluator, MeasurementParser parser,
                              boolean HighRunDemandAlert, boolean ImprovedPerformanceAlert
                              ) throws ParserException {
        this.inputFiles = inputFiles.clone();
        if (inputFiles.length != TWO) {
            throw new ParserException("There must be two versions of performance tests for evaluation.");
        }
        if (inputFiles[0].length == 0 || inputFiles[1].length == 0) {
            throw new ParserException("There must be at least one file of each version with results of performance tests for evaluation.");
        }
        this.resultPrinter = resultPrinter;
        this.performanceEvaluator = performanceEvaluator;
        this.parser = parser;
        this.HighRunDemandAlert = HighRunDemandAlert;
        this.ImprovedPerformanceAlert = ImprovedPerformanceAlert;
    }

    /**
     * 2D array of file with results of performance tests metadata.
     * First dimension is for versions of performance tests.
     * Second dimension is for files with results of performance tests of one version.
     */
    private final FileWithResultsData[][] inputFiles;
    /**
     * Printer for results.
     */
    private final ResultPrinter resultPrinter;
    /**
     * Evaluator for performance tests.
     */
    private final PerformanceEvaluator performanceEvaluator;

    /**
     * Parser for performance tests result files.
     */
    private final MeasurementParser parser;

    /**
     * HighRunDemandAlert flag.
     */
    private final boolean HighRunDemandAlert;

    /**
     * ImprovedPerformanceAlert flag.
     */
    private final boolean ImprovedPerformanceAlert;

    /**
     * Executes evaluation of performance tests results.
     *
     * @return exit code of evaluation, OK if evaluation was successful, atLeastOneWorseResult if at least one test was worse in newer version
     * @throws PerfEvalCommandFailedException if evaluation fails
     */
    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            MeasurementComparisonResultCollection comparisonResults = evaluateResults(parser, inputFiles, performanceEvaluator);
            resultPrinter.PrintResults(comparisonResults);
            return resolveExitCode(comparisonResults);
        } catch (AssertionError | MeasurementPrinterException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.evaluationFailed);
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * Resolves exit code of evaluation.
     * @param comparisonResults collection of results of performance tests evaluation
     * @return exit code of evaluation
     */
    private ExitCode resolveExitCode(MeasurementComparisonResultCollection comparisonResults) {
        for (MeasurementComparisonRecord record : comparisonResults) {
            if (!record.testVerdict() && record.comparisonResult() != ComparisonResult.UnableToMeasureEnoughSamples) {
                return ExitCode.atLeastOneWorseResult;
            }
            if (HighRunDemandAlert) {
                if (record.comparisonResult() == ComparisonResult.UnableToMeasureEnoughSamples) {
                    return ExitCode.highRunDemand;
                }
            }
            if (ImprovedPerformanceAlert) {
                // If test passed and the distribution is different, the performance improved
                if (record.testVerdict() && record.comparisonResult() == ComparisonResult.DifferentDistribution) {
                    return ExitCode.improvedPerformance;
                }
            }
        }
        return ExitCode.OK;
    }

    /**
     * Evaluates results of performance tests.
     *
     * @param filesWithResultsData  files with results of performance tests metadata
     * @param performanceEvaluator comparator for performance tests
     * @return collection of results of performance tests evaluation
     */
    private static MeasurementComparisonResultCollection evaluateResults(MeasurementParser parser, FileWithResultsData[][] filesWithResultsData,
                                                                         PerformanceEvaluator performanceEvaluator) throws PerfEvalCommandFailedException {
        assert filesWithResultsData.length == TWO;
        assert parser != null;

        List<Samples> olderMeasurements = getMeasurements(parser, filesWithResultsData[0]);
        List<Samples> newerMeasurements = getMeasurements(parser, filesWithResultsData[1]);

        List<Samples> commonOldMeasurements = filterCommonMeasurements(olderMeasurements, newerMeasurements);
        List<Samples> uniqueOldMeasurements = filterUniqueMeasurements(olderMeasurements, newerMeasurements);
        List<Samples> commonNewMeasurements = filterCommonMeasurements(newerMeasurements, olderMeasurements);
        List<Samples> uniqueNewMeasurements = filterUniqueMeasurements(newerMeasurements, olderMeasurements);


        if (!compareTestsFromListsOfMeasurements(commonNewMeasurements, commonOldMeasurements)) {
            throw new PerfEvalCommandFailedException("Lists of tests are not the same in both versions.", ExitCode.evaluationFailed);
        }
        MeasurementComparisonResultCollection resultCollection = new MeasurementComparisonResultCollection(filesWithResultsData);
        //fills resultCollection with results of comparison of performance tests
        resultCollection.addAll(compareTestsWithStatistic(commonOldMeasurements, commonNewMeasurements, performanceEvaluator));
        //adds results of comparison of performance tests that are only in older version
        resultCollection.addAll(createComparisonRecords(uniqueOldMeasurements, ComparisonResult.OnlyOlderSamples));
        resultCollection.addAll(createComparisonRecords(uniqueNewMeasurements, ComparisonResult.OnlyNewerSamples));

        return resultCollection;
    }

    /**
     * Gets measurements from files with results of performance tests.
     * @param parser parser for performance tests result files
     * @param filesWithResultsData files with results of performance tests metadata
     * @return list of measurements
     * @throws PerfEvalCommandFailedException if getting measurements fails
     */
    private static List<Samples> getMeasurements(MeasurementParser parser, FileWithResultsData[] filesWithResultsData) throws PerfEvalCommandFailedException {
        try {
            return parser.getTestsFromFiles(Arrays.stream(filesWithResultsData).map(FileWithResultsData::path).toArray(String[]::new));
        } catch (MeasurementParserException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.evaluationFailed);
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * Filters common measurements from two lists of measurements.
     * @param measurements1 list of measurements to be filtered
     * @param measurements2 list of measurements for filtering
     * @return list of common measurements
     */
    private static List<Samples> filterCommonMeasurements(List<Samples> measurements1, List<Samples> measurements2) {
        return measurements1.stream()
                .filter(m1 -> measurements2.stream().anyMatch(m2 -> m2.getName().equals(m1.getName())))
                .sorted(Comparator.comparing(Samples::getName))
                .toList();
    }

    /**
     * Filters unique measurements from two lists of measurements.
     * @param measurements1 list of measurements to be filtered
     * @param measurements2 list of measurements for filtering
     * @return list of unique measurements
     */
    private static List<Samples> filterUniqueMeasurements(List<Samples> measurements1, List<Samples> measurements2) {
        return measurements1.stream()
                .filter(m1 -> measurements2.stream().noneMatch(m2 -> m2.getName().equals(m1.getName())))
                .toList();
    }

    /**
     * Creates mock comparison records for measurements that are only in one version of performance tests.
     * @param measurements list of measurements
     * @param comparisonResult result of comparison
     * @return list of comparison records
     */
    private static List<MeasurementComparisonRecord> createComparisonRecords(List<Samples> measurements, ComparisonResult comparisonResult) {
        return measurements.stream().map(
                measurement -> {
                        double mean = StatUtils.mean(Arrays.stream(measurement.getRawData()).flatMapToDouble(Arrays::stream).toArray());
                        double oldAverage = comparisonResult == ComparisonResult.OnlyOlderSamples ? mean : 0;
                        double newAverage = comparisonResult == ComparisonResult.OnlyNewerSamples ? mean : 0;
                        return new MeasurementComparisonRecord(
                        oldAverage, newAverage,0,
                        0,0,comparisonResult,
                        true,-1,measurement,measurement);
                }
        ).toList();
    }

    /**
     * Compares tests from two lists of measurements, checks if they are the lists of the same tests.
     *
     * @param measurements1 first list of measurements
     * @param measurements2 second list of measurements
     * @return true if lists of measurements are the same, false otherwise
     */
    private static boolean compareTestsFromListsOfMeasurements(List<Samples> measurements1, List<Samples> measurements2) {
        if (measurements1.size() != measurements2.size()) {
            return false;
        }
        for (int i = 0; i < measurements1.size(); i++) {
            if (measurements1.get(i).getName().compareTo(measurements2.get(i).getName()) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares tests from two lists of measurements with statistic method (statistic method is implemented in performance comparator.
     *
     * @param olderMeasurements     first list of measurements
     * @param newerMeasurements     second list of measurements
     * @param performanceEvaluator comparator for performance tests
     * @return list of results of comparison of performance tests
     */
    public static List<MeasurementComparisonRecord> compareTestsWithStatistic(List<Samples> olderMeasurements, List<Samples> newerMeasurements, PerformanceEvaluator performanceEvaluator) {
        // Create a thread-safe list to hold the results
        List<MeasurementComparisonRecord> resultsOfComparison = new CopyOnWriteArrayList<>();

        // Use a traditional for loop to iterate over indices
        /*for (int i = 0; i < newerMeasurements.size(); i++) {
            MeasurementComparisonRecord result = performanceEvaluator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i));
            resultsOfComparison.add(result);
        }*/

        // Use IntStream to iterate over indices in parallel
        IntStream.range(0, newerMeasurements.size()).parallel().forEach(i -> {
            MeasurementComparisonRecord result = performanceEvaluator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i));
            resultsOfComparison.add(result);
        });

        return resultsOfComparison;
    }
}
