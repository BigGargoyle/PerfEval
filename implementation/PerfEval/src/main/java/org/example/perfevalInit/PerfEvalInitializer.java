package org.example.perfevalInit;

import org.example.GlobalVars;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PerfEvalInitializer {

    public static boolean InitPerfEval() {
        try {
            BufferedWriter iniFile = new BufferedWriter(new FileWriter(GlobalVars.IniFileName));
            boolean success = writeIniFileContent(iniFile);
            iniFile.close();

            BufferedWriter helpFile = new BufferedWriter(new FileWriter(GlobalVars.helpFileName));
            success = success & writeHelpFileContent(helpFile);

            helpFile.close();

            return success;
        } catch (IOException e) {
            return false;
        }
    }

    static boolean writeIniFileContent(BufferedWriter iniFileWriter) {
        try {
            iniFileWriter.write(GlobalVars.critValueSign + GlobalVars.ColumnDelimiter + GlobalVars.defaultStatisticCritValue);
            iniFileWriter.newLine();
            iniFileWriter.write(GlobalVars.maxCIWidthSign + GlobalVars.ColumnDelimiter + GlobalVars.defaultMaxCIWidth);
            iniFileWriter.newLine();
            iniFileWriter.write(GlobalVars.maxTimeOnTestSign + GlobalVars.ColumnDelimiter + GlobalVars.defaultMaxTimeOnTest.GetNanoSeconds());
            iniFileWriter.newLine();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

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