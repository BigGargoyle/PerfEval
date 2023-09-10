package org.example.perfevalCLIEvaluator;

import org.example.Evaluation.IMeasurementComparisonResult;
import org.example.Evaluation.MeasurementComparisonResult;
import org.example.Evaluation.ResultPrinter;
import org.example.GlobalVars;
import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.IMeasurementParser;
import org.example.MeasurementFactory.ParserIndustry;
import org.example.MeasurementFactory.UniversalTimeUnit;
import org.example.PerformanceComparatorFactory.ComparatorIndustry;
import org.example.PerformanceComparatorFactory.ComparisonResult;
import org.example.PerformanceComparatorFactory.IPerformanceComparator;
import org.example.ResultDatabase.IDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PerfEvalEvaluator {
    /**
     * Method that will compare last two benchmark test results from database and print information about tests that do not have enough samples to compare, but there is enough time on test to make enough samples
     *
     * @param database database with data about benchmark test results
     * @param args     CLI arguments that were typed in
     * @return true - evaluation and printing result was successful, false - otherwise
     */
    public static boolean ListUndecidedTestResults(IDatabase database, String[] args) {
        List<List<IMeasurement>> measurements = GetLastTwoMeasurements(database);
        // measurements item count is 2 and measurements[0] is older than measurements[1]
        if (measurements == null)
            return false;
        List<IMeasurementComparisonResult> comparisonResults = CompareMeasurements(measurements);
        if (comparisonResults == null)
            return false;

        ResultPrinter.PrintUndecided(comparisonResults, System.out);

        return true;
    }

    /**
     * Method that will compare last two benchmark test results from database and print information about comparison result for each of benchmark tests
     *
     * @param database database with data about benchmark test results
     * @param args     CLI arguments that were typed in
     * @return true - evaluation and printing result was successful, false - otherwise
     */
    public static boolean EvaluateLastResults(IDatabase database, String[] args) {
        List<List<IMeasurement>> measurements = GetLastTwoMeasurements(database);
        // measurements item count is 2 and measurements[0] is older than measurements[1]
        if (measurements == null || measurements.size() < 2 || measurements.get(0) == null || measurements.get(1) == null)
            return false;
        List<IMeasurementComparisonResult> comparisonResults = CompareMeasurements(measurements);
        if (comparisonResults == null)
            return false;
        if (Contains(args, GlobalVars.jsonOutputFlag))
            ResultPrinter.JSONPrinter(comparisonResults, System.out);
        else
            ResultPrinter.TablePrinter(comparisonResults, System.out);

        SetupExitCode(comparisonResults);

        return true;
    }

    /**
     * Sets nonzero exit code in case of that performance got worse
     *
     * @param comparisonResults list of comparison results of benchmark test comparison
     */
    private static void SetupExitCode(List<IMeasurementComparisonResult> comparisonResults) {
        for (IMeasurementComparisonResult comparisonResult : comparisonResults) {
            if (!comparisonResult.getComparisonVerdict()) {
                // TODO: uncommnet
                // System.exit(GlobalVars.atLeastOneWorseResultExitCode);
                System.exit(GlobalVars.OKExitCode);
                return;
            }
        }
        System.exit(GlobalVars.OKExitCode);
    }

    /**
     * Searches database for two newest benchmark test results
     *
     * @param database database to search results in
     * @return List of parsed files to instances of List of IMeasurements
     */
    private static List<List<IMeasurement>> GetLastTwoMeasurements(IDatabase database) {
        String[] lastResultsFileNames = database.GetLastNResults(2);
        return MeasurementsFromFiles(lastResultsFileNames);
    }

    /**
     * Expects 2 IMeasurements in the list and then compares if the list have identical names of IMeasurements in them.
     * Method will compare IMeasurements on the same indices and then returns List of IMeasurementComparisonResults with results of comparison
     *
     * @param measurements List with two parsed files that are represented as a list of IMeasurements
     * @return list of results from each comparison or null if some step of checking input lists or reading config file failed
     */
    private static List<IMeasurementComparisonResult> CompareMeasurements(List<List<IMeasurement>> measurements) {
        List<IMeasurement> olderMeasurement = measurements.get(0);
        List<IMeasurement> newerMeasurement = measurements.get(1);

        // test if sets of measurements are the same
        if (olderMeasurement.size() != newerMeasurement.size())
            return null;
        for (int i = 0; i < olderMeasurement.size(); i++) {
            if (olderMeasurement.get(i).getName().compareTo(newerMeasurement.get(i).getName()) != 0)
                return null;
        }

        ConfigData configData = ReadConfigFromIniFile();
        if (configData == null)
            return null;

        IPerformanceComparator performanceComparator = ComparatorIndustry.GetComparator(
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
     * @param fileNames array of Strings with relative or absolute paths to files to be parsed
     * @return list of parsed files or null if parsing had failed
     */
    static List<List<IMeasurement>> MeasurementsFromFiles(String[] fileNames) {
        List<List<IMeasurement>> measurements = new ArrayList<>();
        if (fileNames.length == 0) return null;
        IMeasurementParser parser = ParserIndustry.RecognizeParserFactory(fileNames[0]);
        if (parser == null) {
            System.err.println("File format was not recognized");
            return null;
        }
        for (String fileName : fileNames) {
            List<IMeasurement> measurement = parser.GetTestsFromFile(fileName);
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
    static boolean Contains(String[] container, String item) {
        for (String element : container) {
            if (element.compareTo(item) == 0)
                return true;
        }
        return false;
    }

    /**
     * Method reads config data from config file and returns them.
     *
     * @return Instance of ConfigData read from config file or null if there was an error or some config data are missing
     */
    private static ConfigData ReadConfigFromIniFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(GlobalVars.perfevalDir
                    + "/" + GlobalVars.IniFileName));
            double critValue = 0;
            double CIWidth = 0;
            UniversalTimeUnit timeUnit = null;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] splittedLine = line.split(GlobalVars.ColumnDelimiter);
                if (splittedLine[0].compareTo(GlobalVars.critValueSign) == 0) {
                    critValue = Double.parseDouble(splittedLine[1]);
                }
                if (splittedLine[0].compareTo(GlobalVars.maxCIWidthSign) == 0) {
                    CIWidth = Double.parseDouble(splittedLine[1]);
                }
                if (splittedLine[0].compareTo(GlobalVars.maxTimeOnTestSign) == 0) {
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