package cz.cuni.mff.d3s.perfeval;

import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;

import java.nio.file.Path;

/**
 * Command for adding all files from a directory to the database.
 */
public class AddFilesFromDirCommand implements Command {
    /**
     * String representation of the command.
     */
    public static final String COMMAND = "index-all-results";
    /**
     * Directory from which to add files.
     */
    final Path sourceDirPath;
    /**
     * Database to which to add files.
     */
    final Database database;
    /**
     * Version of the project to which the files belong.
     */
    final ProjectVersion version;

    /**
     * Creates a new command.
     *
     * @param sourceDirPath Directory from which to add files.
     * @param database      Database to which to add files.
     * @param version       Version of the project to which the files belong.
     */
    public AddFilesFromDirCommand(Path sourceDirPath, Database database, ProjectVersion version) {
        this.sourceDirPath = sourceDirPath;
        this.database = database;
        this.version = version;
    }

    /**
     * Executes the command to add all files from directory to database.
     *
     * @return Exit code of the command.
     * @throws PerfEvalCommandFailedException If the command fails.
     */
    @Override
    public ExitCode execute() throws PerfEvalCommandFailedException {
        try {
            database.addFilesFromDir(sourceDirPath, version);

        } catch (DatabaseException e) {
            PerfEvalCommandFailedException exception = new PerfEvalCommandFailedException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
        return ExitCode.OK;
    }
}
