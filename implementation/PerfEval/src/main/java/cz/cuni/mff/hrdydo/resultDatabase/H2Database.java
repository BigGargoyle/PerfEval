package cz.cuni.mff.hrdydo.resultDatabase;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.cuni.mff.hrdydo.ExitCode;
import org.h2.jdbcx.JdbcDataSource;

public class H2Database implements Database {
    private final JdbcDataSource dataSource;
    private final Path pathToDBFile;
    //TODO: this is the path to which are relative paths of inserted files computed to
    private final Path pathRelativesTo=Path.of("");
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";
    private static final String DB_PREFIX = "jdbc:h2:";
    public static Database getDBFromFilePath(Path path) throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_PREFIX+path.toString());
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
    public void dropTable() throws SQLException {
        Connection connection = dataSource.getConnection();
        String createTableQuery = "DROP TABLE IF EXISTS ResultMetadata";
        connection.createStatement().executeUpdate(createTableQuery);
    }

    public H2Database(JdbcDataSource dataSource, Path originPath) {
        this.dataSource = dataSource;
        this.pathToDBFile = originPath;
    }

    @Override
    public FileVersionCharacteristic[] getLastNVersions(int n) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata ORDER BY dateOfCommit DESC LIMIT ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileVersionCharacteristic> versions = new ArrayList<>();

                    while (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        FileVersionCharacteristic version = new FileVersionCharacteristic(commitDate, versionHash, tag);
                        versions.add(version);
                    }

                    return versions.toArray(new FileVersionCharacteristic[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N versions: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileWithResultsData[] getLastNResults(int n) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT path, dateOfCreation, version, tag, dateOfCommit FROM ResultMetadata ORDER BY dateOfCreation ASC LIMIT ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileWithResultsData> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String path =pathToDBFile.resolve(resultSet.getString("path")).toString();
                        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
                        String versionHash = resultSet.getString("version");
                        String tag = resultSet.getString("tag");
                        Date dateOfCommit = resultSet.getTimestamp("dateOfCommit");

                        FileWithResultsData resultData = new FileWithResultsData(path, dateOfCreation, new FileVersionCharacteristic(dateOfCommit, versionHash, tag));
                        results.add(resultData);
                    }

                    return results.toArray(new FileWithResultsData[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N results: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileWithResultsData[] getResultsOfVersion(FileVersionCharacteristic version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {

            if(version.commitVersionHash()==null) throw new DatabaseException("No versionHash provided", null, ExitCode.databaseError);
            String whereClause = generatePatternWhereClause(version);
            String query = "SELECT path, dateOfCreation, tag, dateOfCommit FROM ResultMetadata "+whereClause;

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                int index = 0;
                if(version.commitVersionHash()!=null) preparedStatement.setString(++index, version.commitVersionHash());
                if(version.dateOfCommit()!=null) preparedStatement.setTimestamp(++index, new Timestamp(version.dateOfCommit().getTime()));
                if(version.tag()!=null) preparedStatement.setString(++index, version.tag());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileWithResultsData> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String path = pathToDBFile.resolve(resultSet.getString("path")).toString();
                        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
                        String tag = resultSet.getString("tag");
                        Date dateOfCommit = resultSet.getTimestamp("dateOfCommit");

                        FileWithResultsData resultData = new FileWithResultsData(path, dateOfCreation, new FileVersionCharacteristic(dateOfCommit, version.commitVersionHash(), tag));
                        results.add(resultData);
                    }

                    return results.toArray(new FileWithResultsData[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving results for version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    private String generatePatternWhereClause(FileVersionCharacteristic pattern){
        StringBuilder whereClauseBuilder = new StringBuilder("WHERE ");
        if(pattern.commitVersionHash()!=null) whereClauseBuilder.append("version = ?");
        if(pattern.commitVersionHash()!=null && pattern.dateOfCommit()!=null) whereClauseBuilder.append(" AND ");
        if(pattern.dateOfCommit()!=null) whereClauseBuilder.append("dateOfCommit = ?");
        if(pattern.dateOfCommit()!=null && pattern.tag()!=null) whereClauseBuilder.append(" AND ");
        else if (pattern.commitVersionHash()!=null && pattern.tag()!= null) whereClauseBuilder.append(" AND ");
        if(pattern.tag()!=null) whereClauseBuilder.append("tag = ?");

        return whereClauseBuilder.toString();
    }


    @Override
    public void addFile(Path filePath, FileVersionCharacteristic version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO ResultMetadata (path, dateOfCreation, dateOfCommit, version, tag) VALUES (?, ?, ?, ?, ?)";

            BasicFileAttributes fileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            Date creationDate = new Date(fileAttributes.creationTime().toMillis());

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, pathRelativesTo.relativize(filePath).toString());
                preparedStatement.setTimestamp(2, new Timestamp(creationDate.getTime())); // Current date for dateOfCreation
                preparedStatement.setTimestamp(3, new Timestamp(version.dateOfCommit().getTime()));
                preparedStatement.setString(4, version.commitVersionHash());
                preparedStatement.setString(5, version.tag());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseException("Error adding file to the database: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public void addFilesFromDir(Path dirPath, FileVersionCharacteristic version) throws DatabaseException {
        try {
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
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
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new DatabaseException("Error adding files from directory: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }


    @Override
    public FileVersionCharacteristic findOlderNeighbourVersion(FileVersionCharacteristic version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String innerQuery = "SELECT dateOfCommit FROM ResultMetadata WHERE version = ? ORDER BY dateOfCommit DESC LIMIT 1";
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit < ("+innerQuery+") ORDER BY dateOfCommit DESC";
            //TODO: another select in WHERE clause
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, version.commitVersionHash());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        return new FileVersionCharacteristic(commitDate, versionHash, tag);
                    }
                }
            }
            // If no older neighbor version is found, return null
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding older neighbor version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileVersionCharacteristic findNewerNeighbourVersion(FileVersionCharacteristic version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()){
            String innerQuery = "SELECT dateOfCommit FROM ResultMetadata WHERE version = ? ORDER BY dateOfCommit DESC LIMIT 1";
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit > ("+innerQuery+") ORDER BY dateOfCommit ASC LIMIT 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, version.commitVersionHash());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        return new FileVersionCharacteristic(commitDate, versionHash, tag);
                    }
                }
            }
            // If no newer neighbor version is found, return null
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding newer neighbor version: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }


    @Override
    public FileVersionCharacteristic[] findVersionsOfPattern(FileVersionCharacteristic pattern) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {

            String whereClause = generatePatternWhereClause(pattern);
            String query = "SELECT version, dateOfCommit, tag, dateOfCommit FROM ResultMetadata "+whereClause;

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                int index = 0;
                if(pattern.commitVersionHash()!=null) preparedStatement.setString(++index, pattern.commitVersionHash());
                if(pattern.dateOfCommit()!=null) preparedStatement.setTimestamp(++index, new Timestamp(pattern.dateOfCommit().getTime()));
                if(pattern.tag()!=null) preparedStatement.setString(++index, pattern.tag());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileVersionCharacteristic> results = new ArrayList<>();

                    while (resultSet.next()) {
                        String version = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        FileVersionCharacteristic resultData = new FileVersionCharacteristic(commitDate, version, tag);
                        results.add(resultData);
                    }

                    return results.toArray(new FileVersionCharacteristic[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving results for version pattern: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileVersionCharacteristic[] findVersionsNewerThan(Date date) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT version, dateOfCommit, tag FROM ResultMetadata WHERE dateOfCommit > ? ORDER BY dateOfCommit ASC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<FileVersionCharacteristic> versions = new ArrayList<>();

                    while (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");

                        FileVersionCharacteristic version = new FileVersionCharacteristic(commitDate, versionHash, tag);
                        versions.add(version);
                    }

                    return versions.toArray(new FileVersionCharacteristic[0]);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding versions newer than a date: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }

    @Override
    public FileVersionCharacteristic findClosestVersionToDate(Date date) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()){
            String query = "SELECT *\n" +
                    "FROM (\n" +
                    "    SELECT *\n" +
                    "    FROM ResultMetadata\n" +
                    "    WHERE dateOfCommit < ?\n" +
                    "    ORDER BY dateOfCommit DESC\n" +
                    "    LIMIT 1\n" +
                    ") AS older\n" +
                    "UNION ALL\n" +
                    "SELECT *\n" +
                    "FROM (\n" +
                    "    SELECT *\n" +
                    "    FROM ResultMetadata\n" +
                    "    WHERE dateOfCommit > ?\n" +
                    "    ORDER BY dateOfCommit ASC\n" +
                    "    LIMIT 1\n" +
                    ") AS newer;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
                preparedStatement.setTimestamp(2, new Timestamp(date.getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String versionHash = resultSet.getString("version");
                        Date commitDate = resultSet.getTimestamp("dateOfCommit");
                        String tag = resultSet.getString("tag");
                        if(!resultSet.next())
                            return new FileVersionCharacteristic(commitDate, versionHash, tag);
                        Date newerCommitDate = resultSet.getTimestamp("dateOfCommit");
                        if(Math.abs(newerCommitDate.getTime() - date.getTime()) > Math.abs(commitDate.getTime()-date.getTime()))
                            return new FileVersionCharacteristic(commitDate, versionHash, tag);
                        versionHash = resultSet.getString("version");
                        tag = resultSet.getString("tag");
                        return new FileVersionCharacteristic(commitDate, versionHash, tag);
                    }
                }
            }

            // If no version is found, return null
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error finding closest version to a date: " + e.getMessage(), e, ExitCode.databaseError);
        }
    }
}
