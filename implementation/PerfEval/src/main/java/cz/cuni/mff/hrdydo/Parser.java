package cz.cuni.mff.hrdydo;

import cz.cuni.mff.hrdydo.evaluation.*;
import cz.cuni.mff.hrdydo.perfevalCLIEvaluator.EvaluateCLICommand;
import cz.cuni.mff.hrdydo.performanceComparatorFactory.*;
import cz.cuni.mff.hrdydo.resultDatabase.DatabaseException;
import cz.cuni.mff.hrdydo.resultDatabase.FileWithResultsData;
import cz.cuni.mff.hrdydo.resultDatabase.H2Database;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.jgit.revwalk.RevCommit;
import cz.cuni.mff.hrdydo.perfevalInit.InitCommand;
import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalConfig;
import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalInvalidConfigException;
import cz.cuni.mff.hrdydo.resultDatabase.Database;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;

public class Parser {
    private static final String EMPTY_STRING = "";
    // commands
    /**
     * Command for PerfEval system initialization. It creates .performance directory inside current directory.
     */
    private static final String INIT_COMMAND = "init";
    /**
     * Command for evaluating last two measurement results.
     */
    private static final String EVALUATE_COMMAND = "evaluate";
    /**
     * Command for inserting a new measurement (single file specified as a next argument) result inside directory.
     */
    private static final String INDEX_NEW_COMMAND = "index-new-result";
    /**
     * Command for inserting all files inside this directory (specified as a next argument) and its subdirectories as new measurement result files.
     */
    private static final String INDEX_ALL_COMMAND = "index-all-results";
    /**
     * Command for listing all test names that has too few measurements for a statistical test.
     */
    private static final String UNDECIDED_COMMAND = "list-undecided";


    private static final String PERFEVAL_DIR = ".performance";
    private static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    private static final String HELP_FILE_NAME = "help.txt";
    private static final String INI_FILE_NAME = "config.ini";
    private static final String DATABASE_FILE_NAME = "test_results.db";
    private static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    private static final String FORCE_FLAG = "force";
    private static final String HELP_FLAG = "flag";
    private static final String JSON_OUTPUT_FLAG = "json-output";
    private static final String GRAPHICAL_FLAG = "graphical";
    private static final String TTEST_FLAG = "t-test";


    private static final String NEW_VERSION_PARAMETER = "new-version";
    private static final String OLD_VERSION_PARAMETER = "old-parameter";
    private static final String MAX_TIME_PARAMETER = "max-time";
    private static final String BOOTSTRAP_SAMPLE_COUNT_PARAMETER = "bootstrap-sample-count";
    private static final int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 10_000;
    private static final double DEFAULT_TOLERANCE = 0.01;

    //used as an empty filter, order of elements will not be changed
    static class DefaultComparator<T> implements Comparator<T> {
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
    static final Comparator<MeasurementComparisonRecord> nameFilterComparator = Comparator.comparing(MeasurementComparisonRecord::newSamples, Comparator.comparing(Samples::getName));
    public static Command getCommand(String[] args) {
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
        try {
            for (var arg : options.nonOptionArguments()) {
                if (arg == INIT_COMMAND) return setupInitCommand(args, options, config);
                if (arg == EVALUATE_COMMAND) return setupEvaluateCommand(args, options, config);
                if (arg == INDEX_NEW_COMMAND) return setupIndexNewCommand(args, options, config);
                if (arg == INDEX_ALL_COMMAND) return setupIndexAllCommand(args, options, config);
                if (arg == UNDECIDED_COMMAND) return setupUndecidedCommand(args, options, config);
            }
        } catch (DatabaseException e){
            System.err.println(e.getMessage());
        } catch (AssertionError e){
            System.err.println("One of versions has no known measurement results.");
        }
        return null;
    }

    private static Command setupInitCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path workingDir = Path.of(args[0]);
        Path perfevalDirPath = workingDir.resolve(PERFEVAL_DIR);
        Path gitIgnorePath = perfevalDirPath.resolve(GIT_IGNORE_FILE_NAME);
        Path iniFilePath = perfevalDirPath.resolve(INI_FILE_NAME);
        Path helpFilePath = perfevalDirPath.resolve(HELP_FILE_NAME);
        //TODO: dodat
        String helpFileContent = "";
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DATABASE_FILE_NAME)};
        Path[] gitIgnoredFiles = new Path[]{
                iniFilePath,
                helpFilePath,
                emptyFiles[0]
        };
        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath, helpFilePath, helpFileContent,
                emptyFiles, gitIgnoredFiles, config, options.has(FORCE_FLAG));
    }
    private static Command setupEvaluateCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        if (options.has(GRAPHICAL_FLAG))
            return setupGraphicalCommand(args, options, config);

        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = resolvePrinterForEvaluateCommand(options);
        PerformanceComparator comparator = resolvePerformanceComparatorForEvaluateCommand(options, config);

        return new EvaluateCLICommand(inputFiles, printer, comparator);
    }

    private static Command setupGraphicalCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        return null;
    }

    private static Command setupUndecidedCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = new UndecidedPrinter(System.out);
        // tTest is able to response that there are not enough samples
        PerformanceComparator comparator = new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), DEFAULT_TOLERANCE);
        // Undecided printer -> printing only undecided results
        return new EvaluateCLICommand(inputFiles, printer, comparator);
    }
    private static Command setupIndexNewCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath, version);

        assert version != null && tag != null;

        return new AddFileCommand(sourceDir, database, version, tag);
    }

    private static Command setupIndexAllCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath, version);

        assert version != null && tag != null;

        return new AddFilesFromDirCommand(sourceDir, database, version, tag);
    }
    private static String resolveVersion(Path gitFilePath, OptionSet options) {
        if(options.has(newVersionOption))
            return options.valueOf(newVersionOption);
        if(gitFilePath==null)
            return null;

        try {
            if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
                RevCommit lastCommit = GitUtilities.getLastCommit(gitFilePath.getParent());
                assert lastCommit != null;
                return lastCommit.getName();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.err.println("Version cannot be resolved");
        return null;

    }

    private static String resolveTag(Path gitFilePath, String version) {
        if(gitFilePath!=null)
            try {
                if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
                    String lastCommitTag = GitUtilities.getLastCommitTag(gitFilePath.getParent(), version);
                    assert lastCommitTag != null;
                    return lastCommitTag;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        return EMPTY_STRING;
    }



    private static ResultPrinter resolvePrinterForEvaluateCommand(OptionSet options){
        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<>();
        if (options.has(filterOption)) {
            switch (options.valueOf(filterOption)) {
                case TEST_ID_FILTER -> filter = nameFilterComparator;
                case SIZE_OF_CHANGE_FILTER -> filter = sizeOfChangeFilterComparator;
                case TEST_RESULT_FILTER -> filter = testResultFilterComparator;
            }
        }
        PrintStream printStream = System.out;
        return options.has(JSON_OUTPUT_FLAG) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);
    }
    private static PerformanceComparator resolvePerformanceComparatorForEvaluateCommand(OptionSet options, PerfEvalConfig config){
        return options.has(TTEST_FLAG) ?
                new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), DEFAULT_TOLERANCE) :
                new BootstrapPerformanceComparator(config.getCritValue(), DEFAULT_TOLERANCE, DEFAULT_BOOTSTRAP_SAMPLE_COUNT);
    }

    private static FileWithResultsData[][] resolveInputFilesWithRespectToInputtedVersions(String[] args, OptionSet options) throws DatabaseException {
        String newVersion = options.has(newVersionOption) ? options.valueOf(newVersionOption) : null;
        String oldVersion = options.has(oldVersionOption) ? options.valueOf(oldVersionOption) : null;

        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));
        FileWithResultsData[] newFiles = database.getResultsOfVersion(newVersion);
        FileWithResultsData[] oldFiles = database.getResultsOfVersion(oldVersion);
        assert newFiles.length > 0 && oldFiles.length > 0;
        return new FileWithResultsData[][]{oldFiles, newFiles};
    }


    static ArgumentAcceptingOptionSpec<String> filterOption;
    static ArgumentAcceptingOptionSpec<String> maxTimeOption;
    static ArgumentAcceptingOptionSpec<Integer> bootstrapSampleCountOption;

    static ArgumentAcceptingOptionSpec<String> newVersionOption;
    static ArgumentAcceptingOptionSpec<String> oldVersionOption;

    private static OptionParser CreateParser() {
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
        bootstrapSampleCountOption = parser.accepts(BOOTSTRAP_SAMPLE_COUNT_PARAMETER)
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(DEFAULT_BOOTSTRAP_SAMPLE_COUNT)
                .describedAs("Count of bootstrap samples");
        newVersionOption = parser.accepts(NEW_VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");
        oldVersionOption = parser.accepts(OLD_VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");

        // Define flags (options without arguments)
        parser.accepts(HELP_FLAG, "Print help message");
        parser.accepts(GRAPHICAL_FLAG, "Enable graphical mode");
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(FORCE_FLAG, "Force the operation of init");
        parser.accepts(TTEST_FLAG, "Uses t-test instead of bootstrap");
        return parser;
    }

    private static Database constructDatabase(Path perfevalDir) {
        Path databasePath = perfevalDir.resolve(DATABASE_FILE_NAME);
        return H2Database.getDBFromFilePath(databasePath);
    }
}
