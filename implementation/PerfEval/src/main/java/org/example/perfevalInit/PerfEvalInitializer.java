package org.example.perfevalInit;

import org.example.GlobalVars;

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

            directoryExistsOrCreate(GlobalVars.perfevalDir);

            IniFileData iniFileData = new IniFileData(false);
            boolean success = IniFileData.createNewIniFile(iniFileData);

            BufferedWriter helpFile = new BufferedWriter(new FileWriter(GlobalVars.workingDirectory+"/"+GlobalVars.helpFileName));
            success = success & writeHelpFileContent(helpFile);
            helpFile.close();

            success = success & forcefullyCreateNewFile(GlobalVars.DatabaseFileName);
            success = success & forcefullyCreateNewFile(GlobalVars.DatabaseCacheFileName);

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
        File dir = new File(GlobalVars.workingDirectory, dirName);
        if (dir.exists())
            return false;
        return dir.mkdirs();
    }

    static boolean forcefullyCreateNewFile(String fileName) {
        File file = new File(GlobalVars.workingDirectory, fileName);
        try {
            if (file.exists() && !file.delete())
                return false;
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private static void CreateGitIgnoreFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GlobalVars.workingDirectory + "/" + GlobalVars.gitIgnoreFileName))) {
            writer.write(GlobalVars.DatabaseFileName.split("/")[1]);
            writer.newLine();
            writer.write(GlobalVars.DatabaseCacheFileName.split("/")[1]);
            writer.newLine();
            writer.write(GlobalVars.IniFileName.split("/")[1]);
            writer.newLine();
            writer.write(GlobalVars.helpFileName.split("/")[1]);
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