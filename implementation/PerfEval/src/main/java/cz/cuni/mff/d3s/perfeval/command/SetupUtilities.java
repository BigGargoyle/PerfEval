package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.Samples;
import cz.cuni.mff.d3s.perfeval.printers.HTMLPrinter;
import cz.cuni.mff.d3s.perfeval.printers.JSONPrinter;
import cz.cuni.mff.d3s.perfeval.printers.MeasurementComparisonRecord;
import cz.cuni.mff.d3s.perfeval.printers.ResultPrinter;
import cz.cuni.mff.d3s.perfeval.printers.TablePrinter;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.evaluation.ComparisonResult;
import cz.cuni.mff.d3s.perfeval.evaluation.NonparametricTest;
import cz.cuni.mff.d3s.perfeval.evaluation.ParametricTest;
import cz.cuni.mff.d3s.perfeval.evaluation.StatisticTest;
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
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;

/**
 * Class containing utilities for parsing and setup of commands.
 */
public class SetupUtilities {
    /**
     * Path to the user directory.
     */
    public static final Path USER_DIR = Path.of(System.getProperty("user.dir"));
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
    public static final String GIT_FILE_NAME = ".git";

    /**
     * Only for use with init command. New PerfEval system will be initialized. Old system will be deleted.
     */
    static final String FORCE_FLAG = "force";

    /**
     * Help flag for printing help message.
     */
    static final String HELP_FLAG = "help";
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
     * Name of the parameter for the HTML template option.
     */
    private static final String HTML_TEMPLATE_PARAMETER = "html-template";

    /**
     * Name of the parameter for the input of the new version.
     */
    private static final String NEW_VERSION_PARAMETER = "new-version";

    /**
     * Name of the parameter for the input of the new tag.
     */
    private static final String NEW_TAG_PARAMETER = "new-tag";

    /**
     * Name of the parameter for the input of the old version.
     */
    private static final String OLD_VERSION_PARAMETER = "old-version";

    /**
     * Name of the parameter for the input of the old tag.
     */
    private static final String OLD_TAG_PARAMETER = "old-tag";

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
     *
     * @param <T> Type of the elements.
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
     *
     * @param gitFilePath Path to the git file.
     * @param options     Command line options.
     * @return Version of the software.
     * @throws IOException If there is an error with the git file.
     */
    static String resolveVersion(Path gitFilePath, OptionSet options) throws IOException, DatabaseException {
        if (options.has(versionOption)) {
            return options.valueOf(versionOption);
        }
        if (gitFilePath == null) {
            throw new DatabaseException("Git file not found. Use --version argument to set version.", ExitCode.databaseError);
        }

        if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
            RevCommit lastCommit = GitUtilities.getLastCommit(gitFilePath.getParent());
            assert lastCommit != null;
            return lastCommit.getName();
        }
        //System.err.println("Version cannot be resolved");
        throw new DatabaseException("Git repo is not clean. Use --version argument to set version.", ExitCode.databaseError);

    }

    /**
     * Resolves tag of the software. Based on the git file, command line
     * options and config file.
     *
     * @param gitFilePath Path to the git file.
     * @param options     Command line options.
     * @param version     Version of the software.
     * @return Tag of the software.
     * @throws IOException If there is an error with the git file.
     */
    static String resolveTag(Path gitFilePath, OptionSet options, String version) throws IOException {
        if (options.has(tagOption)) {
            return options.valueOf(tagOption);
        }
        if (gitFilePath != null) {
            if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
                return GitUtilities.getLastCommitTag(gitFilePath.getParent(), version);
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Resolves date of the last update of software. Based on the git file.
     *
     * @param gitFilePath Path to the git file.
     * @param versionHash Version of the software.
     * @return Date of the software.
     * @throws IOException If there is an error with the git file.
     */
    static Date resolveDate(Path gitFilePath, String versionHash) throws IOException {
        if (gitFilePath == null) {
            return getDateFromDatabase(versionHash);
        }
        if (GitUtilities.isRepoClean(gitFilePath.getParent())) {
            return GitUtilities.getCommitDate(gitFilePath.getParent(), versionHash);
        }
        return getDateFromDatabase(versionHash);
    }

    /**
     * Resolves date of the last update of software. Based on the Perfeval database.
     * @param versionHash Version of the software.
     * @return Date of the software version that is in the database or current date.
     */
    private static Date getDateFromDatabase(String versionHash) {
        try {
            Database database = constructDatabase(USER_DIR.resolve(PERFEVAL_DIR));
            return database.getDateOfVersionHash(versionHash);
        } catch (DatabaseException e) {
            return Date.from(Instant.now());
        }
    }

    /**
     * Resolves printer for printing results. Based on the command line options.
     *
     * @param options Command line options.
     * @return Printer for the evaluate command.
     * @throws ParserException If there is an error with parsing the command line arguments.
     */
    static ResultPrinter resolvePrinterForEvaluateCommand(OptionSet options, Path perfevalDir) throws ParserException {
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
            if (options.has(htmlTemplateOption)) {
                if (options.valueOf(htmlTemplateOption).charAt(0) == '/') {
                    return new HTMLPrinter(printStream, filter, perfevalDir, Path.of(options.valueOf(htmlTemplateOption)));
                }
                return new HTMLPrinter(printStream, filter, perfevalDir, USER_DIR.resolve(options.valueOf(htmlTemplateOption)).normalize());
            }
            return new HTMLPrinter(printStream, filter, perfevalDir);
        }
        if (options.has(JSON_OUTPUT_FLAG)) {
            return new JSONPrinter(printStream, filter);
        }
        return new TablePrinter(printStream, filter);
    }

    /**
     * Resolves input files with respect to inputted versions. Based on the command line options.
     *
     * @param options Command line options.
     * @return Input files with respect to inputted versions.
     * @throws DatabaseException If there is an error with the database.
     */
    static FileWithResultsData[][] resolveInputFilesWithRespectToInputtedVersions(OptionSet options) throws DatabaseException {
        Database database = constructDatabase(USER_DIR.resolve(PERFEVAL_DIR));

        boolean newVersionSet = options.has(newVersionOption) || options.has(newTagOption);
        boolean oldVersionSet = options.has(oldVersionOption) || options.has(oldTagOption);

        FileWithResultsData[] newFiles;
        FileWithResultsData[] oldFiles;

        if (newVersionSet && oldVersionSet) {
            ProjectVersion newVersion = new ProjectVersion(null, options.valueOf(newVersionOption), options.valueOf(newTagOption));
            ProjectVersion oldVersion = new ProjectVersion(null, options.valueOf(oldVersionOption), options.valueOf(oldTagOption));
            newFiles = database.getResultsOfVersion(newVersion);
            oldFiles = database.getResultsOfVersion(oldVersion);
        } else if (newVersionSet) {
            ProjectVersion newVersion = new ProjectVersion(null, options.valueOf(newVersionOption), options.valueOf(newTagOption));
            newFiles = database.getResultsOfVersion(newVersion);
            ProjectVersion oldVersion = database.findOlderNeighbourVersion(newVersion);
            oldFiles = database.getResultsOfVersion(oldVersion);
        } else if (oldVersionSet) {
            ProjectVersion oldVersion = new ProjectVersion(null, options.valueOf(oldVersionOption), options.valueOf(oldTagOption));
            oldFiles = database.getResultsOfVersion(oldVersion);
            ProjectVersion newVersion = database.findNewerNeighbourVersion(oldVersion);
            newFiles = database.getResultsOfVersion(newVersion);
        } else {
            ProjectVersion[] versions = database.getLastNVersions(2);
            if (versions.length < 2) {
                throw new DatabaseException("There are not enough versions in the database.", ExitCode.databaseError);
            }
            newFiles = database.getResultsOfVersion(versions[0]);
            oldFiles = database.getResultsOfVersion(versions[1]);
        }
        // System.out.println(oldFiles[0].version());
        // System.out.println(newFiles[0].version());
        return new FileWithResultsData[][]{oldFiles, newFiles};
    }

    /**
     * Resolves statistic test. Based on the command line options.
     *
     * @param options Command line options.
     * @param config  Configuration of the program.
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
     * Option for the new tag.
     */
    static ArgumentAcceptingOptionSpec<String> newTagOption;

    /**
     * Option for the old version.
     */
    static ArgumentAcceptingOptionSpec<String> oldVersionOption;
    /**
     * Option for the old tag.
     */
    static ArgumentAcceptingOptionSpec<String> oldTagOption;

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
     * Option for the HTML template.
     */
    static ArgumentAcceptingOptionSpec<String> htmlTemplateOption;

    /**
     * Creates parser for parsing command line arguments.
     *
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
        newTagOption = parser.accepts(NEW_TAG_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Tag of measured software");
        oldVersionOption = parser.accepts(OLD_VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of measured software");
        oldTagOption = parser.accepts(OLD_TAG_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Tag of measured software");
        pathOption = parser.accepts(PATH_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Path to file or dir to be added to database");
        versionOption = parser.accepts(VERSION_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Version of file/s that are added to database");
        tagOption = parser.accepts(TAG_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Tag of file/s that are added to database");
        benchmarkParserOption = parser.accepts(BENCHMARK_PARSER_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("Benchmark parser that will be used for parsing the input files.");
        htmlTemplateOption = parser.accepts(HTML_TEMPLATE_PARAMETER)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("HTML template that will be used for printing the results in HTML output.");

        // Define flags (options without arguments)
        parser.accepts(JSON_OUTPUT_FLAG, "Enable JSON output");
        parser.accepts(HTML_OUTPUT_FLAG, "Enable HTML output");
        parser.accepts(FORCE_FLAG, "Force the operation of init");
        parser.accepts(TTEST_FLAG, "Uses t-test instead of bootstrap");
        parser.accepts(HELP_FLAG, "Print help message");
        return parser;
    }

    /**
     * Creates database connection to the database file.
     *
     * @param perfevalDir Path to the directory containing the database file.
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
