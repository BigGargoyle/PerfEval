package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;

import java.nio.file.Path;

public class AddFilesFromDirCommand implements Command {
    public static final String COMMAND = "index-all-results";
    final Path sourceDirPath;
    final Database database;
    final ProjectVersion version;

    public AddFilesFromDirCommand(Path sourceDirPath, Database database, ProjectVersion version){
        this.sourceDirPath = sourceDirPath;
        this.database = database;
        this.version = version;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFilesFromDir(sourceDirPath, version);

        } catch (DatabaseException e){
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
