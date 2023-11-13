import cz.cuni.mff.d3s.perfeval.Command;
import cz.cuni.mff.d3s.perfeval.Parser;
import cz.cuni.mff.d3s.perfeval.resultDatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultDatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultDatabase.ProjectVersion;
import cz.cuni.mff.d3s.perfeval.resultDatabase.H2Database;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {
    //static String PWD = "test-directory";
    static String[] testEvaluateCLIValidLines = new String[]{
        "evaluate",
        "evaluate --old-version version1",
        "evaluate --old-version=version1",
        "evaluate --old-version version1 --new-version version2",
        "evaluate --new-version version1 --old-version version2",
        "evaluate --old-version version1 --t-test",
        "evaluate --old-version version1 --json-output",
        "evaluate --old-version version1 --t-test --json-output",
        "evaluate --old-version version1 --filter test-result",
        "evaluate --old-version version1 --filter test-result2" // unknown filter -> null
    };

    static String[] testEvaluateCLIInvalidLines = new String[]{
        "evaluate --old-version version3", // unknown version -> null
    };
    static String[] testInitLines = new String[]{
        "init",
        "init --force"
    };

    static String[] testIndexNewResultLines = new String[]{
        //"index-new-result --path path/to/file" --> missing git for tests, but valid
        "index-new-result --path path/to/file --version version1",
        "index-new-result --path path/to/file --version version1 --tag tag1"
    };
    static String[] testIndexAllResultsLines = new String[] {
        //"index-all-results --path path/to/file" --> missing git for tests, but valid
        "index-all-results --path path/to/file --version version1",
        "index-all-results --path path/to/file --version version1 --tag tag1"
    };
    static String[] testListUndecidedValidLines = new String[] {
        "list-undecided",
        "list-undecided --max-time-on-test 1h",
        "list-undecided --max-time-on-test 1h30m",
        "list-undecided --max-time-on-test 1h30m30s",
        "list-undecided --max-time-on-test 30m30s",
        "list-undecided --max-time-on-test 30x30s",
        "list-undecided --max-time-on-test 30s",
        "list-undecided --old-version version1",
        "list-undecided --old-version version1 --new-version version2",
        "list-undecided --new-version version1 --old-version version2",
        "list-undecided --old-version version1 --filter test-result",
        "list-undecided --old-version version1 --filter test-result2", // unknown filter -> valid line no filter will be used
        "list-undecided --max-time-on-test 30x30x", // not valid duration -> error message
        "list-undecided --max-time-on-test 30x" // not valid duration -> error message
    };

    static String[] testListUndecidedInvalidLines = new String[] {
        "list-undecided --old-version version3", // unknown version -> null
    };

    static String[] assemblyCLIArgs(String CLIExample) throws URISyntaxException {
        String[] splittedCLI = CLIExample.split(" ");
        Path actualDirectory = Paths.get(CommandTest.class.getClassLoader().getResource(".").toURI());
        String[] arg0 = new String[]{actualDirectory.toAbsolutePath().toString()};
        return ArrayUtils.addAll(arg0, splittedCLI);
    }

    Database db;
    Path dbPath = Path.of("test-db");
    Path dirWithTestFiles = Path.of("test-results");
    @BeforeEach
    void setup() throws DatabaseException, SQLException {
        db = H2Database.getDBFromFilePath(dbPath);
        int i = 0;
        for (File f: Objects.requireNonNull(dirWithTestFiles.toFile().listFiles())) {
            ProjectVersion version = new ProjectVersion(
                    Date.from(Instant.now().minus(i, ChronoUnit.DAYS)),
                    "version"+i,
                    Integer.toString(i));
            db.addFile(f.toPath(), version);
            i++;
        }
    }
    @AfterEach
    void cleanup() throws SQLException {
        ((H2Database)db).dropTable();
    }

    @Test
    public void validCLIEvaluateLines() throws URISyntaxException {
        for (String CLILine : testEvaluateCLIValidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void invalidCLIEvaluateLines() throws URISyntaxException {
        for (String CLILine : testEvaluateCLIInvalidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNull(command);
        }
    }

    @Test
    public void validInitLines() throws URISyntaxException {
        for (String CLILine : testInitLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void validIndexNewLines() throws URISyntaxException {
        for (String CLILine : testIndexNewResultLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void validIndexAllLines() throws URISyntaxException {
        for (String CLILine : testIndexAllResultsLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }

    @Test
    public void validUndecidedLines() throws URISyntaxException {
        for (String CLILine : testListUndecidedValidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }

    @Test
    public void invalidUndecidedLines() throws URISyntaxException {
        for (String CLILine : testListUndecidedInvalidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNull(command);
        }
    }

}
