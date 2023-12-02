package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.JSONPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.TablePrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.BootstrapPerformanceComparator;
import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.performancecomparators.PerformanceComparator;
import cz.cuni.mff.d3s.perfeval.performancecomparators.TTestPerformanceComparator;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.FileWithResultsData;
import cz.cuni.mff.d3s.perfeval.resultdatabase.H2Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetupUtilities {
    private static final String EMPTY_STRING = "";

    static final String PERFEVAL_DIR = ".performance";
    static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    static final String INI_FILE_NAME = "config.ini";
    static final String DATABASE_FILE_NAME = "test_results.db";
    static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    static final String FORCE_FLAG = "force";
    private static final String HELP_FLAG = "flag";
    private static final String JSON_OUTPUT_FLAG = "json-output";
    private static final String GRAPHICAL_FLAG = "graphical";
    private static final String TTEST_FLAG = "t-test";


    private static final String NEW_VERSION_PARAMETER = "new-version";
    private static final String OLD_VERSION_PARAMETER = "old-version";
    private static final String MAX_TIME_PARAMETER = "max-time-on-test";
    private static final String BOOTSTRAP_SAMPLE_COUNT_PARAMETER = "bootstrap-sample-count";
    private static final int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 10_000;

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
    static final String BENCHMARK_PARSER_PARAMETER = "benchmark-parser";
    private static final String TEST_RESULT_FILTER = "test-result";
    static final Comparator<MeasurementComparisonRecord> testResultFilterComparator = Comparator.comparing(MeasurementComparisonRecord::comparisonResult, Comparator.comparingInt(ComparisonResult::getResultNumber));
    private static final String SIZE_OF_CHANGE_FILTER = "size-of-change";
    static final Comparator<MeasurementComparisonRecord> sizeOfChangeFilterComparator = Comparator.comparing(MeasurementComparisonRecord::performanceChange);
    private static final String TEST_ID_FILTER = "test-id";
    static final Comparator<MeasurementComparisonRecord> nameFilterComparator = Comparator.comparing(MeasurementComparisonRecord::newSamples, Comparator.comparing(Samples::getName));
    static Duration resolveDuration(OptionSet options, PerfEvalConfig config) throws ParserException {
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
            //System.err.println("Max time on test has invalid value. Default value will be used.");
            throw new ParserException("Max time on test has invalid value. Default value will be used.");
        }

        // Create a Duration object using the parsed values
        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }
    static String resolveVersion(Path gitFilePath, OptionSet options) throws IOException {
        if(options.has(versionOption))
            return options.valueOf(versionOption);
        if(gitFilePath==null)
            return null;

        if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
            RevCommit lastCommit = GitUtilities.getLastCommit(gitFilePath.getParent());
            assert lastCommit != null;
            return lastCommit.getName();
        }
        //System.err.println("Version cannot be resolved");
        return null;

    }

    static String resolveTag(Path gitFilePath, OptionSet options, String version) throws IOException {
        if(options.has(tagOption))
            return options.valueOf(tagOption);
        if(gitFilePath!=null)
            if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
                String lastCommitTag = GitUtilities.getLastCommitTag(gitFilePath.getParent(), version);
                assert lastCommitTag != null;
                return lastCommitTag;
            }
        return EMPTY_STRING;
    }

    static Date resolveDate(Path gitFilePath, String versionHash) throws IOException {
        if(gitFilePath==null)
            return null;
        if(GitUtilities.isRepoClean(gitFilePath.getParent())) {
            return GitUtilities.getCommitDate(gitFilePath.getParent(), versionHash);
        }

        //System.err.println("Date cannot be resolved");
        return null;
    }

    static ResultPrinter resolvePrinterForEvaluateCommand(OptionSet options) throws ParserException {
        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<>();
        if (options.has(filterOption)) {
            //TODO: zbavit se println (všude)
            switch (options.valueOf(filterOption)) {
                case TEST_ID_FILTER -> filter = nameFilterComparator;
                case SIZE_OF_CHANGE_FILTER -> filter = sizeOfChangeFilterComparator;
                case TEST_RESULT_FILTER -> filter = testResultFilterComparator;
                default -> throw new ParserException("Unknown filter option: " + options.valueOf(filterOption));
            }
        }
        PrintStream printStream = System.out;
        return options.has(JSON_OUTPUT_FLAG) ? new JSONPrinter(printStream, filter) : new TablePrinter(printStream, filter);
    }
    static PerformanceComparator resolvePerformanceComparatorForEvaluateCommand(OptionSet options, PerfEvalConfig config){
        return options.has(TTEST_FLAG) ?
                new TTestPerformanceComparator(config.getCritValue(), config.getMaxCIWidth(), config.getTolerance(), config.getMaxTimeOnTest()) :
                new BootstrapPerformanceComparator(config.getCritValue(), config.getTolerance(), options.valueOf(bootstrapSampleCountOption));
    }

    static FileWithResultsData[][] resolveInputFilesWithRespectToInputtedVersions(String[] args, OptionSet options) throws DatabaseException {
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));
        //fields that are null will not be used in WHERE clause of SQL query
        ProjectVersion newVersion = options.has(newVersionOption) ? new ProjectVersion(null, options.valueOf(newVersionOption), null) : null;
        ProjectVersion oldVersion = options.has(oldVersionOption) ? new ProjectVersion(null, options.valueOf(oldVersionOption), null) : null;
        if (newVersion == null && oldVersion == null) {
            ProjectVersion[] versions = database.getLastNVersions(2);
            assert versions.length == 2;
            newVersion = versions[0];
            oldVersion = versions[1];
        }
        if (newVersion == null) newVersion = database.getLastNVersions(1)[0];
        assert newVersion != null && oldVersion != null;
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
    static ArgumentAcceptingOptionSpec<String> benchmarkParserOption;

    static OptionParser CreateParser() {
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
        benchmarkParserOption = parser.accepts(BENCHMARK_PARSER_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("benchmark parser option with a parameter");

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
