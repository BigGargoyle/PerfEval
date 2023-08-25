package org.example.ResultDatabase;

public interface IDatabase {
    String[] GetLastNResults(int n);

    boolean AddFile(String fileName);

    boolean AddFilesFromDir(String dirName);
}