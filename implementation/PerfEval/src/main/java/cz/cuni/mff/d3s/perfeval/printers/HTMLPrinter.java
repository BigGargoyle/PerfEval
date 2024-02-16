package cz.cuni.mff.d3s.perfeval.printers;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Comparator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

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
    private final Path perfevalDir;

    /**
     * Path to the HTML template.
     */
    private final String templatePath;

    /**
     * Default path to the HTML template.
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
        this.perfevalDir = perfevalDir;
        this.templatePath = TEMPLATE_PATH;
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        this.templateResolver = templateResolver;
    }

    /**
     * Constructor for the HTML printer.
     *
     * @param printStream  Stream to print the results into.
     * @param filter       Comparator for filtering the results.
     * @param perfevalDir  Path to PerfEval directory.
     * @param templatePath Path to the HTML template.
     */
    public HTMLPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter, Path perfevalDir, Path templatePath) {
        this.printStream = printStream;
        this.filter = filter;
        this.perfevalDir = perfevalDir;
        this.templatePath = templatePath.toAbsolutePath().toString();

        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCacheable(false);
        templateResolver.setCheckExistence(true);
        this.templateResolver = templateResolver;
    }

    /**
     * Template resolver for Thymeleaf.
     */
    private final ITemplateResolver templateResolver;

    /**
     * Prints the results into the stream.
     *
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

    /**
     * Writes the result into the output file.
     *
     * @param result result to be written
     * @throws MeasurementPrinterException if the file cannot be written
     */
    private void writeToOutputFile(String result) throws MeasurementPrinterException {
        Path outputFilePath = getOutputFilePath(perfevalDir);
        try (PrintStream out = new PrintStream(outputFilePath.toString())) {
            out.print(result);
            printStream.println(INFO_MESSAGE + outputFilePath);
        } catch (Exception e) {
            throw new MeasurementPrinterException("Error while writing to file: " + outputFilePath, e);
        }
    }

    /**
     * Returns the path to the output file.
     *
     * @param perfevalDir path to PerfEval directory
     * @return path to the output file
     */
    private static Path getOutputFilePath(Path perfevalDir) {
        return perfevalDir.resolve(RESULT_FILE_NAME);
    }

    /**
     * Fills the template with the results.
     *
     * @param resultCollection collection of results to be printed
     * @return filled template
     * @throws MeasurementPrinterException if the template is not found
     */
    private String fillTemplate(MeasurementComparisonResultCollection resultCollection) throws MeasurementPrinterException {
        try {
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            // Create a Thymeleaf context and add data to it
            Context context = new Context();
            context.setVariable("oldVersion", resultCollection.getOldVersion());
            context.setVariable("newVersion", resultCollection.getNewVersion());
            context.setVariable("records", resultCollection.getRecords()); // Assuming a getter method `records()` is available

            // Process the loaded template with Thymeleaf and return the rendered HTML
            return templateEngine.process(templatePath, context);
        } catch (Exception e) {
            throw new MeasurementPrinterException("Error while filling the template: " + templatePath);
        }
    }
}
