package org.example.perfevalInit;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.example.Command;
import org.example.ExitCode;
import org.example.measurementFactory.UniversalTimeUnit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

public class InitCommand implements Command {

    private static final String CRIT_VALUE_KEY = "critValue.key";
    private static final String MAX_CI_WIDTH_KEY = "maxCIWidth.key";
    private static final String MAX_TIME_KEY = "maxTime.key";
    private static final String VERSION_KEY = "version.key";
    private static final String GIT_PRESENCE_KEY = "git.key";
    private static final String TRUE_STRING = "TRUE";
    private static final String FALSE_STRING = "FALSE";

    public static PerfEvalConfig getConfig(Path iniFilePath) throws PerfEvalInvalidConfigException {
        if (isPerfevalInitializedInThisDirectory(iniFilePath)) {
            try {
                return readFromIniFile(iniFilePath);
            } catch (ConfigurationException e){
                PerfEvalInvalidConfigException exception = new PerfEvalInvalidConfigException();
                exception.initCause(e);
                throw exception;
            }
        }
        return PerfEvalConfig.getDefaultConfig();
    }

    public static boolean isPerfevalInitializedInThisDirectory(Path perfevalDirPath) {
        return perfevalDirPath.toFile().isDirectory();
    }

    public static PerfEvalConfig readFromIniFile(Path iniFilePath) throws ConfigurationException, PerfEvalInvalidConfigException {
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(iniFilePath.toFile());
        double critValue = Double.parseDouble(config.getString(CRIT_VALUE_KEY));
        double maxCIWidth = Double.parseDouble(config.getString(MAX_CI_WIDTH_KEY));
        UniversalTimeUnit timeUnit = new UniversalTimeUnit(Long.parseLong(config.getString(MAX_TIME_KEY)), TimeUnit.NANOSECONDS);
        String version = config.getString(VERSION_KEY);
        boolean gitPresence = config.getString(GIT_PRESENCE_KEY).compareTo(TRUE_STRING) == 0;
        return new PerfEvalConfig(gitPresence, timeUnit, maxCIWidth, critValue, version);
    }

    private static void createIniFile(Path iniFilePath, PerfEvalConfig perfevalConfig) throws ConfigurationException, IOException {
        if(iniFilePath.toFile().exists() && !iniFilePath.toFile().delete())
            throw new IOException("ini file cannot be deleted");
        FileWriter writer = new FileWriter(iniFilePath.toFile());
        Configurations configs = new Configurations();
        INIConfiguration config = configs.ini(iniFilePath.toFile());
        config.setProperty(CRIT_VALUE_KEY, perfevalConfig.getCritValue());
        config.setProperty(MAX_CI_WIDTH_KEY, perfevalConfig.getMaxCIWidth());
        config.setProperty(MAX_TIME_KEY, perfevalConfig.getMaxTimeOnTest().getNanoSeconds());
        String gitPresenceString = perfevalConfig.hasGitFilePresence() ? TRUE_STRING : FALSE_STRING;
        config.setProperty(GIT_PRESENCE_KEY, gitPresenceString);
        config.setProperty(VERSION_KEY, perfevalConfig.getVersion());
        config.write(writer);
        writer.close();
    }

    final boolean forceFlag;
    final Path perfevalDirPath;
    final Path gitIgnorePath;
    final Path iniFilePath;
    final Path helpFilePath;
    final String helpFileContent;
    final Path[] emptyFilesToCreate;
    final Path[] gitIgnoredFiles;
    final PerfEvalConfig config;

    public InitCommand(Path perfevalDirPath, Path gitIgnorePath, Path iniFilePath, Path helpFilePath,
                       String helpFileContent, Path[] emptyFilesToCreate, Path[] gitIgnoredFiles, PerfEvalConfig config, boolean forceFlag) {
        this.gitIgnorePath = gitIgnorePath;
        this.iniFilePath = iniFilePath;
        this.emptyFilesToCreate = emptyFilesToCreate;
        this.config = config;
        this.perfevalDirPath = perfevalDirPath;
        this.helpFileContent = helpFileContent;
        this.helpFilePath = helpFilePath;
        this.gitIgnoredFiles = gitIgnoredFiles;
        this.forceFlag = forceFlag;
    }

    /**
     * @throws PerfEvalCommandFailedException
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

    private void createPerfEvalFiles() throws IOException {
        Path iniFilePath = this.iniFilePath;
        try {
            createIniFile(iniFilePath, config);
        }catch (ConfigurationException e){
            IOException exception = new IOException("Config file cannot be created");
            exception.initCause(e);
            throw exception;
        }
        createGitIgnoreFile(this.gitIgnorePath, gitIgnoredFiles);
        Path helpFilePath = this.helpFilePath;
        createHelpFile(helpFilePath, helpFileContent);
        for (Path emptyFile : emptyFilesToCreate) {
            createEmptyFile(emptyFile);
        }
    }

    private static void createHelpFile(Path helpFilePath, String helpFileContent) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(helpFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        writer.write(helpFileContent);
        writer.close();
    }

    private static void createEmptyFile(Path emptyFilePath) throws IOException {
        if (!emptyFilePath.toFile().createNewFile()) {
            throw new IOException("File " + emptyFilePath.getFileName() + " was not created");
        }
    }

    private static void createGitIgnoreFile(Path gitIgnoreFilePath, Path[] ignoredFiles) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(gitIgnoreFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        for (Path ignoredFile : ignoredFiles) {
            writer.write(ignoredFile.getFileName().toString());
            writer.newLine();
        }
        writer.close();
    }

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
