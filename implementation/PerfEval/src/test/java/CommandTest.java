import cz.cuni.mff.hrdydo.Command;
import cz.cuni.mff.hrdydo.Parser;
import cz.cuni.mff.hrdydo.resultDatabase.Database;
import cz.cuni.mff.hrdydo.resultDatabase.DatabaseException;
import cz.cuni.mff.hrdydo.resultDatabase.H2Database;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {
    static String PWD = "./test-directory";
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
        //"index-new-result --path path/to/file",
        "index-new-result --path path/to/file --version version1",
        "index-new-result --path path/to/file --version version1 --tag tag1"
    };
    static String[] testIndexAllResultsLines = new String[] {
        //"index-all-results --path path/to/file",
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

    static String[] assemblyCLIArgs(String CLIExample){
        String[] splittedCLI = CLIExample.split(" ");
        String[] arg0 = new String[]{PWD};
        return ArrayUtils.addAll(arg0, splittedCLI);
    }

    Database db;
    @BeforeEach
    void setup() throws DatabaseException {
        db = Parser.constructDatabase(Path.of(PWD).resolve(".performance"));
        db.addFile(Path.of("file1"), "version1", "tag1");
        db.addFile(Path.of("file2"), "version2", "tag2");
    }
    @AfterEach
    void cleanup() throws SQLException {
        ((H2Database)db).dropTable();
    }

    @Test
    public void validCLIEvaluateLines(){
        for (String CLILine : testEvaluateCLIValidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void invalidCLIEvaluateLines(){
        for (String CLILine : testEvaluateCLIInvalidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNull(command);
        }
    }

    @Test
    public void validInitLines(){
        for (String CLILine : testInitLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void validIndexNewLines(){
        for (String CLILine : testIndexNewResultLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }
    @Test
    public void validIndexAllLines(){
        for (String CLILine : testIndexAllResultsLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }

    @Test
    public void validUndecidedLines(){
        for (String CLILine : testListUndecidedValidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            assertNotNull(command);
        }
    }

    @Test
    public void invalidUndecidedLines(){
        for (String CLILine : testListUndecidedInvalidLines) {
            String[] args = assemblyCLIArgs(CLILine);
            Command command = Parser.getCommand(args);
            //TODO: zpracovat max-time-on-test argument
            assertNull(command);
        }
    }

}
