package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.IDatabase;

import java.nio.file.Path;

public class AddFileCommand implements ICommand {

    final PerfEvalConfig config;
    final Path gitFilePath;
    final Path fileToAdd;
    final IDatabase database;

    public AddFileCommand(Path fileToAdd, Path gitFilePath, IDatabase database, PerfEvalConfig config) {
        this.fileToAdd = fileToAdd;
        this.gitFilePath = gitFilePath;
        this.database = database;
        this.config = config;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFile(fileToAdd, gitFilePath, config);
        } catch (DatabaseException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
