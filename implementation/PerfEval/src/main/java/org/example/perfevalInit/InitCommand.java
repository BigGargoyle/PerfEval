package org.example.perfevalInit;

import org.example.ICommand;
import org.example.globalVariables.ExitCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class InitCommand implements ICommand {
    public static PerfEvalConfig getConfig(Path iniFilePath) throws PerfEvalInvalidConfigException {
        if(isPerfevalInitializedInThisDirectory(iniFilePath)) {
            return readFromIniFile(iniFilePath);
        }
        return PerfEvalConfig.getDefaultConfig();
    }
    public static boolean isPerfevalInitializedInThisDirectory(Path perfevalDirPath) {
        return perfevalDirPath.toFile().isDirectory();
    }
    public static PerfEvalConfig readFromIniFile(Path iniFilePath){
        //TODO: find library to read from ini file
        return null;
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
        }
        else if(!forceFlag){
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
        createIniFile(iniFilePath, config);
        Path gitIgnoreFilePath = this.gitIgnorePath;
        createGitIgnoreFile(gitIgnoreFilePath, gitIgnoredFiles);
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
        if(!emptyFilePath.toFile().createNewFile()){
            throw new IOException("File "+emptyFilePath.getFileName()+" was not created");
        }
    }

    private static void createGitIgnoreFile(Path gitIgnoreFilePath, Path[] ignoredFiles) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(gitIgnoreFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        for(Path ignoredFile : ignoredFiles){
            writer.write(ignoredFile.getFileName().toString());
            writer.newLine();
        }
        writer.close();
    }

    private static void createIniFile(Path iniFilePath, PerfEvalConfig config) {
        //TODO: find library to create and read from ini file
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
