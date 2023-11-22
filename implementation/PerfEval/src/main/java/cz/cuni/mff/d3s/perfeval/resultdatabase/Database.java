package cz.cuni.mff.d3s.perfeval.resultdatabase;

import java.nio.file.Path;
import java.util.Date;

/**
 * Interface defining behaviour of the benchmark test results database
 */
public interface Database {
    /**
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     */
    ProjectVersion[] getLastNVersions(int n) throws DatabaseException;

    /**
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     * @throws DatabaseException if there is a problem with the database
     */
    FileWithResultsData[] getLastNResults(int n) throws DatabaseException;

    /**
     * @param version version of the project
     * @return all benchmark test results file paths of the given version
     * @throws DatabaseException if there is a problem with the database
     */
    FileWithResultsData[] getResultsOfVersion(ProjectVersion version) throws DatabaseException;

    /**
     * Method for adding a benchmark test results file to the database.
     *
     * @param filePath path to the benchmark test results file
     * @param version  version of the project that the file belongs to
     * @throws DatabaseException if there is a problem with the database
     */
    void addFile(Path filePath, ProjectVersion version) throws DatabaseException;

    /**
     * Method for adding all benchmark test results files from a directory to the database.
     *
     * @param dirPath path to the directory
     * @param version version of the project that the files belong to
     * @throws DatabaseException if there is a problem with the database
     */
    void addFilesFromDir(Path dirPath, ProjectVersion version) throws DatabaseException;

    /**
     * Method for finding the version of the project that is the closest older than the given date.
     *
     * @param version version of the project with the date
     * @return version of the project that is the closest older than the given date
     * @throws DatabaseException if there is a problem with the database
     */
    ProjectVersion findOlderNeighbourVersion(ProjectVersion version) throws DatabaseException;

    /**
     * Method for finding the version of the project that is the closest newer than the given date.
     *
     * @param version version of the project with the date
     * @return version of the project that is the closest newer than the given date
     * @throws DatabaseException if there is a problem with the database
     */
    ProjectVersion findNewerNeighbourVersion(ProjectVersion version) throws DatabaseException;

    /**
     * Method for finding all versions of the project that match the given pattern.
     *
     * @param pattern pattern of the project version
     * @return all versions of the project that match the given pattern
     * @throws DatabaseException if there is a problem with the database
     */
    ProjectVersion[] findVersionsOfPattern(ProjectVersion pattern) throws DatabaseException;

    /**
     * Method for finding all versions of the project that are older than the given date.
     *
     * @param date date
     * @return all versions of the project that are older than the given date
     * @throws DatabaseException
     */
    ProjectVersion[] findVersionsNewerThan(Date date) throws DatabaseException;

    /**
     * Method for finding all versions of the project that are newer than the given date.
     *
     * @param date date
     * @return all versions of the project that are newer than the given date
     * @throws DatabaseException if there is a problem with the database
     */
    ProjectVersion findClosestVersionToDate(Date date) throws DatabaseException;
}