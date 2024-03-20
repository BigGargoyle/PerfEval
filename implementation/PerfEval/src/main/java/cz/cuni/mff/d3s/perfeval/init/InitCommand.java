package cz.cuni.mff.d3s.perfeval.init;

import cz.cuni.mff.d3s.perfeval.command.Command;
import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.measurementfactory.MeasurementParser;
import cz.cuni.mff.d3s.perfeval.measurementfactory.ParserFactory;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.GIT_FILE_NAME;

/**
 * Command for initializing perfeval in directory.
 */
public class InitCommand implements Command {
    /**
     * String representation of key for critical value in ini file.
     */
    private static final String CRIT_VALUE_KEY = "statistic.falseAlarmProbability";
    /**
     * String representation of key for maximal confidence interval width in ini file.
     */
    private static final String MAX_CI_WIDTH_KEY = "statistic.accuracy";
    /**
     * String representation of key for maximal time on test in ini file.
     */
    private static final String MAX_TEST_COUNT_KEY = "statistic.maxTestCount";
    /**
     * String representation of key for minimal test count in ini file.
     */
    private static final String MIN_TEST_COUNT_KEY = "statistic.minTestCount";
    /**
     * String representation of key for tolerance in ini file.
     */
    private static final String TOLERANCE_KEY = "statistic.tolerance";
    /**
     * String representation of key for high run demands in ini file.
     */
    private static final String HIGH_RUN_DEMAND_ALERT_KEY = "alerts.highDemandOfRuns";
    /**
     * String representation of key for improved performance in ini file.
     */
    private static final String IMPROVED_PERFORMANCE_ALERT_KEY = "alerts.improvedPerformance";
    /**
     * String representation of key for git presence in ini file.
     */
    private static final String GIT_PRESENCE_KEY = "project.git";
    /**
     * String representation of key for parser name in ini file.
     */
    private static final String PARSER_NAME_KEY = "project.parserName";
    /**
     * String representation of true in ini file.
     */
    private static final String TRUE_STRING = "TRUE";
    /**
     * String representation of false in ini file.
     */
    private static final String FALSE_STRING = "FALSE";

    /**
     * Returns default config if perfeval is not initialized in this directory
     * otherwise returns config from ini file.
     *
     * @param iniFilePath path to ini file
     * @return config from ini file or default config
     * @throws PerfEvalInvalidConfigException if config cannot be created
     */
    public static PerfEvalConfig getConfig(Path iniFilePath) throws PerfEvalInvalidConfigException {
        if (isPerfevalInitializedInThisDirectory(iniFilePath)) {
            try {
                return readFromIniFile(iniFilePath);
            } catch (ConfigurationException e) {
                PerfEvalInvalidConfigException exception = new PerfEvalInvalidConfigException();
                exception.initCause(e);
                throw exception;
            }
        }
        return PerfEvalConfig.getDefaultConfig();
    }

    /**
     * Returns true if perfeval is initialized in this directory.
     *
     * @param iniFilePath path to ini file
     * @return true if perfeval is initialized in this directory, false otherwise
     */
    private static boolean isPerfevalInitializedInThisDirectory(Path iniFilePath) {
        return iniFilePath.toFile().exists();
    }

    /**
     * Reads config from ini file.
     *
     * @param iniFilePath path to ini file
     * @return config from ini file
     * @throws ConfigurationException         if ini file cannot be read
     * @throws PerfEvalInvalidConfigException if config (instance of PerfEvalConfig) cannot be created
     */
    private static PerfEvalConfig readFromIniFile(Path iniFilePath) throws ConfigurationException, PerfEvalInvalidConfigException {
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(iniFilePath.toFile());
        double critValue = Double.parseDouble(config.getString(CRIT_VALUE_KEY));
        double maxCIWidth = Double.parseDouble(config.getString(MAX_CI_WIDTH_KEY));
        int maxTestCount = config.getInt(MAX_TEST_COUNT_KEY);
        int minTestCount = config.getInt(MIN_TEST_COUNT_KEY);
        boolean gitPresence = config.getString(GIT_PRESENCE_KEY).compareTo(TRUE_STRING) == 0;
        String parserName = config.getString(PARSER_NAME_KEY);
        MeasurementParser parser = ParserFactory.getParser(parserName);
        double tolerance = Double.parseDouble(config.getString(TOLERANCE_KEY));
        boolean highRunDemandsAlert = config.getString(HIGH_RUN_DEMAND_ALERT_KEY).compareTo(TRUE_STRING) == 0;
        boolean improvedPerformanceAlert = config.getString(IMPROVED_PERFORMANCE_ALERT_KEY).compareTo(TRUE_STRING) == 0;

        if(critValue <= 0 || critValue >= 1){
            throw new PerfEvalInvalidConfigException("Critical value must be in (0, 1)");
        }
        if(maxCIWidth <= 0 || maxCIWidth >= 1){
            throw new PerfEvalInvalidConfigException("Maximal width of confidence interval must be in (0, 1)");
        }
        if(minTestCount < 0) {
            throw new PerfEvalInvalidConfigException("Minimal count of tests must be non-negative.");
        }
        if(minTestCount > maxTestCount) {
            throw new PerfEvalInvalidConfigException("Minimal count of tests must be less than maximal count of tests.");
        }
        if(maxTestCount <= 0){
            throw new PerfEvalInvalidConfigException("Maximal count of tests must be positive");
        }
        if(tolerance < 0 || tolerance > 1){
            throw new PerfEvalInvalidConfigException("Tolerance must be in [0, 1]");
        }

        return new PerfEvalConfig(gitPresence, minTestCount, maxTestCount, maxCIWidth, critValue, parser, tolerance, highRunDemandsAlert, improvedPerformanceAlert);
    }

    /**
     * Creates ini file.
     *
     * @param iniFilePath    path to ini file
     * @param perfevalConfig config to be saved to ini file
     * @throws ConfigurationException if ini file cannot be created
     * @throws IOException            if ini file cannot be created
     */
    private static void createIniFile(Path iniFilePath, PerfEvalConfig perfevalConfig) throws ConfigurationException, IOException {
        if (iniFilePath.toFile().exists() && !iniFilePath.toFile().delete()) {
            throw new IOException("ini file cannot be deleted");
        }

        Path gitFilePath = iniFilePath.getParent().getParent().resolve(GIT_FILE_NAME);
        boolean isThereAGitFile = gitFilePath.toFile().exists();

        INIConfiguration config = new INIConfiguration();

        // Set INI properties
        config.setProperty(CRIT_VALUE_KEY, perfevalConfig.getCritValue());
        config.setProperty(MAX_CI_WIDTH_KEY, perfevalConfig.getMaxCIWidth());
        config.setProperty(MIN_TEST_COUNT_KEY, perfevalConfig.getMinTestCount());
        config.setProperty(MAX_TEST_COUNT_KEY, perfevalConfig.getMaxTestCount());
        String gitPresenceString = perfevalConfig.hasGitFilePresence() || isThereAGitFile ? TRUE_STRING : FALSE_STRING;
        config.setProperty(GIT_PRESENCE_KEY, gitPresenceString);
        //solve parser name --> method toString()
        config.setProperty(PARSER_NAME_KEY, perfevalConfig.getMeasurementParser().toString());
        config.setProperty(TOLERANCE_KEY, perfevalConfig.getTolerance());
        config.setProperty(HIGH_RUN_DEMAND_ALERT_KEY, perfevalConfig.isHighRunDemandAlert() ? TRUE_STRING : FALSE_STRING);
        config.setProperty(IMPROVED_PERFORMANCE_ALERT_KEY, perfevalConfig.isImprovedPerformanceAlert() ? TRUE_STRING : FALSE_STRING);

        // Save to the INI file
        FileWriter writer = new FileWriter(iniFilePath.toFile());
        config.write(writer);
        writer.close();
    }

    /**
     * True if perfeval should be initialized even if it is already initialized in this directory, false otherwise.
     */
    final boolean forceFlag;
    /**
     * Path to perfeval directory.
     */
    final Path perfevalDirPath;
    /**
     * Path to gitignore file.
     */
    final Path gitIgnorePath;
    /**
     * Path to ini file.
     */
    final Path iniFilePath;
    /**
     * Config to be saved to ini file.
     */
    final PerfEvalConfig config;

    /**
     * Constructor for InitCommand.
     *
     * @param perfevalDirPath    path to perfeval directory
     * @param gitIgnorePath      path to gitignore file
     * @param iniFilePath        path to ini file
     * @param config             config to be saved to ini file
     * @param forceFlag          true if perfeval should be initialized even if it is already initialized in this directory, false otherwise
     */
    public InitCommand(Path perfevalDirPath, Path gitIgnorePath, Path iniFilePath,
                       PerfEvalConfig config, boolean forceFlag) {
        this.gitIgnorePath = gitIgnorePath;
        this.iniFilePath = iniFilePath;
        this.config = config;
        this.perfevalDirPath = perfevalDirPath;
        this.forceFlag = forceFlag;
    }

    /**
     * Executes init command.
     *
     * @return exit code, OK if init was successful, notInitialized if perfeval is already initialized in this directory and force flag is not set
     * @throws PerfEvalCommandFailedException if init fails
     */
    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        if(forceFlag && isPerfevalInitializedInThisDirectory(perfevalDirPath)) {
            try {
                deleteDirectory(perfevalDirPath);
            } catch (IOException e) {
                PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.notInitialized);
                exception.initCause(e);
                throw exception;
            }
        } else if (isPerfevalInitializedInThisDirectory(perfevalDirPath)) {
            return ExitCode.alreadyInitialized;
        }
        try {
            createPerfEvalFiles();
        } catch (IOException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.notInitialized);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }

    /**
     * Creates perfeval files.
     *
     * @throws IOException if perfeval files cannot be created
     */
    private void createPerfEvalFiles() throws IOException {
        Path iniFilePath = this.iniFilePath;
        Files.createDirectory(perfevalDirPath);
        try {
            createIniFile(iniFilePath, config);
        } catch (ConfigurationException e) {
            throw new IOException("Config file cannot be created", e);
        }
        createGitIgnoreFile(this.gitIgnorePath);
    }

    /**
     * Creates gitignore file.
     *
     * @param gitIgnoreFilePath path to gitignore file to be created
     * @throws IOException if gitignore file cannot be created
     */
    private static void createGitIgnoreFile(Path gitIgnoreFilePath) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(gitIgnoreFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        writer.write("# Ignore perfeval files");
        writer.newLine();
        writer.write("*");
        writer.newLine();
        writer.close();
    }

    /**
     * Deletes directory.
     *
     * @param dirPath path to directory to be deleted
     * @throws IOException if directory cannot be deleted
     */
    private static void deleteDirectory(Path dirPath) throws IOException {
        if (Files.isDirectory(dirPath)) {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath);
            for (Path entry : directoryStream) {
                deleteDirectory(entry);
            }
            directoryStream.close();
        }
        Files.delete(dirPath);
    }
}
