package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.Database;

import java.nio.file.Path;

public class AddFilesFromDirCommand implements Command {

    final PerfEvalConfig config;
    final Path sourceDirPath;
    final Path gitFilePath;
    final Database database;

    public AddFilesFromDirCommand(Path sourceDirPath, Path gitFilePath, Database database, PerfEvalConfig config){
        this.sourceDirPath = sourceDirPath;
        this.gitFilePath = gitFilePath;
        this.database = database;
        this.config = config;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFilesFromDir(sourceDirPath, gitFilePath, config);

        } catch (DatabaseException e){
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
