import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static double criticalValue = 0.05;
    static String helpFileName = "help.txt";
    static String helpFlag = "--help";
    static String graphicalFlag = "--graphical";
    static int atLeastOneWorseResultExitCode = 1;
    static int invalidArgumentsExitCode = 2;
    public static void main(String[] args) {
        /*
        * Basic version that accepts 2 benchmark results files as input
        * According to these files the parser will be constructed
        * Content of files will be parsed to test objects and compared
        * by the statistic t-test (Student's test)
        * */

        if(WriteHelpMessage(args)){
            return;
        }
        if(!ContainsGraphical(args)){
            // application should have exit from this function
            CompareTwoResults(args);
            // ensuring about exit
            return;
        }
    }

    static void CompareTwoResults(String[] args){
        var fileNames = GetFilesFromArg(args);
        if(fileNames == null || fileNames.length!=2){
            System.out.println("Benchmark result files not found");
            System.exit(invalidArgumentsExitCode);
            // return;
        }

        IMeasurementParser parser = ParserIndustry.RecognizeParserFactory(fileNames[0]);
        assert parser != null;
        var testSet1 = parser.GetTestsFromFile(fileNames[0]);
        var testSet2 = parser.GetTestsFromFile(fileNames[1]);
        if(testSet1.size() != testSet2.size()){
            System.out.println("Benchmark results are not made on the same set of tests");
            System.exit(invalidArgumentsExitCode);
            return;
        }

        for(int i = 0; i < testSet1.size(); i++){
            if(testSet1.get(i).getName().compareTo(testSet2.get(i).getName()) != 0){
                System.out.println("Benchmark results are not made on the same set of tests");
                System.exit(invalidArgumentsExitCode);
                return;
            }
        }

        var comparison = CompareSets(testSet1,testSet2);
        PrintResult(comparison);

        boolean worseBehaviour = false;
        for(CompareTestResult c : comparison){
            if(!c.getCompareResult() && ((c.getNewTest().hasAscendingPerformanceUnit() && c.getDifference()>0) ||
                    (!c.getNewTest().hasAscendingPerformanceUnit() && c.getDifference()>0))){
                worseBehaviour = true;
            }
        }
        if(worseBehaviour) {
            System.out.println("Some of your test/s gone worse.");
            System.exit(atLeastOneWorseResultExitCode);
        }
    }

    static void PrintResultHeader(){
        System.out.println("\t PUTE evaluation result ");
    }
    static void PrintResultTableHeader(){
        System.out.println("TEST_NAME\t\t\t\t|\tNEW_MEASURING\t|\tLAST_MEASURING\t\t|\tCHANGE (%)");
    }
    static void PrintOneTestResult(CompareTestResult testResult){
        String formatString = "%s\t\t|\t%.0f\t\t|\t%.0f\t\t\t|\t%+f%s";
        String nameOfTest = testResult.getNewTest().getName();
        // double speedOfNewTest = average(testResult.getNewTest().GetValues());
        double speedOfNewTest = testResult.getNewTestAvg();
        double speedOfOldTest = testResult.getOldTestAvg();
        double testDifference = testResult.getDifference();

        // In case of change the change will be reported
        String testResultChanged = "";
        if(!testResult.getCompareResult()){
            testResultChanged = "\t !!! SIGNIFICANT CHANGE !!!";
        }

        String stringToPrint=String.format(formatString, nameOfTest, speedOfNewTest,
                speedOfOldTest, testDifference, testResultChanged);

        System.out.println(stringToPrint);
    }
    static void PrintResult(List<CompareTestResult> compareResults){
        PrintResultHeader();
        PrintResultTableHeader();
        for (CompareTestResult test: compareResults) {
            PrintOneTestResult(test);
        }
    }
    static String[] GetFilesFromArg(String[] args){
        if(args.length!=2) return null;
        File[] inputFiles = new File[2];
        inputFiles[0] = new File(args[0]);
        inputFiles[1] = new File(args[1]);
        if(!(inputFiles[0].exists()&&inputFiles[1].exists())) return null;
        return args;
    }
    static List<CompareTestResult>CompareSets(List<IMeasurement> oldSet, List<IMeasurement> newSet){
        var result = new ArrayList<CompareTestResult>();
        for (int i= 0; i<oldSet.size();i++){
            result.add(new CompareTestResult(criticalValue,oldSet.get(i),newSet.get(i)));
        }
        return result;
    }
    static boolean WriteHelpMessage(String[] args){
        for(String s : args){
            if(s.compareTo(helpFlag)==0){
                try (BufferedReader reader = new BufferedReader(new FileReader(helpFileName))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }
    static boolean ContainsGraphical(String[] args){
        for(String s : args)
            if(s.compareTo(graphicalFlag)==0)
                return true;
        return false;
    }
}