import cz.cuni.mff.d3s.perfeval.command.Command;
import cz.cuni.mff.d3s.perfeval.command.Parser;
import cz.cuni.mff.d3s.perfeval.command.ParserException;
import cz.cuni.mff.d3s.perfeval.command.SetupUtilities;
import cz.cuni.mff.d3s.perfeval.init.PerfEvalCommandFailedException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.DatabaseException;
import cz.cuni.mff.d3s.perfeval.resultdatabase.H2Database;
import cz.cuni.mff.d3s.perfeval.resultdatabase.ProjectVersion;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
// testy mají problém s tím, že si udržují handle na .performance directory, takže se nedaří smazat
// problém s file handlery
public class SimpleCommandExecutionTest {
    static String[] assemblyCLIArgs(String CLIExample) throws URISyntaxException {
        String[] splittedCLI = CLIExample.split(" ");
        Path actualDirectory = Paths.get(CommandTest.class.getClassLoader().getResource(".").toURI());
        String[] arg0 = new String[]{actualDirectory.toAbsolutePath().toString()};
        return ArrayUtils.addAll(arg0, splittedCLI);
    }

    Database db;
    Path dbPath = Path.of("test-db");
    Path dirWithTestFiles = Path.of("test-results");
    @BeforeAll
    static void initPerfEval() throws URISyntaxException, ParserException, PerfEvalCommandFailedException {
        //PerfEval.init(PWD);
        //String inputLine = "init --force --benchmark-parser BenchmarkDotNetJSONParser";
        //String[] args = assemblyCLIArgs(inputLine);
        //Command command = Parser.getCommand(args);
        //assert command != null;
        //command.execute();
    }

    @BeforeEach
    void setup() throws DatabaseException, URISyntaxException {
        //db = H2Database.getDBFromFilePath(dbPath);
        String[] pwd = assemblyCLIArgs("");
        db = SetupUtilities.constructDatabase(Path.of(pwd[0]).resolve(SetupUtilities.PERFEVAL_DIR));
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

    /*@Test
    void EvaluateCommandExecuteTest() throws URISyntaxException, ParserException, PerfEvalCommandFailedException {
        String inputLine = "evaluate";
        String[] args = assemblyCLIArgs(inputLine);
        Command command = Parser.getCommand(args);
        assert command != null;
        command.execute();
    }*/

    /*
    // Unit test controller keeps file handle to .performance directory so this test fails due to cannot delete directory
    @Test
    void InitCommandExecuteTest() throws URISyntaxException, ParserException, PerfEvalCommandFailedException {
        String inputLine = "init --force --benchmark-parser BenchmarkDotNetJSONParser";
        String[] args = assemblyCLIArgs(inputLine);
        Command command = Parser.getCommand(args);
        assert command != null;
        command.execute();
    }
    */
    /*@Test
    void ListUndecidedCommandExecuteTest() throws URISyntaxException, ParserException, PerfEvalCommandFailedException {
        String inputLine = "list-undecided";
        String[] args = assemblyCLIArgs(inputLine);
        Command command = Parser.getCommand(args);
        assert command != null;
        command.execute();
    }*/

}
