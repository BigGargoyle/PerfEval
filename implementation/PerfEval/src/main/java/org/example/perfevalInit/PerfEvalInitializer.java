package org.example.perfevalInit;

import org.example.GlobalVariables.FileNames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PerfEvalInitializer {
    /**
     * Method that controls PerfEval System
     *
     * @return true - if initialization was successful, false - otherwise
     */
    public static boolean InitPerfEval() {
        try {

            if(!directoryExistsOrCreate(FileNames.perfevalDir))
                return false;

            IniFileData iniFileData = new IniFileData(false);
            boolean success = IniFileData.createNewIniFile(iniFileData);

            BufferedWriter helpFile = new BufferedWriter(new FileWriter(FileNames.workingDirectory+"/"+ FileNames.helpFileName));
            success = success & writeHelpFileContent(helpFile);
            helpFile.close();

            success = success & forcefullyCreateNewFile(FileNames.DatabaseFileName);
            success = success & forcefullyCreateNewFile(FileNames.DatabaseCacheFileName);

            CreateGitIgnoreFile();

            return success;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @param dirName which dir is going to be checked
     * @return if dir was created
     */
    static boolean directoryExistsOrCreate(String dirName) {
        File dir = new File(FileNames.workingDirectory, dirName);
        if (dir.exists())
            return true;
        return dir.mkdirs();
    }

    static boolean forcefullyCreateNewFile(String fileName) {
        File file = new File(FileNames.workingDirectory, fileName);
        try {
            if (file.exists() && !file.delete())
                return false;
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private static void CreateGitIgnoreFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileNames.workingDirectory + "/" + FileNames.gitIgnoreFileName))) {
            writer.write(FileNames.DatabaseFileName.split("/")[1]);
            writer.newLine();
            writer.write(FileNames.DatabaseCacheFileName.split("/")[1]);
            writer.newLine();
            writer.write(FileNames.IniFileName.split("/")[1]);
            writer.newLine();
            writer.write(FileNames.helpFileName.split("/")[1]);
            writer.newLine();
        } catch (IOException e) {
            System.err.println(".gitignore file cannot be created");
        }
    }

    /**
     * @param helpFileWriter where to write content of help file
     * @return true - if writing was successful, false - otherwise
     */
    static boolean writeHelpFileContent(BufferedWriter helpFileWriter) {
        /*
        try {

        } catch (IOException e) {
            return false;
        }
        return true;*/
        return true;
    }
}