package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.jgit.revwalk.RevCommit;
import org.example.evaluation.*;
import org.example.perfevalCLIEvaluator.EvaluateCLICommand;
import org.example.perfevalInit.InitCommand;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.perfevalInit.PerfEvalInvalidConfigException;
import org.example.performanceComparatorFactory.BootstrapPerformanceComparator;
import org.example.performanceComparatorFactory.ComparisonResult;
import org.example.performanceComparatorFactory.PerformanceComparator;
import org.example.resultDatabase.Database;
import org.example.resultDatabase.H2Database;

public class Main {
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


    public static final String PERFEVAL_DIR = ".performance";
    public static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    public static final String HELP_FILE_NAME = "help.txt";
    public static final String INI_FILE_NAME = "config.ini";
    public static final String DATABASE_FILE_NAME = "test_results.db";
    public static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    public static final String FORCE_FLAG = "force";
    public static final String HELP_FLAG = "flag";
    public static final String JSON_OUTPUT_FLAG = "json-output";
    public static final String GRAPHICAL_FLAG = "graphical";

    private static final String VERSION_PARAMETER = "version";
    private static final String TAG_PARAMETER = "tag";
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

    public static void main(String[] args) {
        ExitCode exitCode;
        Command command = getCommand(args);
        try {
            if (command == null) exitCode = ExitCode.invalidArguments;
            else exitCode = command.execute();

        } catch (PerfEvalCommandFailedException e) {
            System.err.println(e.getMessage());
            exitCode = e.exitCode;
        }
        exitCode.exit();
    }

    private static Command getCommand(String[] args) {
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

        for (var arg : options.nonOptionArguments()) {
            if (arg == INIT_COMMAND) return setupInitCommand(args, options, config);
            if (arg == EVALUATE_COMMAND) return setupEvaluateCommand(args, options, config);
            if (arg == INDEX_NEW_COMMAND) return setupIndexNewCommand(args, options, config);
            if (arg == INDEX_ALL_COMMAND) return setupIndexAllCommand(args, options, config);
            if (arg == UNDECIDED_COMMAND) return setupUndecidedCommand(args, options, config);
        }
        return null;
    }

    private static Command setupUndecidedCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Database database = constructDatabase(Path.of(args[0]));
        ResultPrinter printer = new UndecidedPrinter(System.out);
        int bootstrapCount = options.valueOf(bootstrapSampleCountOption);
        if (bootstrapCount <= 0) {
            System.err.println("Bootstrap sample count is not valid. Default value was used");
        }
        PerformanceComparator comparator = new BootstrapPerformanceComparator(config.getCritValue(), DEFAULT_TOLERANCE, bootstrapCount);
        // Undecided printer -> printing only undecided results
        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static Command setupIndexAllCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath, options, version);

        assert version != null && tag != null;

        return new AddFilesFromDirCommand(sourceDir, database, version, tag);
    }
    private static String resolveVersion(Path gitFilePath, OptionSet options) {
        if(options.has(versionOption))
            return options.valueOf(versionOption);
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

    private static String resolveTag(Path gitFilePath, OptionSet options, String version) {
        if(options.has(tagOption))
            return options.valueOf(tagOption);
        if(gitFilePath==null)
            return options.valueOf(tagOption);
        try {
            if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
                String lastCommitTag = GitUtilities.getLastCommitTag(gitFilePath.getParent(), version);
                assert lastCommitTag != null;
                return lastCommitTag;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return options.valueOf(tagOption);
    }


    private static Command setupIndexNewCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        Path sourceDir = Path.of(args[2]);
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath, options, version);

        assert version != null && tag != null;

        return new AddFileCommand(sourceDir, database, version, tag);
    }

    private static Command setupEvaluateCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        if (options.has(GRAPHICAL_FLAG))
            return setupGraphicalCommand(args, options, config);
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<>();
        if (options.has(filterOption)) {
            switch (options.valueOf(filterOption)) {
                case TEST_ID_FILTER -> filter = nameFilterComparator;
                case SIZE_OF_CHANGE_FILTER -> filter = sizeOfChangeFilterComparator;
                case TEST_RESULT_FILTER -> filter = testResultFilterComparator;
            }
        }
        PrintStream printStream = System.out;
        ResultPrinter printer = options.has(JSON_OUTPUT_FLAG) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);

        int bootstrapCount = options.valueOf(bootstrapSampleCountOption);
        if (bootstrapCount <= 0) {
            System.err.println("Bootstrap sample count is not valid. Default value was used");
        }
        PerformanceComparator comparator = new BootstrapPerformanceComparator(config.getCritValue(), DEFAULT_TOLERANCE, bootstrapCount);

        return new EvaluateCLICommand(database, printer, comparator);
    }

    private static Command setupGraphicalCommand(String[] args, OptionSet options, PerfEvalConfig config) {
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

    static ArgumentAcceptingOptionSpec<String> filterOption;
    static ArgumentAcceptingOptionSpec<String> maxTimeOption;
    static ArgumentAcceptingOptionSpec<Integer> bootstrapSampleCountOption;

    static ArgumentAcceptingOptionSpec<String> versionOption;
    static ArgumentAcceptingOptionSpec<String> tagOption;

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
        versionOption = parser.accepts(VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");
        tagOption = parser.accepts(TAG_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Tag of measured version")
                .defaultsTo(EMPTY_STRING);

        // Define flags (options without arguments)
        parser.accepts(HELP_FLAG, "Print help message");
        parser.accepts(GRAPHICAL_FLAG, "Enable graphical mode");
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(FORCE_FLAG, "Force the operation");
        return parser;
    }

    private static Database constructDatabase(Path perfevalDir) {
        Path databasePath = perfevalDir.resolve(DATABASE_FILE_NAME);
        return H2Database.getDBFromFilePath(databasePath);
    }

}