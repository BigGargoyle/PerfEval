package org.example.perfevalGraphicalEvaluator;

/**
 * Class with methods for executing perfeval evaluate --graphical command
 */
public class GraphicalEvaluator {
/*
    static String RESULT_FILE_NAME = "benchmarkTests.json";
    static int DEFAULT_FILE_COUNT = 50;

    public static boolean evaluateGraphical(String[] args, IDatabase database) {

        String resultDir = getDirForResults(args);
        DatabaseItem[] databaseItems = database.getLastNResults(DEFAULT_FILE_COUNT);
        List<BenchmarkTest> benchmarkTests = getTestsFromDatabaseItems(databaseItems);
        if (benchmarkTests == null || benchmarkTests.size() == 0)
            return false;
        return createOutputFile(benchmarkTests, resultDir);
    }

    static boolean createOutputFile(List<BenchmarkTest> benchmarkTests, String resultDir){
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String json = objectMapper.writeValueAsString(benchmarkTests);

            if(resultDir!=null && (new File(resultDir)).isDirectory()){
                return saveOutputResultFile(json, resultDir);
            }
            else{
                return createTmpResultFile(json);
            }
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    static boolean saveOutputResultFile(String jsonString, String dirPath){
        copyWebsiteFilesToDir(dirPath);
        File resultFile = new File(dirPath, RESULT_FILE_NAME);
        if(resultFile.exists())
            if(!resultFile.delete())
                return false;
        try {
            FileWriter fileWriter = new FileWriter(resultFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
            System.out.println("You can see result at: "+dirPath+"index.html");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    static void copyWebsiteFilesToDir(String dirPath){
        //TODO: copy files to directory
    }
    static boolean createTmpResultFile(String jsonString){
        String pathToDirWithResults = FileNames.workingDirectory+"/"+FileNames.perfevalDir;
        File resultFile = new File(pathToDirWithResults, RESULT_FILE_NAME);
        if(resultFile.exists())
            if(!resultFile.delete())
                return false;
        try {
            FileWriter fileWriter = new FileWriter(resultFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
            System.out.println("You can see result at: "+pathToDirWithResults+"index.html");
            waitForEnter();
            return resultFile.delete();
        } catch (IOException e) {
            return false;
        }
    }

    static void waitForEnter(){
        System.out.println("Press Enter to end application...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
    }

    static List<BenchmarkTest> getTestsFromDatabaseItems(DatabaseItem[] databaseItems) {
        Hashtable<String, BenchmarkTest> testDictionary = new Hashtable<>();

        if (databaseItems == null || databaseItems.length == 0)
            return null;

        IMeasurementParser parser = ParserFactory.recognizeParserFactory(databaseItems[0].path());
        if (parser == null)
            return null;

        for (DatabaseItem item : databaseItems) {
            if (item == null) continue;
            List<Measurement> measurements = parser.getTestsFromFile(item.path());
            for (Measurement measurement : measurements) {
                TestValue value = new TestValue(calcAverage(measurement.measuredTimes()), item.version(), item.dateOfCreation());
                BenchmarkTest benchmarkTest = testDictionary.get(measurement.name());
                if (benchmarkTest == null) {
                    benchmarkTest = new BenchmarkTest(measurement.name(), new ArrayList<>());
                    testDictionary.put(measurement.name(), benchmarkTest);
                }
                benchmarkTest.values.add(value);
            }
        }

        List<BenchmarkTest> benchmarkTests = new ArrayList<>();
        boolean b = benchmarkTests.addAll(testDictionary.values());
        return b ? benchmarkTests : null;
        // return benchmarkTests;
    }

    static long calcAverage(List<UniversalTimeUnit> timeUnits) {
        long result = 0;
        for (UniversalTimeUnit timeUnit : timeUnits) {
            result += timeUnit.getNanoSeconds();
        }
        return result / timeUnits.size();
    }


    static int DIR_POSITION_ARG = 3;

    public static String getDirForResults(String[] args) {
        if (args.length < DIR_POSITION_ARG + 1)
            return null;
        String dirName = args[DIR_POSITION_ARG];
        if (!(new File(dirName)).isDirectory()) {
            System.err.println("Directory: " + dirName + " was not found.");
            return null;
        }
        return dirName;
    }

    static final class BenchmarkTest {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final List<TestValue> values;

        public BenchmarkTest(String name, List<TestValue> values) {
            this.name = name;
            this.values = values;
        }

        public String name() {
            return name;
        }

        public List<TestValue> values() {
            return values;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (BenchmarkTest) obj;
            return Objects.equals(this.name, that.name) &&
                    Objects.equals(this.values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, values);
        }

        @Override
        public String toString() {
            return "BenchmarkTest[" +
                    "name=" + name + ", " +
                    "values=" + values + ']';
        }
    }

    static final class TestValue {
        @JsonProperty
        private final long nanoseconds;
        @JsonProperty
        private final String version;
        @JsonProperty
        private final Date dateOfMeasuring;

        public TestValue(long nanoseconds, String version, Date dateOfMeasuring) {
            this.nanoseconds = nanoseconds;
            this.version = version;
            this.dateOfMeasuring = dateOfMeasuring;
        }

        public long nanoseconds() {
            return nanoseconds;
        }

        public String version() {
            return version;
        }

        public Date dateOfMeasuring() {
            return dateOfMeasuring;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TestValue) obj;
            return this.nanoseconds == that.nanoseconds &&
                    Objects.equals(this.version, that.version) &&
                    Objects.equals(this.dateOfMeasuring, that.dateOfMeasuring);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nanoseconds, version, dateOfMeasuring);
        }

        @Override
        public String toString() {
            return "TestValue[" +
                    "nanoseconds=" + nanoseconds + ", " +
                    "version=" + version + ", " +
                    "dateOfMeasuring=" + dateOfMeasuring + ']';
        }
    }
*/
}
