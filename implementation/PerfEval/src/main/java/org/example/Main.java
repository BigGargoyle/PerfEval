package org.example;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.example.evaluation.*;
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
    public static final String INIT_COMMAND = "init";
    /**
     * Command for evaluating last two measurement results.
     */
    public static final String EVALUATE_COMMAND = "evaluate";
    /**
     * Command for inserting a new measurement (single file specified as a next argument) result inside directory.
     */
    public static final String INDEX_NEW_COMMAND = "index-new-result";
    /**
     * Command for inserting all files inside this directory (specified as a next argument) and its subdirectories as new measurement result files.
     */
    public static final String INDEX_ALL_COMMAND = "index-all-results";
    /**
     * Command for listing all test names that has too few measurements for a statistical test.
     */
    public static final String UNDECIDED_COMMAND = "list-undecided";


    public static final String PERFEVAL_DIR = ".performance";
    public static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    public static final String HELP_FILE_NAME = "help.txt";
    public static final String INI_FILE_NAME = "config.ini";
    public static final String DATABASE_FILE_NAME = "test_results.db";
    public static final String DATABASE_CACHE_FILE_NAME = "test_results_cache.db";

    public static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    public static final String FORCE_FLAG = "force";
    public static final String HELP_FLAG = "flag";
    public static final String JSON_OUTPUT_FLAG = "json-output";
    public static final String GRAPHICAL_FLAG = "graphical";

    private static final String MAX_TIME_PARAMETER = "max-time";

    static class DefaultComparator<T> implements Comparator<T>{
        @Override
        public int compare(T o1, T o2) {
            return 0;
        }
    }

    private static final String FILTER_PARAMETER = "filter";
    private static final String TEST_RESULT_FILTER = "test-result";
    static final Comparator<MeasurementComparisonRecord> testResultFilterComparator = Comparator.comparing(MeasurementComparisonRecord::comparisonResult, Comparator.comparingInt(ComparisonResult::getResultNumber));
    private static final String SIZE_OF_CHANGE_FILTER = "size-of-change";
    static final Comparator<MeasurementComparisonRecord> sizeOfChangeFilterComparator = Comparator.comparing(MeasurementComparisonRecord::performanceChange);
    private static final String TEST_ID_FILTER = "test-id";
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
        Path iniFilePath = Path.of(args[0]).resolve(PERFEVAL_DIR).resolve(INI_FILE_NAME);
        PerfEvalConfig config;
        try {
            config = InitCommand.getConfig(iniFilePath);
        } catch (PerfEvalInvalidConfigException e) {
            //TODO: zlepšit
            return null;
        }

        for(var arg : options.nonOptionArguments()){
            if(arg== INIT_COMMAND) return setupInitCommand(args, options, config);
            if(arg== EVALUATE_COMMAND) return setupEvaluateCommand(args, options, config);
            if(arg== INDEX_NEW_COMMAND) return setupIndexNewCommand(args, config);
            if(arg== INDEX_ALL_COMMAND) return setupIndexAllCommand(args, config);
            if(arg== UNDECIDED_COMMAND) return setupUndecidedCommand(args, config);
        }
        return null;
    }

    private static ICommand setupUndecidedCommand(String[] args, PerfEvalConfig config) {
        IDatabase database = constructDatabase(Path.of(args[0]));
        IResultPrinter printer = new UndecidedPrinter(System.out);
        IPerformanceComparator comparator = ComparatorFactory.getComparator(config.critValue, config.maxCIWidth, config.maxTimeOnTest);
        // Undecided printer -> printing only undecided results
        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static ICommand setupIndexAllCommand(String[] args, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.gitFilePresence ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        IDatabase database = constructDatabase(Path.of(args[0]));
        return new AddFilesFromDirCommand(sourceDir, gitFilePath, database, config);
    }

    private static ICommand setupIndexNewCommand(String[] args, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.gitFilePresence ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        IDatabase database = constructDatabase(Path.of(args[0]));
        return new AddFileCommand(sourceDir, gitFilePath, database, config);
    }

    private static ICommand setupEvaluateCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        if(options.has(GRAPHICAL_FLAG))
            return setupGraphicalCommand(args, options, config);
        IDatabase database = constructDatabase(Path.of(args[0]));

        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<MeasurementComparisonRecord>();
        if(options.has(filterOption)){
            switch (options.valueOf(filterOption)){
                case TEST_ID_FILTER -> filter = nameFilterComparator;
                case SIZE_OF_CHANGE_FILTER -> filter = sizeOfChangeFilterComparator;
                case TEST_RESULT_FILTER -> filter = testResultFilterComparator;
            }
        }
        PrintStream printStream = System.out;
        IResultPrinter printer = options.has(JSON_OUTPUT_FLAG) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);

        IPerformanceComparator comparator = ComparatorFactory.getComparator(config.critValue, config.maxCIWidth, config.maxTimeOnTest);

        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static ICommand setupGraphicalCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        return null;
    }

    private static ICommand setupInitCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path workingDir = Path.of(args[0]);
        Path perfevalDirPath = workingDir.resolve(PERFEVAL_DIR);
        Path gitIgnorePath = perfevalDirPath.resolve(GIT_IGNORE_FILE_NAME);
        Path iniFilePath = perfevalDirPath.resolve(INI_FILE_NAME);
        Path helpFilePath = perfevalDirPath.resolve(HELP_FILE_NAME);
        //TODO: dodat
        String helpFileContent = "";
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DATABASE_FILE_NAME), perfevalDirPath.resolve(DATABASE_CACHE_FILE_NAME)};
        Path[] gitIgnoredFiles = new Path[] {
                iniFilePath,
                helpFilePath,
                emptyFiles[0],
                emptyFiles[1]
        };
        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath ,helpFilePath, helpFileContent,
                emptyFiles, gitIgnoredFiles, config, options.has(FORCE_FLAG));
    }

    static ArgumentAcceptingOptionSpec<String> filterOption;
    static ArgumentAcceptingOptionSpec<String> maxTimeOption;

    private static OptionParser CreateParser(){
        OptionParser parser = new OptionParser();

        // Define options with arguments
        filterOption = parser.accepts(FILTER_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Filter option with a parameter");
        maxTimeOption = parser.accepts(MAX_TIME_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Max time option with a duration parameter");

        // Define flags (options without arguments)
        parser.accepts(HELP_FLAG, "Print help message");
        parser.accepts(GRAPHICAL_FLAG, "Enable graphical mode");
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(FORCE_FLAG, "Force the operation");
        return parser;
    }

    private static IDatabase constructDatabase(Path workingDir){
        Path databasePath = workingDir.resolve(DATABASE_FILE_NAME);
        Path cachePath = workingDir.resolve(DATABASE_CACHE_FILE_NAME);
        return new CacheDatabase(databasePath, cachePath);
    }

}