package org.example.ResultDatabase;

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

public class DumbDatabase implements IDatabase{

    static String DatabaseItemIdentifier = "R";
    static String DatabaseColumnSeparator = "\t";
    static String DatabaseFileName = ".performance/test_results.db";
    static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");
    @Override
    public String[] GetLastNResults(int n) {
        PriorityQueue<DatabaseItem> maxHeap = new PriorityQueue<>(Comparator.comparing(DatabaseItem::dateOfCreation));
        try (BufferedReader reader = new BufferedReader(new FileReader(DatabaseFileName))){
            String line;
            while((line=reader.readLine())!=null){
                DatabaseItem item = ParseDatabaseLine(line);
                if(item==null) continue;
                if(maxHeap.size()<=n)
                    maxHeap.add(item);
                else if (item.dateOfCreation.compareTo(maxHeap.peek().dateOfCreation()) < 0) {
                    maxHeap.poll();
                    maxHeap.add(item);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }

        String[] result = new String[n];
        for(int i = 0; i < result.length; i++) {
            if(maxHeap.size()>0)
                result[i] = maxHeap.poll().path;
            else
                break;
        }

        return result;
    }

    @Override
    public boolean AddFile(String fileName) {
        File file = new File(fileName);
        if(!file.exists())
            return false;


        Path path = Paths.get(fileName);
        Date date;
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            date = new Date(attributes.creationTime().toMillis());
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
        String version = ResolveVersion();
        DatabaseItem item = new DatabaseItem(path.toString(), date, version);
        try {
            FileWriter database = new FileWriter(DatabaseFileName);
            database.append(DatabaseItemToString(item));
            database.close();
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean AddFilesFromDir(String dirName) {
        return AddFilesFromDirDFS(new File(dirName));
        // return false;
    }

    boolean AddFilesFromDirDFS(File fileOrDir){

        //File file = new File(dirName);

        if (Files.isDirectory(fileOrDir.toPath())) {
            File[] files = fileOrDir.listFiles();
            if(files == null) return false;
            boolean result = true;
            for (var fileInDir:files) {
                result = result && AddFilesFromDirDFS(fileInDir);
            }
            return result;
        }
        if (Files.isRegularFile(fileOrDir.toPath())) {
            return AddFile(fileOrDir.toPath().toString());
        }
        return true;
    }

    DatabaseItem ParseDatabaseLine(String line){
        String[] splittedLine = line.split("\t");
        if(splittedLine.length>=4 && splittedLine[0].compareTo(DatabaseItemIdentifier)!=0)
            return null;
        Date date;
        try {
            date = DateFormat.parse(splittedLine[2]);
        }
        catch (ParseException e){
            e.printStackTrace();
            return null;
        }
        return new DatabaseItem(splittedLine[1], date, splittedLine[3]);
    }

    String DatabaseItemToString(DatabaseItem databaseItem){
        String result = DatabaseItemIdentifier + DatabaseColumnSeparator;
        result += DateFormat.format(databaseItem.dateOfCreation) + DatabaseColumnSeparator;
        result += databaseItem.version;
        return result;
    }

    String ResolveVersion(){
        // TODO: implementation of git Version
        String version = "1234";

        return version;
    }

    record DatabaseItem(String path, Date dateOfCreation, String version){}
}
