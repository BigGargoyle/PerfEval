package org.example.ResultDatabase;


import org.example.GlobalVariables.DBFlags;
import org.example.GlobalVariables.FileNames;
import org.example.GlobalVariables.StringConstants;
import org.example.MeasurementFactory.IMeasurementParser;
import org.example.MeasurementFactory.ParserFactory;
import org.example.perfevalInit.IniFileData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

public class CacheDatabase implements IDatabase {

    static final int maxCountOfItemsInCache = 5;
    static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

    final PriorityQueue<DatabaseItem> databaseCache;

    public CacheDatabase() {
        databaseCache = createPriorityQueueFromFile(FileNames.workingDirectory+"/"+ FileNames.DatabaseCacheFileName, maxCountOfItemsInCache);
    }

    private void updateDatabaseCache() {
        try (FileWriter writer = new FileWriter(FileNames.workingDirectory+"/"+ FileNames.DatabaseCacheFileName)) {
            for (DatabaseItem item : databaseCache) {
                String line = databaseItemToString(item) + System.lineSeparator();
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private PriorityQueue<DatabaseItem> createPriorityQueueFromFile(String fileName, int countOfItemsInQueue) {
        PriorityQueue<DatabaseItem> maxHeap = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DatabaseItem item = parseDatabaseLine(line);
                if (item == null) continue;
                if (maxHeap.size() <= countOfItemsInQueue)
                    maxHeap.add(item);
                else if (item.dateOfCreation().compareTo(maxHeap.peek().dateOfCreation()) > 0) {
                    maxHeap.poll();
                    maxHeap.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return maxHeap;
    }

    @Override
    public String[] getLastNResults(int n) {
        PriorityQueue<DatabaseItem> maxHeap;
        if (n > databaseCache.size())
            maxHeap = createPriorityQueueFromFile(FileNames.workingDirectory+"/"+ FileNames.DatabaseFileName, n);
        else {
            maxHeap = getLastNResultsFromCache(n);
        }

        if (maxHeap == null || maxHeap.size() < n) return null;

        String[] result = new String[n];
        for (int i = 0; i < result.length; i++) {
            if (maxHeap.size() > 0)
                result[i] = maxHeap.poll().path();
            else
                break;
        }

        return result;
    }

    private PriorityQueue<DatabaseItem> getLastNResultsFromCache(int n) {
        PriorityQueue<DatabaseItem> result = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        DatabaseItem[] lastItems = new DatabaseItem[n];
        for (int i = 0; i < lastItems.length; i++) {
            lastItems[i] = databaseCache.poll();
        }
        for (DatabaseItem lastItem : lastItems) {
            databaseCache.add(lastItem);
            result.add(lastItem);
        }
        return result;
    }

    @Override
    public boolean addFile(String fileName) {
        boolean result = addFileToDatabase(fileName);
        if (result) updateDatabaseCache();
        return result;
    }

    private boolean addFileToDatabase(String fileName) {
        File file = new File(fileName);
        if (!file.exists())
            return false;

        if (hasUnknownTestFormat(fileName)) {
            System.err.println("Unknown file format");
            return false;
        }
        DatabaseItem item = createItemFromFileName(fileName);
        if (item == null) return false;

        tryAddItemToCache(item);
        return addItemToDatabase(item);
    }

    static boolean hasUnknownTestFormat(String filePath) {
        IMeasurementParser parser = ParserFactory.recognizeParserFactory(filePath);
        return parser == null;
    }

    private boolean addItemToDatabase(DatabaseItem item) {
        try {
            FileWriter database = new FileWriter(FileNames.workingDirectory+"/"+ FileNames.DatabaseFileName, true);
            database.append(databaseItemToString(item)).append(System.lineSeparator());
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void tryAddItemToCache(DatabaseItem item) {
        if (databaseCache.size() < maxCountOfItemsInCache) {
            databaseCache.add(item);
            return;
        }
        if (databaseCache.peek() == null) return;
        if (item.dateOfCreation().compareTo(databaseCache.peek().dateOfCreation()) > 0) {
            databaseCache.poll();
            databaseCache.add(item);
        }
    }

    private DatabaseItem createItemFromFileName(String fileName) {
        Path path = Paths.get(fileName);
        Date date;
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            date = new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
        String version = resolveVersion(date);
        return new DatabaseItem(path.toAbsolutePath().toString(), date, version);
    }

    @Override
    public boolean addFilesFromDir(String dirName) {
        boolean result = addFilesFromDirDFS(new File(dirName));
        // result == false does not mean that no item was added -> ~~~if(result)~~~
        updateDatabaseCache();
        return result;
        // return false;
    }

    /**
     * @param fileOrDir path to a file that is meant to be added to database or to a directory inside which there are files (benchmark results) searched
     * @return true - if adding was always successful, false - otherwise
     */
    boolean addFilesFromDirDFS(File fileOrDir) {

        //File file = new File(dirName);

        if (Files.isDirectory(fileOrDir.toPath())) {
            File[] files = fileOrDir.listFiles();
            if (files == null) return false;
            boolean result = true;
            for (var fileInDir : files) {
                result = result & addFilesFromDirDFS(fileInDir);
            }
            return result;
        }
        if (Files.isRegularFile(fileOrDir.toPath())) {
            return addFileToDatabase(fileOrDir.toPath().toAbsolutePath().toString());
        }
        return true;
    }

    /**
     * @param line line from a database file
     * @return an instance of a DatabaseItem which was created from the line
     */
    static DatabaseItem parseDatabaseLine(String line) {
        String[] splittedLine = line.split(DBFlags.ColumnDelimiter);
        if (splittedLine.length >= 4 && splittedLine[0].compareTo(DBFlags.DatabaseItemIdentifier) != 0)
            return null;
        Date date;
        try {
            date = DateFormat.parse(splittedLine[2]);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return new DatabaseItem(splittedLine[1], date, splittedLine[3]);
    }

    /**
     * @param databaseItem creating a database file line from an instance of DatabaseItem
     * @return database file line
     */
    static String databaseItemToString(DatabaseItem databaseItem) {
        String result = DBFlags.DatabaseItemIdentifier + DBFlags.ColumnDelimiter;
        result += databaseItem.path() + DBFlags.ColumnDelimiter;
        result += DateFormat.format(databaseItem.dateOfCreation()) + DBFlags.ColumnDelimiter;
        result += databaseItem.version();
        return result;
    }

    /**
     * @return version of software to which the new database file belongs to
     */
    String resolveVersion(Date dateOfCreation) {
        IniFileData configData = new IniFileData(true);
        if (!configData.validConfig) {
            return StringConstants.UnknownVersion;
        }
        if (!configData.gitFilePresence) {
            return configData.version;
        }
        /*try (Git git = Git.open(new File(GlobalVars.gitFileDir))) {
            Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
                if (commit.getCommitterIdent().getWhen().before(dateOfCreation)) {
                    return commit.getName();
                }
            }
        } catch (IOException | GitAPIException e) {
            return GlobalVars.UnknownVersion;
        }*/

        return StringConstants.UnknownVersion;
    }

}
