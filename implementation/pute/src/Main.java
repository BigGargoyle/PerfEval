import java.io.File;
import java.util.ArrayList;
import java.util.List;

// math library import
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.util.FastMath;


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
        /*
        for(var fileName : fileNames)
             System.out.println(fileName);
        */

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
        String stringToPrint="";
        stringToPrint+=testResult.getNewTest().GetName()+"\t\t|";
        stringToPrint+="\t"+average(testResult.getNewTest().GetValues())+"\t\t|";
        stringToPrint+="\t"+average(testResult.getOldTest().GetValues())+"\t|";
        stringToPrint+="\t"+ ((-1)*(testResult.getDiffernce() * 100 - 100));
        if(!testResult.getTestResult()){
            stringToPrint+="\t !!! SIGNIFICANT CHANGE !!!";
        }
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
    static List<CompareTestResult>CompareSets(List<ITest> testSet1, List<ITest> testSet2){
        var result = new ArrayList<CompareTestResult>();
        for (int i= 0; i<testSet1.size();i++){
            double pValue = CompareTests(testSet1.get(i),testSet2.get(i));
            double diff = average(testSet1.get(i).GetValues())/average(testSet2.get(i).GetValues());
            if(Math.abs(pValue)> criticalValue){
                // test result has changed
                result.add(new CompareTestResult(diff,true,testSet1.get(i),testSet2.get(i)));
            }
            else{
                // test result OK
                result.add(new CompareTestResult(diff,false,testSet1.get(i),testSet2.get(i)));
            }
        }
        return result;
    }
    static double CompareTests(ITest test1, ITest test2) {
        TTest tTestClass = new TTest();
        double[] values1 = new double[test1.GetValues().size()];
        double[] values2 = new double[test1.GetValues().size()];
        for (int i = 0; i<values1.length;i++){
            values1[i] = test1.GetValues().get(i);
            values2[i] = test2.GetValues().get(i);
        }
        return tTestClass.t(values1,values2);
        //return tTest(test1.Values, test2.Values);
    }
    static double average(List<Double> values){
        return listSum(values)/values.size();
    }
    static double listSum(List<Double> values){
        double sum = 0;
        for (Double value : values) {
            sum += value;
        }
        return sum;
    }
}