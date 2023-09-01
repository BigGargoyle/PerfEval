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
    String[] GetLastNResults(int n);

    /**
     *
     * @param fileName name/path to a file that has to added to the database
     * @return true - if addition was successful, false - otherwise
     */
    boolean AddFile(String fileName);

    /**
     *
     * @param dirName name/path to a dir that benchmark results have to be added to the database from
     * @return true - if all additions was successful, false - otherwise
     */
    boolean AddFilesFromDir(String dirName);
}