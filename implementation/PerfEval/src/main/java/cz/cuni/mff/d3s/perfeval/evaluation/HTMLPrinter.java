package cz.cuni.mff.d3s.perfeval.evaluation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Comparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class HTMLPrinter implements ResultPrinter{
    PrintStream printStream;
    Comparator<MeasurementComparisonRecord> filter;
    static String templatePath = "templates/result-printer-template.html";

    public HTMLPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }
    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) throws MeasurementPrinterException {
        //TODO: print into file, not to std output
        resultCollection.sort(filter);
        String templateContent = "";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath)){
            assert inputStream != null;
            templateContent = new String(inputStream.readAllBytes());
        } catch (NullPointerException | IOException e) {
            //printStream.println("Cannot be formatted");
            throw new MeasurementPrinterException("HTML Template was not found", e);
        }

        TemplateEngine templateEngine = new TemplateEngine();

        // Create a Thymeleaf context and add data to it
        Context context = new Context();
        context.setVariable("oldVersion", resultCollection.getOldVersion());
        context.setVariable("newVersion", resultCollection.getNewVersion());
        context.setVariable("records", resultCollection.getRecords()); // Assuming a getter method `records()` is available

        // Process the loaded template with Thymeleaf and return the rendered HTML
        String result = templateEngine.process(templateContent, context);
        printStream.println(result);
    }
}
