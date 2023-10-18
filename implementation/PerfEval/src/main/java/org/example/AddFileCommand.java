package org.example;

import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.Database;

import java.nio.file.Path;

public class AddFileCommand implements Command {

    final Path fileToAdd;
    final String version;
    final String tag;
    final Database database;

    public AddFileCommand(Path fileToAdd, Database database,String version, String tag){
        this.fileToAdd = fileToAdd;
        this.version = version;
        this.database = database;
        this.tag = tag;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFile(fileToAdd, version, tag);
        } catch (DatabaseException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
