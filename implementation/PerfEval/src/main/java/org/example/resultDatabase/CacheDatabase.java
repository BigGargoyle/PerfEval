package org.example.resultDatabase;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.example.globalVariables.ExitCode;
import org.example.measurementFactory.IMeasurementParser;
import org.example.measurementFactory.ParserFactory;
import org.example.perfevalInit.PerfEvalConfig;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidParameterException;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class CacheDatabase implements IDatabase {

    static final int maxCountOfItemsInCache = 5;
    final Path cachePath;
    final Path databasePath;


    public CacheDatabase(Path databasePath, Path cachePath) {
        this.cachePath = cachePath;
        this.databasePath = databasePath;
    }

    private void updateDatabaseCache(PriorityQueue<FileWithResultsData> newCacheContent) throws DatabaseException {
        try {
            FileWriter writer = new FileWriter(cachePath.toFile());
            for (FileWithResultsData item : newCacheContent) {
                String line = item.toDatabaseString() + System.lineSeparator();
                writer.write(line);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            DatabaseException exception = new DatabaseException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
    }


    static private PriorityQueue<FileWithResultsData> createPriorityQueueFromFile(BufferedReader reader, int countOfItemsInQueue) throws IOException {
        PriorityQueue<FileWithResultsData> maxHeap = new PriorityQueue<>(Comparator.comparing(FileWithResultsData::dateOfCreation));
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                FileWithResultsData item = FileWithResultsData.fromDatabaseString(line);
                if (maxHeap.size() <= countOfItemsInQueue)
                    maxHeap.add(item);
                else if (item.dateOfCreation().compareTo(maxHeap.peek().dateOfCreation()) > 0) {
                    maxHeap.poll();
                    maxHeap.add(item);
                }
            } catch (InvalidParameterException e) {
                // in case of the line is not database item
                // it could be a comment
                continue;
            }
        }
        return maxHeap;
    }

    @Override
    public FileWithResultsData[] getLastNResults(int n) throws DatabaseException {
        PriorityQueue<FileWithResultsData> maxHeap;
        try {

            PriorityQueue<FileWithResultsData> cache = LoadCache();
            if (n > cache.size())
                maxHeap = LoadDataFromDatabase(n);
            else {
                maxHeap = cache;
                while (cache.size() > n)
                    cache.poll();
            }
        } catch (IOException e) {
            DatabaseException exception = new DatabaseException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }

        //TODO: what to do with size < n ?

        // if (maxHeap.size() < n) return null;

        FileWithResultsData[] result = new FileWithResultsData[n];
        for (int i = 0; i < result.length; i++) {
            if (maxHeap.size() > 0)
                result[i] = maxHeap.poll();
            else
                break;
        }

        return result;
    }

    private PriorityQueue<FileWithResultsData> LoadDataFromDatabase(int countOfItems) throws IOException {
        BufferedReader reader = Files.newBufferedReader(databasePath);
        return createPriorityQueueFromFile(reader, countOfItems);
    }

    private PriorityQueue<FileWithResultsData> LoadCache() throws IOException {
        BufferedReader reader = Files.newBufferedReader(cachePath);
        return createPriorityQueueFromFile(reader, maxCountOfItemsInCache);
    }

    @Override
    public void addFile(Path filePath, Path gitFilePath, PerfEvalConfig config) throws DatabaseException {
        try {
            PriorityQueue<FileWithResultsData> cache = createPriorityQueueFromFile(Files.newBufferedReader(cachePath), maxCountOfItemsInCache);
            //String version = resolveVersion(filePath, gitFilePath, config.version);
            boolean result = addFileToDatabase(filePath, gitFilePath, config.version, cache);
            updateDatabaseCache(cache);
        } catch (IOException e) {
            DatabaseException exception = new DatabaseException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
    }

    private boolean addFileToDatabase(Path filePath, Path gitFilePath, String configVersion, PriorityQueue<FileWithResultsData> cache) throws IOException {
        if (!filePath.toFile().exists())
            throw new IOException("File does not exist.");

        if (hasUnknownTestFormat(filePath)) {
            throw new IOException("File format is not known.");
        }
        Date creationTime = new Date((Files.readAttributes(filePath, BasicFileAttributes.class)).creationTime().to(TimeUnit.MILLISECONDS));
        String version = resolveVersion(creationTime, gitFilePath, configVersion);
        FileWithResultsData item = FileWithResultsData.createFromFilePath(filePath, version);

        tryAddItemToCache(item, cache);
        return addItemToDatabase(item);
    }

    static boolean hasUnknownTestFormat(Path filePath) {
        //TODO: change to path
        IMeasurementParser parser = ParserFactory.recognizeParserFactory(filePath.toString());
        return parser == null;
    }

    private boolean addItemToDatabase(FileWithResultsData item) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(databasePath);
            writer.append(item.toDatabaseString()).append(System.lineSeparator());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void tryAddItemToCache(FileWithResultsData item, PriorityQueue<FileWithResultsData> cache) {
        if (cache.size() < maxCountOfItemsInCache) {
            cache.add(item);
            return;
        }
        if (cache.peek() == null) return;
        if (item.dateOfCreation().compareTo(cache.peek().dateOfCreation()) > 0) {
            cache.poll();
            cache.add(item);
        }
    }

    @Override
    public void addFilesFromDir(Path dirPath, Path gitFilePath, PerfEvalConfig config) throws DatabaseException {

        try {
            PriorityQueue<FileWithResultsData> cache = createPriorityQueueFromFile(Files.newBufferedReader(cachePath), maxCountOfItemsInCache);
            boolean result = addFilesFromDirDFS(dirPath, gitFilePath, config.version, cache);
            updateDatabaseCache(cache);
        } catch (IOException e) {
            DatabaseException exception = new DatabaseException(ExitCode.databaseError);
            exception.initCause(e);
            throw exception;
        }
    }

    /**
     * @param path path to a file that is meant to be added to database or to a directory inside which there are files (benchmark results) searched
     * @return true - if adding was always successful, false - otherwise
     */
    boolean addFilesFromDirDFS(Path path, Path gitFilePath, String configVersion, PriorityQueue<FileWithResultsData> cache) throws IOException {

        //File file = new File(dirName);

        if (Files.isDirectory(path)) {
            DirectoryStream<Path> files = Files.newDirectoryStream(path);
            boolean result = true;
            for (var fileInDir : files) {
                result = result & addFilesFromDirDFS(fileInDir, gitFilePath, configVersion, cache);
            }
            files.close();
            return result;
        }
        if (Files.isRegularFile(path)) {
            return addFileToDatabase(path, gitFilePath, configVersion, cache);
        }
        return true;
    }

    /**
     * @return version of software to which the new database file belongs to
     */
    static String resolveVersion(Date dateOfCreation, Path gitFilePath, String configVersion) {
        if (gitFilePath == null) {
            return configVersion;
        }
        try (Git git = Git.open(gitFilePath.toFile())) {
            Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
                if (commit.getAuthorIdent().getWhen().before(dateOfCreation)) {
                    return commit.getName();
                }
            }
        } catch (IOException | GitAPIException e) {
            return configVersion;
        }

        return configVersion;
    }

}
