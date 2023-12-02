package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;

import java.nio.file.Path;

/**
 * Command for adding a file to the database.
 */
public class AddFileCommand implements Command {
    /**
     * Path to the file to add.
     */
    final Path fileToAdd;
    /**
     * Database to add the file to.
     */
    final Database database;
    /**
     * Version of the project to add the file to.
     */
    final ProjectVersion version;

    /**
     * Creates a new instance of the command.
     *
     * @param fileToAdd Path to the file to add.
     * @param database  Database to add the file to.
     * @param version   Version of the project to add the file to.
     */
    public AddFileCommand(Path fileToAdd, Database database, ProjectVersion version) {
        this.fileToAdd = fileToAdd;
        this.database = database;
        this.version = version;
    }

    /**
     * Executes the command.
     *
     * @return Exit code of the command.
     * @throws PerfEvalCommandFailedException Thrown when the command fails.
     */
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
