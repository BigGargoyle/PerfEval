package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;
import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.GIT_FILE_NAME;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.PERFEVAL_DIR;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.constructDatabase;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.pathOption;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.resolveDate;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.resolveTag;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.resolveVersion;

/**
 * Command for adding all files from a directory to the database.
 */
public class IndexAllSetup implements CommandSetup {

    /**
     * Name of the command.
     */
    static final String commandName = "index-all-results";

    /**
     * Sets up the command.
     *
     * @param args    Command line arguments.
     * @param options Command line options.
     * @param config  Configuration of the program.
     * @return Command to be executed.
     * @throws DatabaseException If there is an error with the database.
     */
    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        Path sourceDir = Path.of(options.valueOf(pathOption));
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        try {
            String version = resolveVersion(gitFilePath, options);
            String tag = resolveTag(gitFilePath, options, version);
            Date date = resolveDate(gitFilePath, version);

            ProjectVersion projectVersion = new ProjectVersion(date, version, tag);

            if(version == null) {
                throw new DatabaseException("Version is null. Your repo is not clean or the version was not typed in.", ExitCode.databaseError);
            }
            if(date == null) {
                throw new DatabaseException("Date is null. Your repo is not clean or the date was not typed in.", ExitCode.databaseError);
            }

            return new AddFilesFromDirCommand(sourceDir, database, projectVersion);
        } catch (IOException e) {
            throw new DatabaseException("Git file not found: " + e.getMessage(), e, ExitCode.databaseError);
        }

    }

    /**
     * Returns the name of the command.
     * @return Name of the command.
     */
    public static String getCommandName() {
        return commandName;
    }
}
