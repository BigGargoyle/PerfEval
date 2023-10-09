package org.example;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.example.evaluation.*;
import org.example.globalVariables.ExitCode;
import org.example.measurementFactory.Measurement;
import org.example.perfevalCLIEvaluator.EvaluateCLICommand;
import org.example.perfevalInit.InitCommand;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.perfevalInit.PerfEvalInvalidConfigException;
import org.example.performanceComparatorFactory.ComparatorFactory;
import org.example.performanceComparatorFactory.ComparisonResult;
import org.example.performanceComparatorFactory.IPerformanceComparator;
import org.example.resultDatabase.CacheDatabase;
import org.example.resultDatabase.IDatabase;

public class Main {
    // commands
    /**
     * Command for PerfEval system initialization. It creates .performance directory inside current directory.
     */
    public static final String initCommand = "init";
    /**
     * Command for evaluating last two measurement results.
     */
    public static final String evaluateCommand = "evaluate";
    /**
     * Command for inserting a new measurement (single file specified as a next argument) result inside directory.
     */
    public static final String indexNewCommand = "index-new-result";
    /**
     * Command for inserting all files inside this directory (specified as a next argument) and its subdirectories as new measurement result files.
     */
    public static final String indexAllCommand = "index-all-results";
    /**
     * Command for listing all test names that has too few measurements for a statistical test.
     */
    public static final String undecidedCommand = "list-undecided";
    /**
     * Command for changing configuration of PerfEval system.
     */
    public static final String configCommand = "config";


    public static final String perfevalDir = ".performance";
    public static final String gitIgnoreFileName = ".gitignore";
    public static final String helpFileName = "help.txt";
    public static final String iniFileName = "config.ini";
    public static final String DatabaseFileName = "test_results.db";
    public static final String DatabaseCacheFileName = "test_results_cache.db";

    public static final String gitFileName = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    public static final String forceFlag = "force";
    public static final String helpFlag = "flag";
    public static final String jsonOutputFlag = "json-output";
    public static final String graphicalFlag = "graphical";

    private static final String maxTimeParameter = "max-time";

    static class DefaultComparator<T> implements Comparator<T>{
        @Override
        public int compare(T o1, T o2) {
            return 0;
        }
    }

    private static final String filterParameter = "filter";
    private static final String testResultFilter = "test-result";
    static final Comparator<MeasurementComparisonRecord> testResultFilterComparator = Comparator.comparing(MeasurementComparisonRecord::comparisonResult, Comparator.comparingInt(ComparisonResult::getResultNumber));
    private static final String sizeOfChangeFilter = "size-of-change";
    static final Comparator<MeasurementComparisonRecord> sizeOfChangeFilterComparator = Comparator.comparing(MeasurementComparisonRecord::performanceChange);
    private static final String testIDFilter = "test-id";
    static final Comparator<MeasurementComparisonRecord> nameFilterComparator = Comparator.comparing(MeasurementComparisonRecord::newMeasurement, Comparator.comparing(Measurement::name));

    public static void main(String[] args) {
        ExitCode exitCode;
        ICommand command = getCommand(args);
        try {
            if(command==null) exitCode=ExitCode.invalidArguments;
            else exitCode = command.execute();

        }catch (PerfEvalCommandFailedException e){
            System.err.println(e.toString());
            exitCode = e.exitCode;
        }
        exitCode.exit();
    }

    private static ICommand getCommand(String[] args) {
        OptionParser parser = CreateParser();
        OptionSet options = parser.parse(args);
        Path iniFilePath = Path.of(args[0]).resolve(perfevalDir).resolve(iniFileName);
        PerfEvalConfig config = null;
        try {
            config = InitCommand.getConfig(iniFilePath);
        } catch (PerfEvalInvalidConfigException e) {
            //TODO: zlepšit
            return null;
        }

        for(var arg : options.nonOptionArguments()){
            if(arg==initCommand) return setupInitCommand(args, options, config);
            if(arg==evaluateCommand) return setupEvaluateCommand(args, options, config);
            if(arg==indexNewCommand) return setupIndexNewCommand(args, options, config);
            if(arg==indexAllCommand) return setupIndexAllCommand(args, options, config);
            if(arg==undecidedCommand) return setupUndecidedCommand(args, options, config);
        }
        return null;
    }

    private static ICommand setupUndecidedCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        IDatabase database = new CacheDatabase(Path.of(DatabaseFileName), Path.of(DatabaseCacheFileName));
        IResultPrinter printer = new UndecidedPrinter(System.out);
        IPerformanceComparator comparator = ComparatorFactory.getComparator(config.critValue, config.maxCIWidth, config.maxTimeOnTest);
        // Undecided printer -> printing only undecided results
        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static ICommand setupIndexAllCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.gitFilePresence ? Path.of(args[0]).resolve(gitFileName) : null;
        IDatabase database = new CacheDatabase(Path.of(DatabaseFileName), Path.of(DatabaseCacheFileName));
        return new AddFilesFromDirCommand(sourceDir, gitFilePath, database, config);
    }

    private static ICommand setupIndexNewCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.gitFilePresence ? Path.of(args[0]).resolve(gitFileName) : null;
        IDatabase database = new CacheDatabase(Path.of(DatabaseFileName), Path.of(DatabaseCacheFileName));
        return new AddFileCommand(sourceDir, gitFilePath, database, config);
    }

    private static ICommand setupEvaluateCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        if(options.has(graphicalFlag))
            return setupGraphicalCommand(args, options, config);
        IDatabase database = new CacheDatabase(Path.of(DatabaseFileName), Path.of(DatabaseCacheFileName));

        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<MeasurementComparisonRecord>();
        if(options.has(filterOption)){
            switch (options.valueOf(filterOption)){
                case testIDFilter -> filter = nameFilterComparator;
                case sizeOfChangeFilter -> filter = sizeOfChangeFilterComparator;
                case testResultFilter -> filter = testResultFilterComparator;
            }
        }
        PrintStream printStream = System.out;
        IResultPrinter printer = options.has(jsonOutputFlag) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);

        IPerformanceComparator comparator = ComparatorFactory.getComparator(config.critValue, config.maxCIWidth, config.maxTimeOnTest);

        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static ICommand setupGraphicalCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        return null;
    }

    private static ICommand setupInitCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path workingDir = Path.of(args[0]);
        Path perfevalDirPath = workingDir.resolve(perfevalDir);
        Path gitIgnorePath = perfevalDirPath.resolve(gitIgnoreFileName);
        Path iniFilePath = perfevalDirPath.resolve(iniFileName);
        Path helpFilePath = perfevalDirPath.resolve(helpFileName);
        //TODO: dodat
        String helpFileContent = "";
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DatabaseFileName), perfevalDirPath.resolve(DatabaseCacheFileName)};
        Path[] gitIgnoredFiles = new Path[] {
                iniFilePath,
                helpFilePath,
                emptyFiles[0],
                emptyFiles[1]
        };
        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath ,helpFilePath, helpFileContent,
                emptyFiles, gitIgnoredFiles, config, options.has(forceFlag));
    }

    static ArgumentAcceptingOptionSpec<String> filterOption;
    static ArgumentAcceptingOptionSpec<String> maxTimeOption;

    private static OptionParser CreateParser(){
        OptionParser parser = new OptionParser();

        // Define options with arguments
        filterOption = parser.accepts(filterParameter)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Filter option with a parameter");
        maxTimeOption = parser.accepts(maxTimeParameter)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Max time option with a duration parameter");

        // Define flags (options without arguments)
        parser.accepts(helpFlag, "Print help message");
        parser.accepts(graphicalFlag, "Enable graphical mode");
        parser.accepts(jsonOutputFlag, "Enable JSON output");
        parser.accepts(forceFlag, "Force the operation");
        return parser;
    }

}