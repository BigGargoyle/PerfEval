package cz.cuni.mff.d3s.perfeval.resultdatabase;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cuni.mff.d3s.perfeval.ExitCode;
import org.h2.jdbcx.JdbcDataSource;

/**
 * Class representing a database of the benchmark test results.
 * The database is implemented using H2 database and JDBC.
 */
public class H2Database implements Database {
    /**
     * JDBCDataSource that represents metadata about database connection.
     */
    private final JdbcDataSource dataSource;
    /**
     * Path to the database file.
     */
    private final Path pathToDBFile;
    //this is the path to which are relative paths of inserted files computed to
    //private final Path pathRelativesTo=Path.of("");
    /**
     * Username for accessing the database.
     */
    private static final String DB_USER = "sa";
    /**
     * Password for accessing the database.
     */
    private static final String DB_PASSWORD = "sa";
    /**
     * Prefix of the database URL.
     */
    private static final String DB_PREFIX = "jdbc:h2:";

    /**
     * Creates new PerfEval database with the given path.
     * @param path              path to the database file
     * @return                  instance of Database with usage of H2 database and JDBC
     * @throws SQLException     SQLException could be thrown if file cannot be created or there is another error with access to H2 database
     */
    public static Database getDBFromFilePath(Path path) throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_PREFIX + path.toString());
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        Connection connection = dataSource.getConnection();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS ResultMetadata ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "path VARCHAR(255), "
                + "dateOfCreation TIMESTAMP, "
                + "dateOfCommit TIMESTAMP, "
                + "version VARCHAR(255), "
                + "tag VARCHAR(255))";
        connection.createStatement().executeUpdate(createTableQuery);

        return new H2Database(dataSource, path);
    }

    /**
     * method used just for testing, drops database table.
     * @throws SQLException throws SQLException if table cannot be dropped
     */
    public void dropTable() throws SQLException {
        Connection connection = dataSource.getConnection();
        String createTableQuery = "DROP TABLE IF EXISTS ResultMetadata";
        connection.createStatement().executeUpdate(createTableQuery);
    }

    /**
     * Initializes private members.
     * @param dataSource    JDBCDataSource that represents metadata about database connection
     * @param originPath    path to db file, used for adding relative paths to that file to database
     */
    private H2Database(JdbcDataSource dataSource, Path originPath) {
        this.dataSource = dataSource;
        this.pathToDBFile = originPath;
    }

    /**
     *
     * @param n                     how many results should be returned
     * @return                      array of last n FileVersionCharacteristic that the database knows
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion[] getLastNVersions(int n) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            //db query for getting info about newest N versions
            String query = "SELECT DISTINCT version, dateOfCommit, tag FROM ResultMetadata ORDER BY dateOfCommit DESC LIMIT ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<ProjectVersion> versions = new ArrayList<>();

                    while (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        ProjectVersion version = new ProjectVersion(commitDate, versionHash, tag);
                        versions.add(version);
                    }

                    return versions.toArray(new ProjectVersion[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N versions: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param n                     how many results should be returned
     * @return                      the n newest (by their dateOfCreation) files in database
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public FileWithResultsData[] getLastNResults(int n) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT path, dateOfCreation, version, tag, dateOfCommit FROM ResultMetadata ORDER BY dateOfCreation ASC LIMIT ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileWithResultsData> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String path = pathToDBFile.resolve(resultSet.getString("path")).toAbsolutePath().toString();
                        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
                        String versionHash = resultSet.getString("version");
                        String tag = resultSet.getString("tag");
                        Date dateOfCommit = resultSet.getTimestamp("dateOfCommit");

                        FileWithResultsData resultData = new FileWithResultsData(path, dateOfCreation, new ProjectVersion(dateOfCommit, versionHash, tag));
                        results.add(resultData);
                    }

                    return results.toArray(new FileWithResultsData[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N results: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param version               files from this version are wanted
     * @return                      all files of the specified version (commitHash needed in this case)
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public FileWithResultsData[] getResultsOfVersion(ProjectVersion version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {

            if (version.commitVersionHash() == null) {
                throw new DatabaseException("No versionHash provided", null, ExitCode.databaseError);
            }
            String whereClause = generatePatternWhereClause(version);
            String query = "SELECT path, dateOfCreation, tag, dateOfCommit FROM ResultMetadata " + whereClause;
            try (PreparedStatement preparedStatement = generateStatementFromVersionPatternQuery(connection, query, version)) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileWithResultsData> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String path = pathToDBFile.resolve(resultSet.getString("path")).toAbsolutePath().normalize().toString();
                        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
                        String tag = resultSet.getString("tag");
                        Date dateOfCommit = resultSet.getTimestamp("dateOfCommit");

                        FileWithResultsData resultData = new FileWithResultsData(path, dateOfCreation, new ProjectVersion(dateOfCommit, version.commitVersionHash(), tag));
                        results.add(resultData);
                    }

                    return results.toArray(new FileWithResultsData[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving results for version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param pattern   non-null values of this FileVersionCharacteristic instance are wanted
     * @return          String representation of SQL WHERE clause
     */
    private String generatePatternWhereClause(ProjectVersion pattern) {
        StringBuilder whereClauseBuilder = new StringBuilder("WHERE ");
        if (pattern.commitVersionHash() != null) {
            whereClauseBuilder.append("version = ?");
        }
        if (pattern.commitVersionHash() != null && pattern.dateOfCommit() != null) {
            whereClauseBuilder.append(" AND ");
        }
        if (pattern.dateOfCommit() != null) {
            whereClauseBuilder.append("dateOfCommit = ?");
        }
        if (pattern.dateOfCommit() != null && pattern.tag() != null) {
            whereClauseBuilder.append(" AND ");
        } else if (pattern.commitVersionHash() != null && pattern.tag() != null) {
            whereClauseBuilder.append(" AND ");
        }
        if (pattern.tag() != null) {
            //tag is case-insensitive
            whereClauseBuilder.append("tag ~* ?");
        }
        return whereClauseBuilder.toString();
    }

    /**
     *
     * @param connection        connection to PerfEval database
     * @param query             String representation of database query to be prepared
     * @param pattern           pattern from which WHERE clause was generated
     * @return                  SQL PreparedStatement ready for execution
     * @throws SQLException     thrown if preparing PreparedStatement fails
     */
    private PreparedStatement generateStatementFromVersionPatternQuery(Connection connection, String query, ProjectVersion pattern) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        int index = 0;
        if (pattern.commitVersionHash() != null) {
            preparedStatement.setString(++index, pattern.commitVersionHash());
        }
        if (pattern.dateOfCommit() != null) {
            preparedStatement.setTimestamp(++index, new Timestamp(pattern.dateOfCommit().getTime()));
        }
        if (pattern.tag() != null) {
            preparedStatement.setString(++index, pattern.tag());
        }
        return preparedStatement;
    }

    /**
     *
     * @param filePath              path to file that is meant to be added to database
     * @param version               version of measured values of file that is meant to be added
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public void addFile(Path filePath, ProjectVersion version) throws DatabaseException {
        if (!Files.isRegularFile(filePath)) {
            throw new DatabaseException("Path is not a file", ExitCode.databaseError);
        }
        try (Connection connection = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO ResultMetadata (path, dateOfCreation, dateOfCommit, version, tag) VALUES (?, ?, ?, ?, ?)";

            BasicFileAttributes fileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            Date creationDate = new Date(fileAttributes.creationTime().toMillis());

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, pathToDBFile.relativize(filePath.toAbsolutePath()).toString());
                preparedStatement.setTimestamp(2, new Timestamp(creationDate.getTime())); // Current date for dateOfCreation
                preparedStatement.setTimestamp(3, new Timestamp(version.dateOfCommit().getTime()));
                preparedStatement.setString(4, version.commitVersionHash());
                preparedStatement.setString(5, version.tag());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding file to the database: " + e.getMessage(), ExitCode.databaseError);
        } catch (IOException e) {
            throw new DatabaseException("File does not exists: " + filePath.toString(), ExitCode.databaseError);
        }
    }

    /**
     *
     * @param dirPath               path to dir that its files are meant to be added to database
     * @param version               version of measured values of files that is meant to be added
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public void addFilesFromDir(Path dirPath, ProjectVersion version) throws DatabaseException {
        if (!Files.isDirectory(dirPath)) {
            throw new DatabaseException("Path is not a directory", ExitCode.databaseError);
        }
        try {
            Files.walkFileTree(dirPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        addFile(file, version);
                    } catch (DatabaseException e) {
                        throw new IOException("Error adding file to the database", e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new DatabaseException("Error adding files from directory: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param version               version of which older neighbour is wanted
     * @return                      the youngest older neighbour of version
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion findOlderNeighbourVersion(ProjectVersion version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String innerQuery = "SELECT dateOfCommit FROM ResultMetadata WHERE version = ? ORDER BY dateOfCommit DESC LIMIT 1";
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit < (" + innerQuery + ") ORDER BY dateOfCommit DESC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, version.commitVersionHash());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        return new ProjectVersion(commitDate, versionHash, tag);
                    }
                }
            }
            // If no older neighbor version is found, return null
            throw new DatabaseException("No older neighbor version found of version "+version.commitVersionHash(), ExitCode.databaseError);
        } catch (SQLException e) {
            throw new DatabaseException("Error finding older neighbor version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param version               version of which younger neighbour is wanted
     * @return                      the oldest younger neighbour of version
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion findNewerNeighbourVersion(ProjectVersion version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String innerQuery = "SELECT dateOfCommit FROM ResultMetadata WHERE version = ? ORDER BY dateOfCommit DESC LIMIT 1";
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit > (" + innerQuery + ") ORDER BY dateOfCommit ASC LIMIT 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, version.commitVersionHash());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        return new ProjectVersion(commitDate, versionHash, tag);
                    }
                }
            }
            // If no newer neighbor version is found, return null
            throw new DatabaseException("No newer neighbor version found of version "+version.commitVersionHash(), ExitCode.databaseError);
        } catch (SQLException e) {
            throw new DatabaseException("Error finding newer neighbor version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param pattern               non-null members are declaring how does the wanted versions looks like
     * @return                      versions that are matching the pattern
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion[] findVersionsOfPattern(ProjectVersion pattern) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {

            String whereClause = generatePatternWhereClause(pattern);
            String query = "SELECT version, dateOfCommit, tag, dateOfCommit FROM ResultMetadata " + whereClause;

            try (PreparedStatement preparedStatement = generateStatementFromVersionPatternQuery(connection, query, pattern)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<ProjectVersion> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String version = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        ProjectVersion resultData = new ProjectVersion(commitDate, version, tag);
                        results.add(resultData);
                    }

                    return results.toArray(new ProjectVersion[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving results for version pattern: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param date                  date from which are versions finding
     * @return                      versions that are newer than date (their dateOfCommit is higher -> timestamp)
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion[] findVersionsNewerThan(Date date) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit > ? ORDER BY dateOfCommit ASC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<ProjectVersion> versions = new ArrayList<>();

                    while (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        ProjectVersion version = new ProjectVersion(commitDate, versionHash, tag);
                        versions.add(version);
                    }

                    return versions.toArray(new ProjectVersion[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding versions newer than a date: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    /**
     *
     * @param date                  date from which are versions finding
     * @return                      the closest (by time distance) version to the date
     * @throws DatabaseException    if query is not valid, or there is another SQL error
     */
    @Override
    public ProjectVersion findClosestVersionToDate(Date date) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            //query for first newer and first older version -> then will be searched the closer one from both
            String query = """
                    SELECT *
                    FROM (
                        SELECT *
                        FROM ResultMetadata
                        WHERE dateOfCommit < ?
                        ORDER BY dateOfCommit DESC
                        LIMIT 1
                    ) AS older
                    UNION ALL
                    SELECT *
                    FROM (
                        SELECT *
                        FROM ResultMetadata
                        WHERE dateOfCommit > ?
                        ORDER BY dateOfCommit ASC
                        LIMIT 1
                    ) AS newer;""";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");
                        if (!resultSet.next()) {
                            return new ProjectVersion(commitDate, versionHash, tag);
                        }
                        Date newerCommitDate = resultSet.getTimestamp("dateOfCommit");
                        if (Math.abs(newerCommitDate.getTime() - date.getTime()) > Math.abs(commitDate.getTime() - date.getTime())) {
                            return new ProjectVersion(commitDate, versionHash, tag);
                        }
                        versionHash = resultSet.getString("version");
                        tag = resultSet.getString("tag");
                        return new ProjectVersion(commitDate, versionHash, tag);
                    }
                }
            }

            // If no version is found, return null
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding closest version to a date: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileWithResultsData[] getAllResults() throws DatabaseException{
        try(Connection connection = dataSource.getConnection()){
            String query = "SELECT path, dateOfCreation, version, tag, dateOfCommit FROM ResultMetadata";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    List<FileWithResultsData> results = new ArrayList<>();
                    while(resultSet.next()){
                        String path = pathToDBFile.resolve(resultSet.getString("path")).toAbsolutePath().toString();
                        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
                        String versionHash = resultSet.getString("version");
                        String tag = resultSet.getString("tag");
                        Date dateOfCommit = resultSet.getTimestamp("dateOfCommit");

                        FileWithResultsData resultData = new FileWithResultsData(path, dateOfCreation, new ProjectVersion(dateOfCommit, versionHash, tag));
                        results.add(resultData);
                    }
                    return results.toArray(new FileWithResultsData[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all results: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public Date getDateOfVersionHash(String versionHash) throws DatabaseException {
        try(Connection connection = dataSource.getConnection()){
            String query = "SELECT dateOfCommit FROM ResultMetadata WHERE version = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, versionHash);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if (resultSet.next()){
                        return resultSet.getTimestamp(1);
                    }
                    throw new DatabaseException("Error unknown version.", ExitCode.OK);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all results: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }
}
