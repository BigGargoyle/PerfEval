package org.example.evaluation;

import org.example.resultDatabase.FileWithResultsData;

import java.util.List;

public interface IResultPrinter {
    void PrintResults(List<MeasurementComparisonRecord> results, FileWithResultsData[] originalFiles);
}
