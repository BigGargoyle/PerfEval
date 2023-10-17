import org.example.perfevalInit.InitCommand;
import org.example.perfevalInit.PerfEvalCommandFailedException;
import org.example.perfevalInit.PerfEvalConfig;
import org.example.perfevalInit.PerfEvalInvalidConfigException;
import org.example.ExitCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class InitCommandTests {

    private Path testDir;
    private Path iniFile;
    private Path gitIgnoreFile;
    private Path helpFile;
    private Path[] emptyFiles;
    private Path[] ignoredFiles;
    private String helpFileContent;
    private PerfEvalConfig defaultConfig;

    @BeforeEach
    public void setUp() throws IOException, PerfEvalInvalidConfigException {
        testDir = Files.createTempDirectory("testDir");
        iniFile = testDir.resolve("test.ini");
        gitIgnoreFile = testDir.resolve(".gitignore");
        helpFile = testDir.resolve("help.txt");
        emptyFiles = new Path[] { testDir.resolve("file1.txt"), testDir.resolve("file2.txt") };
        ignoredFiles = new Path[] { testDir.resolve("ignored1.txt"), testDir.resolve("ignored2.txt") };
        helpFileContent = "This is the help file content.";
        defaultConfig = PerfEvalConfig.getDefaultConfig();
    }

    @Test
    public void testExecuteInitialization() {
        InitCommand initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, defaultConfig, false);
        try {
            ExitCode exitCode = initCommand.execute();
            assertEquals(ExitCode.OK, exitCode);
            assertTrue(Files.exists(iniFile));
            assertTrue(Files.exists(gitIgnoreFile));
            assertTrue(Files.exists(helpFile));
            for (Path emptyFile : emptyFiles) {
                assertTrue(Files.exists(emptyFile));
            }
            for (Path ignoredFile : ignoredFiles) {
                assertTrue(Files.exists(ignoredFile));
            }
        } catch (Exception e) {
            fail("Execution should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testExecuteAlreadyInitialized() throws PerfEvalInvalidConfigException, PerfEvalCommandFailedException {
        // Creating a test.ini file to simulate an already initialized state
        PerfEvalConfig initialConfig = new PerfEvalConfig(false, null, 0, 0, "INITIALIZED");
        InitCommand initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, initialConfig, false);
        initCommand.execute();

        // Attempting to execute initialization again
        initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, defaultConfig, false);
        try {
            ExitCode exitCode = initCommand.execute();
            assertEquals(ExitCode.notInitialized, exitCode);
        } catch (Exception e) {
            fail("Execution should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testExecuteForceInitialization() throws PerfEvalInvalidConfigException, PerfEvalCommandFailedException {
        // Creating a test.ini file to simulate an already initialized state
        PerfEvalConfig initialConfig = new PerfEvalConfig(false, null, 0, 0, "INITIALIZED");
        InitCommand initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, initialConfig, false);
        initCommand.execute();

        // Attempting to execute force initialization
        initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, defaultConfig, true);
        try {
            ExitCode exitCode = initCommand.execute();
            assertEquals(ExitCode.OK, exitCode);
        } catch (Exception e) {
            fail("Execution should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetConfig() throws PerfEvalInvalidConfigException {
        InitCommand initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, defaultConfig, false);
        PerfEvalConfig config = InitCommand.getConfig(iniFile);
        assertEquals(defaultConfig, config);
    }

    @Test
    public void testGetConfigIniFileDoesNotExist() {
        InitCommand initCommand = new InitCommand(testDir, gitIgnoreFile, iniFile, helpFile, helpFileContent, emptyFiles, ignoredFiles, defaultConfig, false);
        iniFile = testDir.resolve("nonexistent.ini");
        assertThrows(PerfEvalInvalidConfigException.class, () -> InitCommand.getConfig(iniFile));
    }
}
