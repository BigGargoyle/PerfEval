package cz.cuni.mff.d3s.perfeval.printers;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
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
     * Path to PerfEval directory. (.performance)
     */
    private final String perfevalDir;

    /**
     * Path to the HTML template.
     */
    private static final String TEMPLATE_PATH = "templates/result-printer-template.html";

    /**
     * Name of file to save the results into.
     */
    private static final String RESULT_FILE_NAME = "performance_result.html";

    /**
     * Info message for the user.
     */
    private static final String INFO_MESSAGE = "Results were saved into file: ";

    /**
     * Constructor for the HTML printer.
     *
     * @param printStream Stream to print the results into.
     * @param filter      Comparator for filtering the results.
     * @param perfevalDir Path to PerfEval directory.
     */
    public HTMLPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter, Path perfevalDir) {
        this.printStream = printStream;
        this.filter = filter;
        this.perfevalDir = perfevalDir.toString();
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
        String result = fillTemplate(resultCollection);
        writeToOutputFile(result);
    }

    private void writeToOutputFile(String result) throws MeasurementPrinterException {
        String outputFilePath = getOutputFilePath(perfevalDir);
        try (PrintStream out = new PrintStream(outputFilePath)) {
            out.print(result);
            printStream.println(INFO_MESSAGE + outputFilePath);
        } catch (Exception e) {
            throw new MeasurementPrinterException("Error while writing to file: " + outputFilePath, e);
        }
    }

    private static String getOutputFilePath(String perfevalDir) {
        return Path.of(perfevalDir, RESULT_FILE_NAME).toString();
    }

    private static String fillTemplate(MeasurementComparisonResultCollection resultCollection) {
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
        return templateEngine.process(TEMPLATE_PATH, context);
    }
}
