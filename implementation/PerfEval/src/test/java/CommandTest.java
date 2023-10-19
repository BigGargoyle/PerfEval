public class CommandTest {
    static String[] testEvaluateCLILines = new String[]{
        "evaluate",
        "evaluate --old-version version1",
        "evaluate --old-version=version1",
        "evaluate --old-version version3", // unknown version -> null
        "evaluate --old-version version1 --new-version version2",
        "evaluate --new-version version1 --old-version version2",
        "evaluate --old-version version1 --t-test",
        "evaluate --old-version version1 --json-output",
        "evaluate --old-version version1 --t-test --json-output",
        "evaluate --old-version version1 --filter test-result",
        "evaluate --old-version version1 --filter test-result2" // unknown filter -> null
    };

    static String[] testInitLines = new String[]{
        "init",
        "init --force"
    };

    static String[] testIndexNewResultLines = new String[]{
        "index-new-result --path path/to/file",
        "index-new-result --path path/to/file --version version1",
        "index-new-result --path path/to/file --version version1 --tag tag1"
    };
    static String[] testIndexAllResultsLines = new String[] {
        "index-all-result --path path/to/file",
        "index-all-result --path path/to/file --version version1",
        "index-all-result --path path/to/file --version version1 --tag tag1"
    };
    static String[] testListUndecidedLines = new String[] {
        "list-undecided",
        "list-undecided --max-time-on-test 1h",
        "list-undecided --max-time-on-test 1h30m",
        "list-undecided --max-time-on-test 1h30m30s",
        "list-undecided --max-time-on-test 30m30s",
        "list-undecided --max-time-on-test 30x30s",
        "list-undecided --max-time-on-test 30x30x", // not valid -> null
        "list-undecided --max-time-on-test 30s",
        "list-undecided --max-time-on-test 30x", // not valid -> null
        "list-undecided --old-version version1",
        "list-undecided --old-version version3", // unknown version -> null
        "list-undecided --old-version version1 --new-version version2",
        "list-undecided --new-version version1 --old-version version2",
        "list-undecided --old-version version1 --filter test-result",
        "list-undecided --old-version version1 --filter test-result2" // unknown filter -> null
    };

}
