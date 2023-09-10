package org.example.ResultDatabase;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.example.GlobalVars;
import org.example.MeasurementFactory.IMeasurementParser;
import org.example.MeasurementFactory.ParserIndustry;
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

    static int maxCountOfItemsInCache = 5;
    static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

    PriorityQueue<DatabaseItem> databaseCache;

    public CacheDatabase() {
        databaseCache = CreatePriorityQueueFromFile(GlobalVars.DatabaseCacheFileName, maxCountOfItemsInCache);
    }

    private void UpdateDatabaseCache(){
        try (FileWriter writer = new FileWriter(GlobalVars.DatabaseCacheFileName)){
            for(DatabaseItem item : databaseCache){
                String line = DatabaseItemToString(item)+System.lineSeparator();
                writer.write(line);
            }
            writer.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    static private PriorityQueue<DatabaseItem> CreatePriorityQueueFromFile(String fileName, int countOfItemsInQueue) {
        PriorityQueue<DatabaseItem> maxHeap = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DatabaseItem item = ParseDatabaseLine(line);
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
    public String[] GetLastNResults(int n) {
        PriorityQueue<DatabaseItem> maxHeap;
        if (n > databaseCache.size())
            maxHeap = CreatePriorityQueueFromFile(GlobalVars.DatabaseFileName, n);
        else {
            maxHeap = GetLastNResultsFromCache(n);
        }

        if(maxHeap == null) return null;

        String[] result = new String[n];
        for (int i = 0; i < result.length; i++) {
            if (maxHeap.size() > 0)
                result[i] = maxHeap.poll().path();
            else
                break;
        }

        return result;
    }

    private PriorityQueue<DatabaseItem> GetLastNResultsFromCache(int n) {
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
    public boolean AddFile(String fileName) {
        boolean result = AddFileToDatabase(fileName);
        if(result) UpdateDatabaseCache();
        return result;
    }

    private boolean AddFileToDatabase(String fileName){
        File file = new File(fileName);
        if (!file.exists())
            return false;

        if(HasUnknownTestFormat(fileName)){
            System.err.println("Unknown file format");
            return false;
        }

        DatabaseItem item = CreateItemFromFileName(fileName);
        if(item == null) return false;

        TryAddItemToCache(item);

        return AddItemToDatabase(item);
    }
    static boolean HasUnknownTestFormat(String filePath){
        IMeasurementParser parser = ParserIndustry.RecognizeParserFactory(filePath);
        return parser == null;
    }

    private boolean AddItemToDatabase(DatabaseItem item){
        try {
            FileWriter database = new FileWriter(GlobalVars.DatabaseFileName, true);
            database.append(DatabaseItemToString(item)).append(System.lineSeparator());
            database.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void TryAddItemToCache(DatabaseItem item) {
        if (databaseCache.size() < maxCountOfItemsInCache) {
            databaseCache.add(item);
            return;
        }
        if(databaseCache.peek()==null) return;
        if (item.dateOfCreation().compareTo(databaseCache.peek().dateOfCreation()) > 0) {
            databaseCache.poll();
            databaseCache.add(item);
        }
    }

    private DatabaseItem CreateItemFromFileName(String fileName){
        Path path = Paths.get(fileName);
        Date date;
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            date = new Date(attributes.creationTime().toMillis());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String version = ResolveVersion(date);
        return new DatabaseItem(path.toString(), date, version);
    }

    @Override
    public boolean AddFilesFromDir(String dirName) {
        boolean result = AddFilesFromDirDFS(new File(dirName));
        // result == false does not mean that no item was added -> ~~~if(result)~~~
        UpdateDatabaseCache();
        return result;
        // return false;
    }

    /**
     * @param fileOrDir path to a file that is meant to be added to database or to a directory inside which there are files (benchmark results) searched
     * @return true - if adding was always successful, false - otherwise
     */
    boolean AddFilesFromDirDFS(File fileOrDir) {

        //File file = new File(dirName);

        if (Files.isDirectory(fileOrDir.toPath())) {
            File[] files = fileOrDir.listFiles();
            if (files == null) return false;
            boolean result = true;
            for (var fileInDir : files) {
                result = result & AddFilesFromDirDFS(fileInDir);
            }
            return result;
        }
        if (Files.isRegularFile(fileOrDir.toPath())) {
            return AddFileToDatabase(fileOrDir.toPath().toString());
        }
        return true;
    }

    /**
     * @param line line from a database file
     * @return an instance of a DatabaseItem which was created from the line
     */
    static DatabaseItem ParseDatabaseLine(String line) {
        String[] splittedLine = line.split(GlobalVars.ColumnDelimiter);
        if (splittedLine.length >= 4 && splittedLine[0].compareTo(GlobalVars.DatabaseItemIdentifier) != 0)
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
    static String DatabaseItemToString(DatabaseItem databaseItem) {
        String result = GlobalVars.DatabaseItemIdentifier + GlobalVars.ColumnDelimiter;
        result += databaseItem.path() + GlobalVars.ColumnDelimiter;
        result += DateFormat.format(databaseItem.dateOfCreation()) + GlobalVars.ColumnDelimiter;
        result += databaseItem.version();
        return result;
    }

    /**
     * @return version of software to which the new database file belongs to
     */
    String ResolveVersion(Date dateOfCreation) {
        IniFileData configData = new IniFileData(true);
        if(!configData.validConfig){
            return GlobalVars.UnknownVersion;
        }
        if(!configData.gitFilePresence){
            return configData.version;
        }

        try(Git git = Git.open(new File(GlobalVars.gitFileDir))){
            Iterable<RevCommit> commits = git.log().all().call();
            for(RevCommit commit : commits){
                if(commit.getCommitterIdent().getWhen().before(dateOfCreation)){
                    return commit.getName();
                }
            }
        }catch (IOException | GitAPIException e){
            return GlobalVars.UnknownVersion;
        }

        return GlobalVars.UnknownVersion;
    }

}
