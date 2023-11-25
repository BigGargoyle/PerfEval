package cz.cuni.mff.d3s.perfeval.command;

import cz.cuni.mff.d3s.perfeval.init.PerfEvalConfig;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;
import joptsimple.OptionSet;

import java.nio.file.Path;
import java.util.Date;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.*;

public class IndexAllSetup implements CommandSetup{

    static final String commandName = "index-all-results";
    @Override
    public Command setup(String[] args, OptionSet options, PerfEvalConfig config) throws DatabaseException {
        Path sourceDir = Path.of(options.valueOf(pathOption));
        Path gitFilePath = config.hasGitFilePresence() ? Path.of(args[0]).resolve(GIT_FILE_NAME) : null;
        Database database = constructDatabase(Path.of(args[0]).resolve(PERFEVAL_DIR));

        String version = resolveVersion(gitFilePath, options);
        String tag = resolveTag(gitFilePath,options,  version);
        Date date = resolveDate(gitFilePath, version);

        ProjectVersion projectVersion = new ProjectVersion(date, version, tag);

        assert version != null && tag != null;

        return new AddFilesFromDirCommand(sourceDir, database, projectVersion);
    }

    @Override
    public String getCommandName() {
        return commandName;
    }
}
