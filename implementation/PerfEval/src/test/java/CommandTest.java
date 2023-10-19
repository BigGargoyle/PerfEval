import cz.cuni.mff.hrdydo.Command;
import cz.cuni.mff.hrdydo.ExitCode;
import cz.cuni.mff.hrdydo.Parser;
import cz.cuni.mff.hrdydo.perfevalInit.PerfEvalCommandFailedException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    };

    static String[] testEvaluateCLIInvalidLines = new String[]{
        "evaluate --old-version version3", // unknown version -> null
        "evaluate --old-version version1 --filter test-result2" // unknown filter -> null
    };
    static String[] testInitLines = new String[]{
        "init",
        "init --force"
    };

    static String[] testIndexNewResultLines = new String[]{
        "index-new-result --path path/to/file",
        "index-new-result --path path/to/file --version version1",
        "index-new-result --path path/to/file --version version1 --tag tag1"
    };
    static String[] testIndexAllResultsLines = new String[] {
        "index-all-result --path path/to/file",
        "index-all-result --path path/to/file --version version1",
        "index-all-result --path path/to/file --version version1 --tag tag1"
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
    };

    static String[] testListUndecidedInvalidLines = new String[] {
        "list-undecided --max-time-on-test 30x30x", // not valid -> null
        "list-undecided --max-time-on-test 30x", // not valid -> null
        "list-undecided --old-version version3", // unknown version -> null
        "list-undecided --old-version version1 --filter test-result2" // unknown filter -> null
    };

    @BeforeEach
    public void setup() throws PerfEvalCommandFailedException {
        Command initCommand = Parser.getCommand(new String[]{PWD,"init","--force"});
        assert initCommand != null;
        ExitCode exitCode = initCommand.execute();
        assertSame(exitCode, ExitCode.OK);
    }

    static String[] assemblyCLIArgs(String CLIExample){
        String[] splittedCLI = CLIExample.split(" ");
        String[] arg0 = new String[]{PWD};
        return ArrayUtils.addAll(arg0, splittedCLI);
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
            assertNull(command);
        }
    }

}
