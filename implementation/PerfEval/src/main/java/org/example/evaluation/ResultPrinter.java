package org.example.evaluation;

import org.example.resultDatabase.FileWithResultsData;

import java.util.List;

public interface ResultPrinter {
    void PrintResults(MeasurementComparisonResultCollection resultCollection);
}
