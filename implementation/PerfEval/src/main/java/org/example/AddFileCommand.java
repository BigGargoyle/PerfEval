package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.Database;

import java.nio.file.Path;

public class AddFileCommand implements Command {

    final PerfEvalConfig config;
    final Path gitFilePath;
    final Path fileToAdd;
    final Database database;

    public AddFileCommand(Path fileToAdd, Path gitFilePath, Database database, PerfEvalConfig config) {
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
