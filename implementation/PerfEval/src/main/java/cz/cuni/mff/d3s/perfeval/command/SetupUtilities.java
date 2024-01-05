package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.evaluation.HTMLPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.JSONPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.evaluation.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.evaluation.TablePrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.performancecomparators.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.performancecomparators.NonparametricTest;
import cz.cuni.mff.d3s.perfeval.performancecomparators.ParametricTest;
import cz.cuni.mff.d3s.perfeval.performancecomparators.StatisticTest;
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
import java.util.Comparator;
import java.util.Date;

/**
 * Class containing utilities for parsing and setup of commands.
 */
public class SetupUtilities {
    /**
     * Empty string constant.
     */
    private static final String EMPTY_STRING = "";
    /**
     * Name of the directory containing the database and other files.
     */
    public static final String PERFEVAL_DIR = ".performance";
    /**
     * Name of the file containing the gitignore.
     */
    static final String GIT_IGNORE_FILE_NAME = ".gitignore";
    /**
     * Name of the file containing the configuration.
     */
    static final String INI_FILE_NAME = "config.ini";
    /**
     * Name of the file containing the database.
     */
    static final String DATABASE_FILE_NAME = "test_results.db";
    /**
     * Name of the file containing the git information.
     */
    static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    static final String FORCE_FLAG = "force";

    //private static final String HELP_FLAG = "flag";
    /**
     * JSONPrinter will be used for printing results.
     */
    private static final String JSON_OUTPUT_FLAG = "json-output";
    /**
     * HTMLPrinter will be used for printing results.
     */
    private static final String HTML_OUTPUT_FLAG = "html-output";
    /**
     * Statistical method of t-Test will be used for evaluation.
     */
    private static final String TTEST_FLAG = "t-test";


    /**
     * Name of the parameter for the input of the new version.
     */
    private static final String NEW_VERSION_PARAMETER = "new-version";
    /**
     * Name of the parameter for the input of the old version.
     */
    private static final String OLD_VERSION_PARAMETER = "old-version";
    /**
     * Name of the parameter for the maximum count of tests.
     */
    private static final String MAX_TIME_PARAMETER = "max-time-on-test";
    /**
     * Name of the bootstrap sample count parameter.
     */
    private static final String BOOTSTRAP_SAMPLE_COUNT_PARAMETER = "bootstrap-sample-count";
    /**
     * Default value of the bootstrap sample count parameter.
     */
    private static final int DEFAULT_BOOTSTRAP_SAMPLE_COUNT = 10_000;

    //used as an empty filter, order of elements will not be changed

    /**
     * Comparator that does not change the order of elements.
     * @param <T>   Type of the elements.
     */
    static class DefaultComparator<T> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return 0;
        }
    }

    /**
     * Name of the parameter for the path from which file or files are added to database.
     */
    private static final String PATH_PARAMETER = "path";
    /**
     * Name of the parameter for the tag of the version of the software.
     */
    private static final String TAG_PARAMETER = "tag";
    /**
     * Name of the parameter for the version of the software.
     */
    private static final String VERSION_PARAMETER = "version";
    /**
     * Name of the parameter for the filter option.
     */
    private static final String FILTER_PARAMETER = "filter";
    /**
     * Name of the parameter for the benchmark parser option.
     */
    static final String BENCHMARK_PARSER_PARAMETER = "benchmark-parser";
    /**
     * Name of the parameter for the result filter option.
     */
    private static final String TEST_RESULT_FILTER = "test-result";
    /**
     * Comparator for the result filter option.
     */
    static final Comparator<MeasurementComparisonRecord> testResultFilterComparator = Comparator.comparing(MeasurementComparisonRecord::comparisonResult, Comparator.comparingInt(ComparisonResult::getResultNumber));
    /**
     * Name of the parameter for the size of change filter option.
     */
    private static final String SIZE_OF_CHANGE_FILTER = "size-of-change";
    /**
     * Comparator for the size of change filter option.
     */
    static final Comparator<MeasurementComparisonRecord> sizeOfChangeFilterComparator = Comparator.comparing(MeasurementComparisonRecord::performanceChange);
    /**
     * Name of the parameter for the test id filter option.
     */
    private static final String TEST_ID_FILTER = "test-id";
    /**
     * Comparator for the test id filter option.
     */
    static final Comparator<MeasurementComparisonRecord> nameFilterComparator = Comparator.comparing(MeasurementComparisonRecord::newSamples, Comparator.comparing(Samples::getName));

    /**
     * Resolves version of the software. Based on the git file, command line
     * options and config file.
     * @param gitFilePath  Path to the git file.
     * @param options  Command line options.
     * @return Version of the software.
     * @throws IOException If there is an error with the git file.
     */
    static String resolveVersion(Path gitFilePath, OptionSet options) throws IOException {
        if (options.has(versionOption)) {
            return options.valueOf(versionOption);
        }
        if (gitFilePath == null) {
            return null;
        }

        if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
            RevCommit lastCommit = GitUtilities.getLastCommit(gitFilePath.getParent());
            assert lastCommit != null;
            return lastCommit.getName();
        }
        //System.err.println("Version cannot be resolved");
        return null;

    }

    /**
     * Resolves tag of the software. Based on the git file, command line
     * options and config file.
     * @param gitFilePath  Path to the git file.
     * @param options  Command line options.
     * @param version  Version of the software.
     * @return Tag of the software.
     * @throws IOException If there is an error with the git file.
     */
    static String resolveTag(Path gitFilePath, OptionSet options, String version) throws IOException {
        if (options.has(tagOption)) {
            return options.valueOf(tagOption);
        }
        if (gitFilePath != null) {
            if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
                String lastCommitTag = GitUtilities.getLastCommitTag(gitFilePath.getParent(), version);
                assert lastCommitTag != null;
                return lastCommitTag;
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Resolves date of the last update of software. Based on the git file.
     * @param gitFilePath  Path to the git file.
     * @param versionHash  Version of the software.
     * @return Date of the software.
     * @throws IOException If there is an error with the git file.
     */
    static Date resolveDate(Path gitFilePath, String versionHash) throws IOException {
        if (gitFilePath == null) {
            return null;
        }
        if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
            return GitUtilities.getCommitDate(gitFilePath.getParent(), versionHash);
        }

        //System.err.println("Date cannot be resolved");
        return null;
    }

    /**
     * Resolves printer for printing results. Based on the command line options.
     * @param options  Command line options.
     * @return Printer for the evaluate command.
     * @throws ParserException If there is an error with parsing the command line arguments.
     */
    static ResultPrinter resolvePrinterForEvaluateCommand(OptionSet options) throws ParserException {
        Comparator<MeasurementComparisonRecord> filter = new DefaultComparator<>();
        if (options.has(filterOption)) {
            switch (options.valueOf(filterOption)) {
                case TEST_ID_FILTER -> filter = nameFilterComparator;
                case SIZE_OF_CHANGE_FILTER -> filter = sizeOfChangeFilterComparator;
                case TEST_RESULT_FILTER -> filter = testResultFilterComparator;
                default -> throw new ParserException("Unknown filter option: " + options.valueOf(filterOption));
            }
        }
        PrintStream printStream = System.out;
        if (options.has(HTML_OUTPUT_FLAG)) {
            return new HTMLPrinter(printStream, filter);
        }
        if (options.has(JSON_OUTPUT_FLAG)) {
            return new JSONPrinter(printStream, filter);
        }
        return new TablePrinter(printStream, filter);
    }

    /**
     * Resolves input files with respect to inputted versions. Based on the command line options.
     * @param args  Command line arguments.
     * @param options  Command line options.
     * @return Input files with respect to inputted versions.
     * @throws DatabaseException If there is an error with the database.
     */
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
        if (newVersion == null) {
            newVersion = database.getLastNVersions(1)[0];
        }
        assert newVersion != null && oldVersion != null;
        FileWithResultsData[] newFiles = database.getResultsOfVersion(newVersion);
        FileWithResultsData[] oldFiles = database.getResultsOfVersion(oldVersion);
        assert newFiles.length > 0 && oldFiles.length > 0;
        return new FileWithResultsData[][]{oldFiles, newFiles};
    }

    /**
     * Resolves statistic test. Based on the command line options.
     * @param options Command line options.
     * @param config Configuration of the program.
     * @return Statistic test.
     */
    static StatisticTest resolveStatisticTest(OptionSet options, PerfEvalConfig config) {
        return options.has(TTEST_FLAG)
                ? new ParametricTest(config.getCritValue())
                : options.has(bootstrapSampleCountOption)
                        ? new NonparametricTest(config.getCritValue(), options.valueOf(bootstrapSampleCountOption))
                        : new NonparametricTest(config.getCritValue());
    }

    /**
     * Option for the filter option.
     */
    static ArgumentAcceptingOptionSpec<String> filterOption;
    /**
     * Option for the maximum count of tests.
     */
    static ArgumentAcceptingOptionSpec<Integer> maxTestCountOption;
    /**
     * Option for the bootstrap sample count.
     */
    static ArgumentAcceptingOptionSpec<Integer> bootstrapSampleCountOption;
    /**
     * Option for the new version.
     */
    static ArgumentAcceptingOptionSpec<String> newVersionOption;
    /**
     * Option for the old version.
     */
    static ArgumentAcceptingOptionSpec<String> oldVersionOption;
    /**
     * Option for the path.
     */
    static ArgumentAcceptingOptionSpec<String> pathOption;
    /**
     * Option for the version. Adding new file to database.
     */
    static ArgumentAcceptingOptionSpec<String> versionOption;
    /**
     * Option for the tag. Adding new file to database.
     */
    static ArgumentAcceptingOptionSpec<String> tagOption;
    /**
     * Option for the benchmark parser.
     */
    static ArgumentAcceptingOptionSpec<String> benchmarkParserOption;

    /**
     * Creates parser for parsing command line arguments.
     * @return Parser for parsing command line arguments.
     */
    public static OptionParser createParser() {
        OptionParser parser = new OptionParser();

        // Define options with arguments
        filterOption = parser.accepts(FILTER_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Filter option with a parameter");
        maxTestCountOption = parser.accepts(MAX_TIME_PARAMETER)
                .withRequiredArg()
                .ofType(Integer.class)
                .describedAs("Max count of test that it is possible to do");
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
        //parser.accepts(HELP_FLAG, "Print help message");
        //parser.accepts(GRAPHICAL_FLAG, "Enable graphical mode");
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(HTML_OUTPUT_FLAG, "Enable HTML output");
        parser.accepts(FORCE_FLAG, "Force the operation of init");
        parser.accepts(TTEST_FLAG, "Uses t-test instead of bootstrap");
        return parser;
    }

    /**
     * Creates database connection to the database file.
     * @param perfevalDir  Path to the directory containing the database file.
     * @return Database from the database file.
     * @throws DatabaseException If there is an error with the database.
     */
    public static Database constructDatabase(Path perfevalDir) throws DatabaseException {
        Path databasePath = perfevalDir.resolve(DATABASE_FILE_NAME);
        try {
            return H2Database.getDBFromFilePath(databasePath);

        } catch (SQLException e) {
            DatabaseException exception = new DatabaseException("DB cannot be instantiated", null, ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
    }
}
