package org.example;

import org.example.globalVariables.ExitCode;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.IDatabase;

import java.nio.file.Path;

public class AddFilesFromDirCommand implements ICommand{

    final PerfEvalConfig config;
    final Path sourceDirPath;
    final Path gitFilePath;
    final IDatabase database;

    public AddFilesFromDirCommand(Path sourceDirPath, Path gitFilePath, IDatabase database, PerfEvalConfig config){
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
