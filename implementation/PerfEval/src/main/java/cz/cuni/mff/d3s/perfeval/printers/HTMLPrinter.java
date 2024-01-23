package cz.cuni.mff.d3s.perfeval.printers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Comparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Class for printing results into HTML format.
 */
public class HTMLPrinter implements ResultPrinter {
    /**
     * Stream to print the results into.
     */
    private final PrintStream printStream;
    /**
     * Comparator for filtering the results.
     */
    private final Comparator<MeasurementComparisonRecord> filter;
    /**
     * Path to the HTML template.
     */
    private static final String templatePath = "templates/result-printer-template.html";

    /**
     * Constructor for the HTML printer.
     * @param printStream Stream to print the results into.
     * @param filter Comparator for filtering the results.
     */
    public HTMLPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
    }

    /**
     * Prints the results into the stream.
     * @param resultCollection collection of results to be printed
     * @throws MeasurementPrinterException if the template is not found
     */
    @Override
    public void PrintResults(MeasurementComparisonResultCollection resultCollection) throws MeasurementPrinterException {
        // print into file, not to std output (user redirects output to file)
        resultCollection.sort(filter);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // Create a Thymeleaf context and add data to it
        Context context = new Context();
        context.setVariable("oldVersion", resultCollection.getOldVersion());
        context.setVariable("newVersion", resultCollection.getNewVersion());
        context.setVariable("records", resultCollection.getRecords()); // Assuming a getter method `records()` is available

        // Process the loaded template with Thymeleaf and return the rendered HTML
        String result = templateEngine.process(templatePath, context);
        printStream.println(result);
    }
}
