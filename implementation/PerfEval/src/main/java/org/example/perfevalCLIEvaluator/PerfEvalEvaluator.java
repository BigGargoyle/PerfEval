package org.example.perfevalCLIEvaluator;

import org.example.evaluation.IMeasurementComparisonResult;
import org.example.evaluation.MeasurementComparisonResult;
import org.example.evaluation.ResultPrinter;
import org.example.globalVariables.CLIFlags;
import org.example.globalVariables.DBFlags;
import org.example.globalVariables.ExitCode;
import org.example.globalVariables.FileNames;
import org.example.measurementFactory.Measurement;
import org.example.measurementFactory.IMeasurementParser;
import org.example.measurementFactory.ParserFactory;
import org.example.measurementFactory.UniversalTimeUnit;
import org.example.performanceComparatorFactory.ComparatorFactory;
import org.example.performanceComparatorFactory.ComparisonResult;
import org.example.performanceComparatorFactory.IPerformanceComparator;
import org.example.resultDatabase.DatabaseItem;
import org.example.resultDatabase.IDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerfEvalEvaluator {

    private static final String equalSign = "=";
    private static final String testResultFilter = "test-result";
    private static final String sizeOfChangeFilter = "size-of-change";
    private static final String testIDFilter = "test-id";

    /**
     * Method that will compare last two benchmark test results from database and print information about tests that do not have enough samples to compare, but there is enough time on test to make enough samples
     *
     * @param database database with data about benchmark test results
     * @param args     CLI arguments that were typed in
     * @return true - evaluation and printing result was successful, false - otherwise
     */
    public static boolean listUndecidedTestResults(IDatabase database, String[] args) {
        List<List<Measurement>> measurements = getLastTwoMeasurements(database);
        // measurements item count is 2 and measurements[0] is older than measurements[1]
        if (measurements == null)
            return false;
        List<IMeasurementComparisonResult> comparisonResults = compareMeasurements(measurements);
        if (comparisonResults == null)
            return false;

        ResultPrinter.printUndecided(comparisonResults, System.out);

        return true;
    }

    /**
     * Method that will compare last two benchmark test results from database and print information about comparison result for each of benchmark tests
     *
     * @param database database with data about benchmark test results
     * @param args     CLI arguments that were typed in
     * @return true - evaluation and printing result was successful, false - otherwise
     */
    public static boolean evaluateLastResults(IDatabase database, String[] args) {
        DatabaseItem[] lastTwoItems = database.getLastNResults(2);
        List<List<Measurement>> measurements = getMeasurementsFromFiles(lastTwoItems);
        // measurements item count is 2 and measurements[0] is older than measurements[1]
        if (measurements == null || measurements.size() < 2 || measurements.get(0) == null || measurements.get(1) == null)
            return false;
        List<IMeasurementComparisonResult> comparisonResults = compareMeasurements(measurements);
        if (comparisonResults == null)
            return false;

        if(contains(args, CLIFlags.filterParam)){
            var tmp = filter(comparisonResults, args);
            if(tmp!=null)
                comparisonResults = tmp;
            else
                System.err.println("Unknown filter");
        }

        if (contains(args, CLIFlags.jsonOutputFlag))
            ResultPrinter.JSONPrinter(comparisonResults, System.out, lastTwoItems);
        else
            ResultPrinter.tablePrinter(comparisonResults, System.out, lastTwoItems);

        setupExitCode(comparisonResults);

        return true;
    }

    private static List<IMeasurementComparisonResult> filter(List<IMeasurementComparisonResult> measurementComparisonResults, String[] args){
        String filterBy = null;
        for (int i = 0; i<args.length;i++){
            if(args[i].contains(CLIFlags.filterParam)){
                if(args[i].contains(equalSign)){
                    String[] splitted = args[i].split(equalSign);
                    if(splitted.length<2) return null;
                    filterBy = splitted[1];
                }
                else if(i+1< args.length){
                    filterBy = args[i+1];
                }
            }
        }
        if(filterBy == null) return null;
        switch (filterBy){
            case testResultFilter -> {
                List<IMeasurementComparisonResult> resultList = new ArrayList<>(measurementComparisonResults);
                resultList.sort(Comparator.comparing(IMeasurementComparisonResult::getComparisonResult, Comparator.comparingInt(ComparisonResult::getResultNumber)));
                return resultList;
            }
            case sizeOfChangeFilter -> {
                List<IMeasurementComparisonResult> resultList = new ArrayList<>(measurementComparisonResults);
                resultList.sort(Comparator.comparing(IMeasurementComparisonResult::getChange));
                return resultList;
            }
            case testIDFilter -> {
                List<IMeasurementComparisonResult> resultList = new ArrayList<>(measurementComparisonResults);
                resultList.sort(Comparator.comparing(IMeasurementComparisonResult::getName));
                return resultList;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Sets nonzero exit code in case of that performance got worse
     *
     * @param comparisonResults list of comparison results of benchmark test comparison
     */
    private static void setupExitCode(List<IMeasurementComparisonResult> comparisonResults) {
        for (IMeasurementComparisonResult comparisonResult : comparisonResults) {
            if (!comparisonResult.getComparisonVerdict()) {
                System.exit(ExitCode.atLeastOneWorseResult.getExitCode());
                return;
            }
        }
    }

    /**
     * Searches database for two newest benchmark test results
     *
     * @param database database to search results in
     * @return List of parsed files to instances of List of IMeasurements
     */
    private static List<List<Measurement>> getLastTwoMeasurements(IDatabase database) {
        DatabaseItem[] lastTwoDBItems = database.getLastNResults(2);
        return getMeasurementsFromFiles(lastTwoDBItems);
    }

    /**
     * Expects 2 IMeasurements in the list and then compares if the list have identical names of IMeasurements in them.
     * Method will compare IMeasurements on the same indices and then returns List of IMeasurementComparisonResults with results of comparison
     *
     * @param measurements List with two parsed files that are represented as a list of IMeasurements
     * @return list of results from each comparison or null if some step of checking input lists or reading config file failed
     */
    private static List<IMeasurementComparisonResult> compareMeasurements(List<List<Measurement>> measurements) {
        List<Measurement> olderMeasurement = measurements.get(0);
        List<Measurement> newerMeasurement = measurements.get(1);

        // test if sets of measurements are the same
        if (olderMeasurement.size() != newerMeasurement.size())
            return null;
        for (int i = 0; i < olderMeasurement.size(); i++) {
            if (olderMeasurement.get(i).name().compareTo(newerMeasurement.get(i).name()) != 0)
                return null;
        }

        ConfigData configData = readConfigFromIniFile();
        if (configData == null)
            return null;

        IPerformanceComparator performanceComparator = ComparatorFactory.getComparator(
                configData.critValue,
                configData.maxCIWidth,
                configData.maxTimeOnTest
        );

        List<IMeasurementComparisonResult> comparisonResults = new ArrayList<>();
        for (int i = 0; i < olderMeasurement.size(); i++) {
            IMeasurementComparisonResult comparisonResult =
                    new MeasurementComparisonResult(configData.critValue,
                            newerMeasurement.get(i), olderMeasurement.get(i), performanceComparator);
            comparisonResults.add(comparisonResult);
        }

        return comparisonResults;
    }

    /**
     * Method that parses files which names were given in a fileNames parameter into a List of IMeasurements
     *
     * @param databaseItems array of DatabaseItems with relative or absolute paths to files to be parsed
     * @return list of parsed files or null if parsing had failed
     */
    static List<List<Measurement>> getMeasurementsFromFiles(DatabaseItem[] databaseItems) {
        List<List<Measurement>> measurements = new ArrayList<>();
        if (databaseItems==null || databaseItems.length == 0) return null;
        IMeasurementParser parser = ParserFactory.recognizeParserFactory(databaseItems[0].path());
        if (parser == null) {
            System.err.println("File format was not recognized");
            return null;
        }
        for (DatabaseItem databaseItem : databaseItems) {
            List<Measurement> measurement = parser.getTestsFromFile(databaseItem.path());
            measurements.add(measurement);
        }

        return measurements;
    }

    /**
     * Method checks if item is an element of container
     *
     * @param container String array that would be searched
     * @param item      String that is searched
     * @return true - if item is an element of the container, false - otherwise
     */
    static boolean contains(String[] container, String item) {
        for (String element : container) {
            if (element.contains(item))
                return true;
        }
        return false;
    }

    /**
     * Method reads config data from config file and returns them.
     *
     * @return Instance of ConfigData read from config file or null if there was an error or some config data are missing
     */
    private static ConfigData readConfigFromIniFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FileNames.workingDirectory
                    + "/" + FileNames.IniFileName));
            double critValue = 0;
            double CIWidth = 0;
            UniversalTimeUnit timeUnit = null;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] splittedLine = line.split(DBFlags.ColumnDelimiter);
                if (splittedLine[0].compareTo(DBFlags.critValueSign) == 0) {
                    critValue = Double.parseDouble(splittedLine[1]);
                }
                if (splittedLine[0].compareTo(DBFlags.maxCIWidthSign) == 0) {
                    CIWidth = Double.parseDouble(splittedLine[1]);
                }
                if (splittedLine[0].compareTo(DBFlags.maxTimeOnTestSign) == 0) {
                    long nanoseconds = Long.parseLong(splittedLine[1]);
                    timeUnit = new UniversalTimeUnit(nanoseconds, TimeUnit.NANOSECONDS);
                }
            }
            if (critValue == 0 || CIWidth == 0 || timeUnit == null)
                return null;
            return new ConfigData(critValue, CIWidth, timeUnit);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Represents data read from config file. Just to simplify passing them all to methods
     *
     * @param critValue
     * @param maxCIWidth
     * @param maxTimeOnTest
     */
    record ConfigData(double critValue, double maxCIWidth, UniversalTimeUnit maxTimeOnTest) {
    }
}