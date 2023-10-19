package cz.cuni.mff.hrdydo.resultDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import cz.cuni.mff.hrdydo.ExitCode;
import org.h2.jdbcx.JdbcDataSource;
public class H2Database implements Database {
    private final JdbcDataSource dataSource;
    private final Path pathToDBFile;
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";
    private static final String DB_PREFIX = "jdbc:h2:";
    public static Database getDBFromFilePath(Path path){
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_PREFIX+path.toString());
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        return new H2Database(dataSource, path);
    }

    public H2Database(JdbcDataSource dataSource, Path originPath) {
        this.dataSource = dataSource;
        this.pathToDBFile = originPath;
    }
    @Override
    public String[] getLastNVersions(int n) throws DatabaseException {
        List<String> versions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT DISTINCT version, dateOfCreation FROM ResultMetadata ORDER BY dateOfCreation DESC LIMIT ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    versions.add(resultSet.getString("version"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N versions from the database", e, ExitCode.databaseError);
        }

        return versions.toArray(new String[0]);
    }

    @Override
    public FileWithResultsData[] getLastNResults(int n) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM ResultMetadata ORDER BY dateOfCreation DESC LIMIT ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, n);

                ResultSet resultSet = preparedStatement.executeQuery();
                FileWithResultsData[] results = new FileWithResultsData[n];
                int i = 0;
                while (resultSet.next()) {
                    results[i] = createFileWithResultsData(resultSet);
                    i++;
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving last N results from the database", e, ExitCode.databaseError);
        }
    }

    @Override
    public FileWithResultsData[] getResultsOfVersion(String version) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM ResultMetadata WHERE version = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, version);

                ResultSet resultSet = preparedStatement.executeQuery();
                List<FileWithResultsData> resultsData = new ArrayList<>();
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    resultsData.add(createFileWithResultsData(resultSet));
                }
                return resultsData.toArray(new FileWithResultsData[0]);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving results of a specific version from the database", e, ExitCode.databaseError);
        }
    }

    @Override
    public void addFile(Path filePath, String version, String tag) throws DatabaseException {
        try (Connection connection = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO ResultMetadata (path, dateOfCreation, version, tag) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                Path relativePath = pathToDBFile.relativize(filePath);
                preparedStatement.setString(1, relativePath.toString());
                preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setString(3, version);
                preparedStatement.setString(4, tag);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error adding a single file to the database", e, ExitCode.databaseError);
        }
    }

    @Override
    public void addFilesFromDir(Path dirPath, String version, String tag) throws DatabaseException {
        try {
            Stream<Path> stream= Files.walk(dirPath).filter(Files::isRegularFile);
            stream.forEach(file -> {
                try {
                    addFile(file, version, tag);
                } catch (DatabaseException e) {
                    throw new RuntimeException(e);
                }
            });
            stream.close();
        } catch (IOException e) {
            throw new DatabaseException("Error adding files from directory to the database", e, ExitCode.databaseError);
        }
    }

    private FileWithResultsData createFileWithResultsData(ResultSet resultSet) throws SQLException {
        //int id = resultSet.getInt("id");
        String path = resultSet.getString("path");
        Date dateOfCreation = resultSet.getTimestamp("dateOfCreation");
        String version = resultSet.getString("version");
        //String tag = resultSet.getString("tag");
        return new FileWithResultsData(path, dateOfCreation, version);
    }
}
