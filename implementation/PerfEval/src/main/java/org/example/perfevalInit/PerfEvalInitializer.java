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
            if(!directoryExistsOrCreate(GlobalVars.perfevalDir))
                return false;

            IniFileData iniFileData = new IniFileData(false);
            boolean success = IniFileData.CreateNewIniFile(iniFileData);

            BufferedWriter helpFile = new BufferedWriter(new FileWriter(GlobalVars.helpFileName));
            success = success & writeHelpFileContent(helpFile);
            helpFile.close();

            success = success & forcefullyCreateNewFile(GlobalVars.DatabaseFileName);
            success = success & forcefullyCreateNewFile(GlobalVars.DatabaseCacheFileName);

            return success;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param dirName which dir is going to be checked
     * @return if dir was created
     */
    static boolean directoryExistsOrCreate(String dirName){
        File dir = new File(dirName);
        if(dir.exists())
            return false;
        return dir.mkdirs();
    }

    static boolean forcefullyCreateNewFile(String fileName) {
        File file = new File(fileName);
        try {
            if (file.exists() && !file.delete())
                return false;
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @param helpFileWriter where to write content of help file
     * @return true - if writing was successful, false - otherwise
     */
    static boolean writeHelpFileContent(BufferedWriter helpFileWriter) {
        try {
            // TODO: doplnit obsah help souboru
            helpFileWriter.write("?");
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}