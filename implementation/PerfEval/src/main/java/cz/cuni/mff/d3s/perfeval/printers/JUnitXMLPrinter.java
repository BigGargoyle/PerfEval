package cz.cuni.mff.d3s.perfeval.printers;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.io.PrintStream;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.PERFEVAL_DIR;
import static cz.cuni.mff.d3s.perfeval.command.SetupUtilities.USER_DIR;

public class JUnitXMLPrinter implements ResultPrinter {

    private static final String TEMPLATE_PATH = "templates/junit-template.xml";

    private static final String RESULT_FILE_NAME = "junit-result.xml";

    /**
     * Info message for the user.
     */
    private static final String INFO_MESSAGE = "Results were saved into file: ";

    private static final Path perfevalDir = USER_DIR.resolve(PERFEVAL_DIR);

    private final PrintStream printStream;
    private final Comparator<MeasurementComparisonRecord> filter;

    /**
     * Template resolver for Thymeleaf.
     */
    private final ITemplateResolver templateResolver;

    private final String templatePath;

    public JUnitXMLPrinter(PrintStream printStream, Comparator<MeasurementComparisonRecord> filter) {
        this.printStream = printStream;
        this.filter = filter;
        this.templatePath = TEMPLATE_PATH;
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("XML");
        templateResolver.setCacheable(false);
        this.templateResolver = templateResolver;
    }

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
        Path outputFilePath = getOutputFilePath();
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
     * @return path to the output file
     */
    private static Path getOutputFilePath() {
        return JUnitXMLPrinter.perfevalDir.resolve(RESULT_FILE_NAME);
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
            context.setVariable("comparisonRecords", resultCollection.getRecords());
            context.setVariable("testCount", resultCollection.size());

            List<TestCase> testCases = resultCollection.stream().map(TestCase::new).toList();
            context.setVariable("testcases", testCases);


            context.setVariable("failureCount", testCases.stream().filter(TestCase::hasFailure).count());
            context.setVariable("errorCount", 0);
            context.setVariable("skipCount", 0);

            // Process the loaded template with Thymeleaf and return the rendered XML
            return templateEngine.process(templatePath, context);
        } catch (Exception e) {
            throw new MeasurementPrinterException("Error while filling the template: " + templatePath);
        }
    }

    public static class TestCase {
        public String name;
        public String classname;

        public String failureMessage;

        public String failureType;

        public double performanceChange;

        public String comparisonResultMessage;

        public boolean failure;

        public int minSampleCount;

        public boolean hasFailure() {
            return this.failure;
        }

        public TestCase(MeasurementComparisonRecord record) {
            int lastDotPos = record.oldSamples().getName().lastIndexOf('.');
            this.name = record.oldSamples().getName().substring(lastDotPos + 1);
            try {
                this.classname = record.oldSamples().getName().substring(0, lastDotPos);
            } catch (StringIndexOutOfBoundsException e) {
                this.classname = "";
            }
            this.performanceChange = record.performanceChange();
            DecimalFormat df = new DecimalFormat("0.00");
            this.performanceChange = Double.parseDouble(df.format(this.performanceChange));
            switch (record.comparisonResult()) {
                case SameDistribution:
                    this.failureMessage = "";
                    this.comparisonResultMessage = "";
                    this.failure = false;
                    break;
                case DifferentDistribution:
                    this.failureMessage = "Worse performance";
                    this.failureType = "PERFORMANCE";
                    this.comparisonResultMessage = "Performance has changed";
                    this.failure = performanceChange < 0;
                    break;
                case NotEnoughSamples:
                    this.failureMessage = "Not enough samples were measured.";
                    this.failureType = "MEASURING";
                    this.comparisonResultMessage = "There are not enough runs. (" + record.minSampleCount() + " runs needed)";
                    this.failure = true;
                    break;
                case Bootstrap:
                    this.failureMessage = "";
                    this.comparisonResultMessage = "Impossible to measure enough runs. (" + record.minSampleCount() + " runs needed)";
                    this.failure = false;
                    break;
                default:
                    this.comparisonResultMessage = "";
                    break;
            }
            this.minSampleCount = record.minSampleCount();

        }

    }

}
