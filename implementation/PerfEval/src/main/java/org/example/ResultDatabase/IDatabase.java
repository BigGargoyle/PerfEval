package org.example.ResultDatabase;

/**
 * Interface defining behaviour of the benchmark test results database
 */
public interface IDatabase {
    /**
     *
     * @param n how many results should be returned
     * @return n most recently added benchmark test results file paths from the database
     */
    String[] getLastNResults(int n);

    /**
     *
     * @param fileName name/path to a file that has to added to the database
     * @return true - if addition was successful, false - otherwise
     */
    boolean addFile(String fileName);

    /**
     *
     * @param dirName name/path to a dir that benchmark results have to be added to the database from
     * @return true - if all additions was successful, false - otherwise
     */
    boolean addFilesFromDir(String dirName);
}