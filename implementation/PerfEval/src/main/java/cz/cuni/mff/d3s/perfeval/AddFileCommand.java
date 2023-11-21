package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;

import java.nio.file.Path;

public class AddFileCommand implements Command {
    public static final String COMMAND = "index-new-result";
    final Path fileToAdd;
    final Database database;
    final ProjectVersion version;

    public AddFileCommand(Path fileToAdd, Database database, ProjectVersion version){
        this.fileToAdd = fileToAdd;
        this.database = database;
        this.version = version;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFile(fileToAdd, version);
        } catch (DatabaseException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
