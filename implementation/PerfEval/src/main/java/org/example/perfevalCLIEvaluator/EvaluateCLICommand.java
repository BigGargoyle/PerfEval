package org.example.perfevalCLIEvaluator;

import org.example.ICommand;
import org.example.evaluation.IResultPrinter;
import org.example.evaluation.MeasurementComparisonRecord;
import org.example.globalVariables.ExitCode;
import org.example.measurementFactory.IMeasurementParser;
import org.example.measurementFactory.Measurement;
import org.example.measurementFactory.ParserFactory;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.performanceComparatorFactory.IPerformanceComparator;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.FileWithResultsData;
import org.example.resultDatabase.IDatabase;

import java.util.ArrayList;
import java.util.List;

public class EvaluateCLICommand implements ICommand {
    static final int TWO = 2;

    public EvaluateCLICommand(IDatabase database, IResultPrinter resultPrinter, IPerformanceComparator performanceComparator) {
        this.database = database;
        this.resultPrinter = resultPrinter;
        this.performanceComparator = performanceComparator;
    }

    IDatabase database;
    IResultPrinter resultPrinter;
    IPerformanceComparator performanceComparator;

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            FileWithResultsData[] filesWithResultsData = database.getLastNResults(TWO);
            List<MeasurementComparisonRecord> comparisonResults = evaluateResults(filesWithResultsData, performanceComparator);
            resultPrinter.PrintResults(comparisonResults, filesWithResultsData);
        } catch (DatabaseException | AssertionError e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }

    private static List<MeasurementComparisonRecord> evaluateResults(FileWithResultsData[] filesWithResultsData, IPerformanceComparator performanceComparator) {
        assert filesWithResultsData.length == TWO;
        IMeasurementParser parser = ParserFactory.recognizeParserFactory(filesWithResultsData[0].path());
        assert parser != null;
        List<Measurement> olderMeasurements = parser.getTestsFromFile(filesWithResultsData[0].path());
        List<Measurement> newerMeasurements = parser.getTestsFromFile(filesWithResultsData[1].path());
        assert compareTestsFromListsOfMeasurements(newerMeasurements, olderMeasurements);
        return compareTestsWithStatistic(olderMeasurements, newerMeasurements, performanceComparator);
    }

    private static boolean compareTestsFromListsOfMeasurements(List<Measurement> measurements1, List<Measurement> measurements2) {
        if (measurements1.size() != measurements2.size())
            return false;
        for (int i = 0; i < measurements1.size(); i++) {
            if (measurements1.get(i).name().compareTo(measurements2.get(i).name()) != 0)
                return false;
        }
        return true;
    }

    private static List<MeasurementComparisonRecord> compareTestsWithStatistic(List<Measurement> olderMeasurements, List<Measurement> newerMeasurements, IPerformanceComparator performanceComparator) {
        List<MeasurementComparisonRecord> resultsOfComparison = new ArrayList<>();
        for (int i = 0; i < newerMeasurements.size(); i++) {
            resultsOfComparison.add(performanceComparator.compareSets(olderMeasurements.get(i), newerMeasurements.get(i)));
        }
        return resultsOfComparison;
    }
}
