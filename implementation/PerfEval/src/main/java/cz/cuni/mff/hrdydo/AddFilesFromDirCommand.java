package cz.cuni.mff.hrdydo;

import cz.cuni.mff.hrdydo.resultDatabase.DatabaseException;
import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalCommandFailedException;
import cz.cuni.mff.hrdydo.resultDatabase.Database;

import java.nio.file.Path;

public class AddFilesFromDirCommand implements Command {
    final Path sourceDirPath;
    final String version;
    final String tag;
    final Database database;

    public AddFilesFromDirCommand(Path sourceDirPath, Database database,String version, String tag){
        this.sourceDirPath = sourceDirPath;
        this.version = version;
        this.database = database;
        this.tag = tag;
    }

    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFilesFromDir(sourceDirPath, version, tag);

        } catch (DatabaseException e){
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
