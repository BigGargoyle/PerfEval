package org.example.Evaluation;

import org.example.MeasurementFactory.IMeasurement;
import org.example.MeasurementFactory.ParserIndustry;
import org.example.MeasurementFactory.UniversalTimeUnit;
import org.example.PerformanceComparatorFactory.ComparatorIndustry;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class PerformanceEvaluator {
    //TODO: na místo newResultFileName a oldResultFileName později přijde databáze a tahání souorů z ní
    public static void Evaluate(String newResultFileName, String oldResultFileName,
                                double critValue, double maxCIWidth, UniversalTimeUnit maxTestTime,
                                PrintStream outputStream, boolean jsonFormat) throws IOException {

        // parsing files
        var parser = ParserIndustry.RecognizeParserFactory(newResultFileName);
        if(parser == null)
            throw new IOException("file format has not been recognized");
        List<IMeasurement> newSet = parser.GetTestsFromFile(newResultFileName);
        List<IMeasurement> oldSet = parser.GetTestsFromFile(oldResultFileName);

        if(newSet == null || oldSet == null)
            throw new IOException("file format has not been recognized");

        // comparing sets of measurements
        if(!IsIdenticalSet(newSet, oldSet))
            throw new IOException("sets of tests are not same");
        List<MeasurementComparisonResult> comparisonResults = CompareSets(newSet, oldSet, critValue,
                maxCIWidth, maxTestTime);

        // printing result
        if(jsonFormat) ResultPrinter.JSONPrinter(comparisonResults, outputStream);
        else ResultPrinter.TablePrinter(comparisonResults, outputStream);

    }

    private static List<MeasurementComparisonResult> CompareSets(List<IMeasurement> newSet, List<IMeasurement> oldSet,
                                                                 double critValue, double maxCIWidth,
                                                                 UniversalTimeUnit maxTestTime) {
        List<MeasurementComparisonResult> result = new ArrayList<>();

        for (int i = 0; i < newSet.size(); i++) {
            var comparator = ComparatorIndustry.GetComparator(critValue, maxCIWidth, maxTestTime);
            var comparisonResult = new MeasurementComparisonResult(critValue, newSet.get(i), oldSet.get(i), comparator);
            result.add(comparisonResult);
        }

        return result;
    }

    private static boolean IsIdenticalSet(List<IMeasurement> set1, List<IMeasurement> set2){
        if(set1.size() != set2.size())
            return false;
        for (int i =0; i<set1.size();i++){
            if(set1.get(i).getName().compareTo(set2.get(i).getName())!=0)
                return false;
        }
        return true;
    }


}
