import org.example.resultDatabase.Database;
import org.example.resultDatabase.DatabaseException;
import org.example.resultDatabase.FileWithResultsData;
import org.example.resultDatabase.H2Database;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class H2DatabaseTest {
    private JdbcDataSource dataSource;

    @BeforeEach
    public void setup() throws SQLException {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");

        try (Connection connection = dataSource.getConnection()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS ResultMetadata ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "path VARCHAR(255), "
                    + "dateOfCreation TIMESTAMP, "
                    + "version VARCHAR(255), "
                    + "tag VARCHAR(255))";
            connection.createStatement().executeUpdate(createTableQuery);
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS ResultMetadata");
        }
    }

    @Test
    public void testAddFile() throws DatabaseException{
        Database database = new H2Database(dataSource, Path.of("."));
        database.addFile(Path.of("file1"), "version1", "tag1");
        database.addFile(Path.of("file2"), "version2", "tag2");

        String[] versions = database.getLastNVersions(2);
        assertNotNull(versions);
        assertEquals(2, versions.length);

        FileWithResultsData[] fileMetadata = database.getLastNResults(2);
        assertNotNull(fileMetadata);
        assertEquals(2, fileMetadata.length);

        FileWithResultsData[] fileOfVersion = database.getResultsOfVersion(versions[0]);
        assertNotNull(fileOfVersion);
        assertEquals(1, fileOfVersion.length);
    }

}
