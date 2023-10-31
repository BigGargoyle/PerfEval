import cz.cuni.mff.hrdydo.resultDatabase.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    Database database;
    Path dbPath = Path.of("test-db");
    Path dirWithTestFiles = Path.of("./test-directory/test-results");
    @BeforeEach
    void setup() throws SQLException, DatabaseException {
        database = H2Database.getDBFromFilePath(dbPath);
        int i = 0;
        for (File f: Objects.requireNonNull(dirWithTestFiles.toFile().listFiles())) {
            FileVersionCharacteristic version = new FileVersionCharacteristic(
                    Date.from(Instant.now().minus(i, ChronoUnit.DAYS)),
                    "version"+i,
                    Integer.toString(i));
            database.addFile(f.toPath(), version);
            i++;
        }
    }
    @AfterEach
    void cleanup() throws SQLException {
        ((H2Database)database).dropTable();
    }

    @Test
    void findOlderVersionTest() throws DatabaseException {
        FileVersionCharacteristic version = new FileVersionCharacteristic(null, "version3", null);
        FileVersionCharacteristic olderVersion = database.findOlderNeighbourVersion(version);
        assertEquals(0, olderVersion.commitVersionHash().compareTo("version4"));
    }

    @Test
    void findOlderVersionNoOlderVersionTest() throws DatabaseException {
        FileVersionCharacteristic version = new FileVersionCharacteristic(null, "version9", null);
        FileVersionCharacteristic olderVersion = database.findOlderNeighbourVersion(version);
        assertNull(olderVersion);
    }

    @Test
    void findNewerVersionTest() throws DatabaseException {
        FileVersionCharacteristic version = new FileVersionCharacteristic(Date.from(Instant.now()), "version3", null);
        FileVersionCharacteristic olderVersion = database.findNewerNeighbourVersion(version);
        assertEquals(0, olderVersion.commitVersionHash().compareTo("version2"));
    }

    @Test
    void findNewerVersionNoNewerVersionTest() throws DatabaseException {
        FileVersionCharacteristic version = new FileVersionCharacteristic(Date.from(Instant.now()), "version0", null);
        FileVersionCharacteristic olderVersion = database.findNewerNeighbourVersion(version);
        assertNull(olderVersion);
    }

    @Test
    void findVersionsOfPattern() throws DatabaseException {
        FileVersionCharacteristic pattern = new FileVersionCharacteristic(null, "version1", null);
        FileVersionCharacteristic[] result = database.findVersionsOfPattern(pattern);
        assertEquals(1, result.length);
        assertEquals(0, result[0].commitVersionHash().compareTo("version1"));
    }

    @Test
    void getLastNVersionsTest() throws DatabaseException {
        int n = 3; // Choose the number of versions you want to retrieve
        FileVersionCharacteristic[] lastNVersions = database.getLastNVersions(n);

        assertNotNull(lastNVersions);
        assertTrue(lastNVersions.length > 0);
        assertTrue(lastNVersions.length <= n);

        // Ensure the versions are in descending order (newest first)
        for (int i = 0; i < lastNVersions.length - 1; i++) {
            assertTrue(lastNVersions[i].dateOfCommit().after(lastNVersions[i + 1].dateOfCommit()));
        }
    }

    @Test
    void getLastNResultsTest() throws DatabaseException {
        int n = 3; // Choose the number of results you want to retrieve
        FileWithResultsData[] lastNResults = database.getLastNResults(n);

        assertNotNull(lastNResults);
        assertTrue(lastNResults.length > 0);
        assertTrue(lastNResults.length <= n);

        // Ensure the results are in descending order (newest first)
        for (int i = 0; i < lastNResults.length - 1; i++) {
            assertTrue(lastNResults[i].version().dateOfCommit().after(lastNResults[i + 1].version().dateOfCommit()));
        }
    }

    @Test
    void getResultsOfVersionTest() throws DatabaseException {
        // Choose a version to retrieve results for (you can use the addedFiles array)
        FileVersionCharacteristic versionToRetrieve = new FileVersionCharacteristic(
                null,
                "version1",
                "1"
        );
        FileWithResultsData[] results = database.getResultsOfVersion(versionToRetrieve);

        assertNotNull(results);
        assertTrue(results.length > 0);

        // Optionally, you can further validate the results as needed
    }

    @Test
    void findVersionsNewerThanTest() throws DatabaseException {
        // Get the date of the first added file during setup
        Date fileDate = Date.from(Instant.now().minus(2, ChronoUnit.DAYS));

        FileVersionCharacteristic[] newerVersions = database.findVersionsNewerThan(fileDate);

        assertTrue(newerVersions.length > 0);

        for (FileVersionCharacteristic version : newerVersions) {
            assertTrue(version.dateOfCommit().after(fileDate));
        }
    }

    @Test
    void findClosestVersionToDateTest() throws DatabaseException {
        // Get the date of the first added file during setup
        Date fileDate = Date.from(Instant.now().minus(2, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));

        FileVersionCharacteristic closestVersion = database.findClosestVersionToDate(fileDate);

        assertNotNull(closestVersion);

        long diff1 = Math.abs(fileDate.getTime() - closestVersion.dateOfCommit().getTime());

        for (FileVersionCharacteristic version : database.getLastNVersions(10)) {
            long diff = Math.abs(fileDate.getTime() - version.dateOfCommit().getTime());
            if (diff < diff1) {
                diff1 = diff;
            }
        }

        assertEquals(Math.abs(fileDate.getTime() - closestVersion.dateOfCommit().getTime()), diff1);
        assertEquals("version2", closestVersion.commitVersionHash());
    }

}
