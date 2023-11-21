package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.clievaluator.EvaluateCLICommand;
import cz.cuni.mff.d3s.perfeval.evaluation.*;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.BootstrapPerformanceComparator;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.PerformanceComparator;
import cz.cuni.mff.d3s.perfeval.performancecomparatorfactory.TTestPerformanceComparator;
import cz.cuni.mff.d3s.perfeval.resultdatabase.*;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.jgit.revwalk.RevCommit;
import cz.cuni.mff.d3s.perfeval.init.InitCommand;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalInvalidConfigException;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final String EMPTY_STRING = "";

    private static final String PERFEVAL_DIR = ".performance";
    private static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    //private static final String HELP_FILE_NAME = "help.txt";
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
    private static final String OLD_VERSION_PARAMETER = "old-version";
    private static final String MAX_TIME_PARAMETER = "max-time-on-test";
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

    private static final String PATH_PARAMETER = "path";
    private static final String TAG_PARAMETER = "tag";
    private static final String VERSION_PARAMETER = "version";
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
            return null;
        }
        try {
            //TODO: foreach ICommand that exists?
            for (var arg : options.nonOptionArguments()) {
                if (InitCommand.COMMAND.equals(arg)) return setupInitCommand(args, options, config);
                if (EvaluateCLICommand.COMMAND.equals(arg)) return setupEvaluateCommand(args, options, config);
                if (AddFileCommand.COMMAND.equals(arg)) return setupIndexNewCommand(args, options, config);
                if (AddFilesFromDirCommand.COMMAND.equals(arg)) return setupIndexAllCommand(args, options, config);
                if (EvaluateCLICommand.UNDECIDED_COMMAND.equals(arg)) return setupUndecidedCommand(args, options, config);
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
        Path[] emptyFiles = new Path[]{perfevalDirPath.resolve(DATABASE_FILE_NAME)};
        Path[] gitIgnoredFiles = new Path[]{
                iniFilePath,
                emptyFiles[0]
        };
        return new InitCommand(perfevalDirPath, gitIgnorePath, iniFilePath,
                emptyFiles, gitIgnoredFiles, config, options.has(FORCE_FLAG));
    }
    private static Command setupEvaluateCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        if (options.has(GRAPHICAL_FLAG))
            return setupGraphicalCommand(args, options, config);

        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = resolvePrinterForEvaluateCommand(options);
        PerformanceComparator comparator = resolvePerformanceComparatorForEvaluateCommand(options, config);

        return new EvaluateCLICommand(inputFiles, printer, comparator);
        return null;
    }

    private static Command setupGraphicalCommand(String[] args, OptionSet options, PerfEvalConfig config) {
        return null;
    }

    private static Command setupUndecidedCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        FileWithResultsData[][] inputFiles = resolveInputFilesWithRespectToInputtedVersions(args, options);
        ResultPrinter printer = new UndecidedPrinter(System.out);
        // tTest is able to response that there are not enough samples
        //TODO: zpracovat maxTestDuration
        //Duration maxTestDuration = resolveDuration(options, config);
        PerformanceComparator comparator = new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), DEFAULT_TOLERANCE);
        // Undecided printer -> printing only undecided results
        return new EvaluateCLICommand(inputFiles, printer, comparator);
        return null;
    }

    private static Duration resolveDuration(OptionSet options, PerfEvalConfig config) {
        if(!options.has(maxTimeOption))
            return config.getMaxTimeOnTest();
        Pattern pattern = Pattern.compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");
        Matcher matcher = pattern.matcher(options.valueOf(maxTimeOption));
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (matcher.find()) {
            // Extract and parse the matched groups
            if (matcher.group(1) != null) {
                hours = Integer.parseInt(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                minutes = Integer.parseInt(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                seconds = Integer.parseInt(matcher.group(3));
            }
        }

        if(Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds).toNanos()==0){
            System.err.println("Max time on test has invalid value. Default value will be used.");
            return config.getMaxTimeOnTest();
        }

        // Create a Duration object using the parsed values
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    private static Command setupIndexNewCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        Path sourceDir = Path.of(options.valueOf(pathOption));
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath, options, version);
        Date date = resolveDate(gitFilePath, version);

        ProjectVersion projectVersion = new ProjectVersion(date, version, tag);

        assert version != null && tag != null;

        return new AddFileCommand(sourceDir, database, projectVersion);
    }

    private static Command setupIndexAllCommand(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        Path sourceDir = Path.of(options.valueOf(pathOption));
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath,options,  version);
        Date date = resolveDate(gitFilePath, version);

        ProjectVersion projectVersion = new ProjectVersion(date, version, tag);

        assert version != null && tag != null;

        return new AddFilesFromDirCommand(sourceDir, database, projectVersion);
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

    private static String resolveTag(Path gitFilePath, OptionSet options,String version) {
        if(options.has(tagOption))
            return options.valueOf(tagOption);
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
                default -> System.err.println("Unknown filter. Default filter will be used.");
            }
        }
        PrintStream printStream = System.out;
        return options.has(JSON_OUTPUT_FLAG) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);
    }
    private static PerformanceComparator resolvePerformanceComparatorForEvaluateCommand(OptionSet options, PerfEvalConfig config){
        return options.has(TTEST_FLAG) ?
                new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), DEFAULT_TOLERANCE) :
                new BootstrapPerformanceComparator(config.getCritValue(), DEFAULT_TOLERANCE, options.valueOf(bootstrapSampleCountOption));
    }

    private static FileWithResultsData[][] resolveInputFilesWithRespectToInputtedVersions(String[] args, OptionSet options) throws DatabaseException {
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));
        //fields that are null will not be used in WHERE clause of SQL query
        ProjectVersion newVersion = options.has(newVersionOption) ? new ProjectVersion(null, options.valueOf(newVersionOption),null) : null;
        ProjectVersion oldVersion = options.has(oldVersionOption) ? new ProjectVersion(null, options.valueOf(oldVersionOption),null) : null;
        if(newVersion==null && oldVersion==null){
            ProjectVersion[] versions = database.getLastNVersions(2);
            assert versions.length==2;
            newVersion = versions[0];
            oldVersion = versions[1];
        }
        if(newVersion==null) newVersion=database.getLastNVersions(1)[0];
        assert newVersion!=null && oldVersion!=null;
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
    static ArgumentAcceptingOptionSpec<String> pathOption;
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
        newVersionOption = parser.accepts(NEW_VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");
        oldVersionOption = parser.accepts(OLD_VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");
        pathOption = parser.accepts(PATH_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Path option with a parameter");
        versionOption = parser.accepts(VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("version option with a parameter");
        tagOption = parser.accepts(TAG_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("tag option with a parameter");

        // Define flags (options without arguments)
        parser.accepts(HELP_FLAG, "Print help message");
        parser.accepts(GRAPHICAL_FLAG, "Enable graphical mode");
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(FORCE_FLAG, "Force the operation of init");
        parser.accepts(TTEST_FLAG, "Uses t-test instead of bootstrap");
        return parser;
    }

    public static Database constructDatabase(Path perfevalDir) throws DatabaseException {
        Path databasePath = perfevalDir.resolve(DATABASE_FILE_NAME);
        try {
            return H2Database.getDBFromFilePath(databasePath);

        }catch (SQLException e)
        {
            DatabaseException exception = new DatabaseException("DB cannot be instantiated", null, ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
    }
}
