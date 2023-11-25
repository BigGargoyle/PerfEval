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
import java.time.Duration;

/**
 * Command for initializing perfeval in directory
 */
public class InitCommand implements Command {
    /**
     * String representation of key for critical value in ini file
     */
    private static final String CRIT_VALUE_KEY = "critValue.key";
    /**
     * String representation of key for maximal confidence interval width in ini file
     */
    private static final String MAX_CI_WIDTH_KEY = "maxCIWidth.key";
    /**
     * String representation of key for maximal time on test in ini file
     */
    private static final String MAX_TIME_KEY = "maxTime.key";
    /**
     * String representation of key for version in ini file
     */
    private static final String VERSION_KEY = "version.key";
    /**
     * String representation of key for git presence in ini file
     */
    private static final String GIT_PRESENCE_KEY = "git.key";
    /**
     * String representation of key for parser name in ini file
     */
    private static final String PARSER_NAME_KEY = "parserName.key";
    /**
     * String representation of key for tolerance in ini file
     */
    private static final String TOLERANCE_KEY = "tolerance.key";
    /**
     * String representation of true in ini file
     */
    private static final String TRUE_STRING = "TRUE";
    /**
     * String representation of false in ini file
     */
    private static final String FALSE_STRING = "FALSE";

    /**
     * Returns default config if perfeval is not initialized in this directory
     * Otherwise returns config from ini file
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
     * Returns true if perfeval is initialized in this directory
     *
     * @param perfevalDirPath path to perfeval directory
     * @return true if perfeval is initialized in this directory, false otherwise
     */
    public static boolean isPerfevalInitializedInThisDirectory(Path perfevalDirPath) {
        return perfevalDirPath.toFile().isDirectory();
    }

    /**
     * Reads config from ini file
     *
     * @param iniFilePath path to ini file
     * @return config from ini file
     * @throws ConfigurationException         if ini file cannot be read
     * @throws PerfEvalInvalidConfigException if config (instance of PerfEvalConfig) cannot be created
     */
    public static PerfEvalConfig readFromIniFile(Path iniFilePath) throws ConfigurationException, PerfEvalInvalidConfigException {
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(iniFilePath.toFile());
        double critValue = Double.parseDouble(config.getString(CRIT_VALUE_KEY));
        double maxCIWidth = Double.parseDouble(config.getString(MAX_CI_WIDTH_KEY));
        Duration timeUnit = Duration.ofNanos(Long.parseLong(config.getString(MAX_TIME_KEY)));
        String version = config.getString(VERSION_KEY);
        boolean gitPresence = config.getString(GIT_PRESENCE_KEY).compareTo(TRUE_STRING) == 0;
        String parserName = config.getString(PARSER_NAME_KEY);
        MeasurementParser parser = ParserFactory.getParser(parserName);
        double tolerance = Double.parseDouble(config.getString(TOLERANCE_KEY));
        return new PerfEvalConfig(gitPresence, timeUnit, maxCIWidth, critValue, version, parser, tolerance);
    }

    /**
     * Creates ini file
     *
     * @param iniFilePath    path to ini file
     * @param perfevalConfig config to be saved to ini file
     * @throws ConfigurationException if ini file cannot be created
     * @throws IOException            if ini file cannot be created
     */
    private static void createIniFile(Path iniFilePath, PerfEvalConfig perfevalConfig) throws ConfigurationException, IOException {
        if (iniFilePath.toFile().exists() && !iniFilePath.toFile().delete())
            throw new IOException("ini file cannot be deleted");

        INIConfiguration config = new INIConfiguration();

        // Set INI properties
        config.setProperty(CRIT_VALUE_KEY, perfevalConfig.getCritValue());
        config.setProperty(MAX_CI_WIDTH_KEY, perfevalConfig.getMaxCIWidth());
        config.setProperty(MAX_TIME_KEY, perfevalConfig.getMaxTimeOnTest().getNano());
        String gitPresenceString = perfevalConfig.hasGitFilePresence() ? TRUE_STRING : FALSE_STRING;
        config.setProperty(GIT_PRESENCE_KEY, gitPresenceString);
        config.setProperty(VERSION_KEY, perfevalConfig.getVersion());
        config.setProperty(PARSER_NAME_KEY, perfevalConfig.getMeasurementParser().getParserName());
        config.setProperty(TOLERANCE_KEY, perfevalConfig.getTolerance());

        // Save to the INI file
        FileWriter writer = new FileWriter(iniFilePath.toFile());
        config.write(writer);
        writer.close();
    }

    /**
     * true if perfeval should be initialized even if it is already initialized in this directory, false otherwise
     */
    final boolean forceFlag;
    /**
     * path to perfeval directory
     */
    final Path perfevalDirPath;
    /**
     * path to gitignore file
     */
    final Path gitIgnorePath;
    /**
     * path to ini file
     */
    final Path iniFilePath;
    /**
     * paths to files that should be created
     */
    final Path[] emptyFilesToCreate;
    /**
     * paths to files that should be ignored by git
     */
    final Path[] gitIgnoredFiles;
    /**
     * config to be saved to ini file
     */
    final PerfEvalConfig config;

    /**
     * Constructor for InitCommand
     *
     * @param perfevalDirPath    path to perfeval directory
     * @param gitIgnorePath      path to gitignore file
     * @param iniFilePath        path to ini file
     * @param emptyFilesToCreate paths to files that should be created
     * @param gitIgnoredFiles    paths to files that should be ignored by git
     * @param config             config to be saved to ini file
     * @param forceFlag          true if perfeval should be initialized even if it is already initialized in this directory, false otherwise
     */
    public InitCommand(Path perfevalDirPath, Path gitIgnorePath, Path iniFilePath, Path[] emptyFilesToCreate,
                       Path[] gitIgnoredFiles, PerfEvalConfig config, boolean forceFlag) {
        this.gitIgnorePath = gitIgnorePath;
        this.iniFilePath = iniFilePath;
        this.emptyFilesToCreate = emptyFilesToCreate;
        this.config = config;
        this.perfevalDirPath = perfevalDirPath;
        this.gitIgnoredFiles = gitIgnoredFiles;
        this.forceFlag = forceFlag;
    }

    /**
     * Executes init command
     *
     * @return exit code, OK if init was successful, notInitialized if perfeval is already initialized in this directory and force flag is not set
     * @throws PerfEvalCommandFailedException if init fails
     */
    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        if (isPerfevalInitializedInThisDirectory(perfevalDirPath)) {
            try {
                deleteDirectory(perfevalDirPath);
            } catch (IOException e) {
                PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.notInitialized);
                exception.initCause(e);
                throw exception;
            }
        } else if (!forceFlag) {
            throw new PerfEvalCommandFailedException(ExitCode.notInitialized);
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
     * Creates perfeval files
     *
     * @throws IOException if perfeval files cannot be created
     */
    private void createPerfEvalFiles() throws IOException {
        Path iniFilePath = this.iniFilePath;
        Files.createDirectory(perfevalDirPath);
        try {
            createIniFile(iniFilePath, config);
        } catch (ConfigurationException e) {
            throw new IOException("Config file cannot be created");
        }
        createGitIgnoreFile(this.gitIgnorePath, gitIgnoredFiles);
        for (Path emptyFile : emptyFilesToCreate) {
            createEmptyFile(emptyFile);
        }
    }

    /**
     * Creates empty file
     *
     * @param emptyFilePath path to empty file to be created
     * @throws IOException if empty file cannot be created
     */
    private static void createEmptyFile(Path emptyFilePath) throws IOException {
        if (!emptyFilePath.toFile().createNewFile()) {
            throw new IOException("File " + emptyFilePath.getFileName() + " was not created");
        }
    }

    /**
     * Creates gitignore file
     *
     * @param gitIgnoreFilePath path to gitignore file to be created
     * @param ignoredFiles      paths to files that should be ignored by git
     * @throws IOException if gitignore file cannot be created
     */
    private static void createGitIgnoreFile(Path gitIgnoreFilePath, Path[] ignoredFiles) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(gitIgnoreFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        for (Path ignoredFile : ignoredFiles) {
            writer.write(ignoredFile.getFileName().toString());
            writer.newLine();
        }
        writer.close();
    }

    /**
     * Deletes directory
     *
     * @param dirPath path to directory to be deleted
     * @throws IOException if directory cannot be deleted
     */
    private static void deleteDirectory(Path dirPath) throws IOException {
        if (Files.isDirectory(dirPath)) {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath);
            for (Path entry : directoryStream)
                deleteDirectory(entry);
            directoryStream.close();
        }
        Files.delete(dirPath);
    }
}
