import java.io.File;
import java.util.ArrayList;
import java.util.List;

// math library import


public class Main {
    static double criticalValue = 0.05;
    public static void main(String[] args) {
        /*
        * Basic version that accepts 2 benchmark results files as input
        * According to these files the parser will be constructed
        * Content of files will be parsed to test objects and compared
        * by the statistic t-test (Student's test)
        * */

        var fileNames = GetFilesFromArg(args);
        if(fileNames == null || fileNames.length!=2){
            System.out.println("Benchmark result files not found");
            return;
        }

        ITestParser parser = ParserIndustry.RecognizeParserFactory(fileNames[0]);
        assert parser!=null;
        var testSet1 = parser.GetTestsFromFile(fileNames[0]);
        var testSet2 = parser.GetTestsFromFile(fileNames[1]);
        if(testSet1.size() != testSet2.size()){
            System.out.println("Benchmark results are not made on the same set of tests");
            return;
        }

        for(int i =0;i<testSet1.size();i++){
            if(testSet1.get(i).GetName().compareTo(testSet2.get(i).GetName()) != 0){
                System.out.println("Benchmark results are not made on the same set of tests");
                return;
            }
        }

        PrintResult(CompareSets(testSet1,testSet2));

    }

    static void PrintResultHeader(){
        System.out.println("\t PUTE evaluation result ");
    }
    static void PrintResultTableHeader(){
        System.out.println("TEST_NAME\t\t\t\t|\tNEW_MEASURING\t|\tLAST_MEASURING\t\t|\tCHANGE (%)");
    }
    static void PrintOneTestResult(CompareTestResult testResult){
        String formatString = "%s\t\t|\t%.0f\t\t|\t%.0f\t\t\t|\t%+f%s";
        String nameOfTest = testResult.getNewTest().GetName();
        // double speedOfNewTest = average(testResult.getNewTest().GetValues());
        double speedOfNewTest = testResult.getNewTestAvg();
        double speedOfOldTest = testResult.getOldTestAvg();
        double testDifference = testResult.getDifference();

        // In case of change the change will be reported
        String testResultChanged = "";
        if(!testResult.getTestResult()){
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
    static List<CompareTestResult>CompareSets(List<ITest> oldSet, List<ITest> newSet){
        var result = new ArrayList<CompareTestResult>();
        for (int i= 0; i<oldSet.size();i++){
            result.add(new CompareTestResult(criticalValue,oldSet.get(i),newSet.get(i)));
        }
        return result;
    }
}