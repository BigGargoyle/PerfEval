### Available Commands

This section of the work discusses the individual commands of the PerfEval system and their parameters. Each subsection explains the purpose of the respective command. The subsections also provide information about the required and optional parameters for each command.

#### Command: `init`

The `init` command is used to initialize the system within the current working directory. After starting, PerfEval looks for a folder named `.performance` in the working directory. If the folder is not found and the `init` command was not executed, the system will terminate with an error indicating that it has not been initialized.

**Required Arguments:**

- `benchmark-parser`: Specifies the parser to be used for the project. The parser name is provided as a parameter for this flag. The parser is selected based on the testing framework and output format used. This is the only configuration parameter that can be provided during initialization because it determines the structure and format of the input data PerfEval expects, and thus cannot be set with a default value.

**Optional Arguments:**

- `force`: Forces initialization even if the system has already been initialized in the directory.

#### Command: `index-new-result`

The `index-new-result` command is used to add performance measurement results to the database. Each project has its own database, allowing multiple projects to be managed by PerfEval on the same machine. When adding information about a result file, the path to the file must be specified. The version to which the results apply can also be provided, or the system will attempt to determine it from the Git repository.

**Required Arguments:**

- `path`: This flag specifies the path to the result file.

**Optional Arguments:**

- `version`: Specifies the text representation of the software version being measured.
- `tag`: Specifies the tag of the measured version.

#### Command: `index-all-results`

The `index-all-results` command is used to add multiple performance measurement results to the database. When adding information about result files, the path to the directory containing these results must be specified. All files (including nested ones) in this directory will be added. The version to which the results apply can also be provided, or the system will attempt to estimate it from the Git repository.

**Required Arguments:**

- `path`: This flag specifies the path to the directory containing the result files.

**Optional Arguments:**

- `version`: Specifies the text representation of the software version being measured.
- `tag`: Specifies the tag of the measured version.

#### Command: `evaluate`

The `evaluate` command compares the two most recent recorded versions that have been measured. The versions for comparison can also be specified manually using flags. The output is a table or JSON file with the comparison results. If any of the performance tests show a regression in the new version, the system will return an exit code of 1.

**Optional Arguments:**

- `new-version`: Specifies the text representation of the version to be considered as the newer version in the comparison.
- `new-tag`: Only files with this tag will be used as the newer version for comparison.
- `old-version`: Specifies the text representation of the version to be considered as the older version in the comparison.
- `old-tag`: Only files with this tag will be used as the older version for comparison.
- `t-test`: Uses the t-test for statistical comparison instead of bootstrap.
- `json-output`: The output will be in JSON format.
- `html-output`: The output will be in HTML format.
- `html-template`: When using the `html-output` flag, this argument can specify the path to a custom HTML template for displaying the results.
- `junit-xml-output`: The output will be in JUnit XML format. This format is commonly used by Git tools for continuous integration.
- `filter`: This flag allows filtering the results based on the name of the test method. The options are `test-id`, `size-of-change`, and `test-result`.

#### Command: `list-undecided`

The `list-undecided` command compares the two most recent recorded versions that have been measured. The versions for comparison can also be specified manually using flags. The output is two columns separated by a tab character. The first column contains the name of the test method, and the second column contains the number of measurements needed to determine with sufficient confidence whether the performance is the same.

**Optional Arguments:**

- `new-version`: Specifies the text representation of the version to be considered as the newer version in the comparison.
- `new-tag`: Only files with this tag will be used as the newer version for comparison.
- `old-version`: Specifies the text representation of the version to be considered as the older version in the comparison.
- `old-tag`: Only files with this tag will be used as the older version for comparison.
- `t-test`: Uses the t-test for statistical comparison instead of bootstrap.
- `filter`: This flag allows filtering the results based on the name of the test method. The options are `test-id`, `size-of-change`, and `test-result`.

#### Command: `list-results`

The `list-results` command displays information about the files stored in the database. This command does not have any arguments. The output is a table with information about the files, providing a simple overview of the files in the PerfEval database.

### Configuration File

After running the `init` command, PerfEval creates a `.performance` folder in the working directory. This folder contains a configuration file named `config.ini`. This file holds settings related to the usage of PerfEval for performance evaluation of a specific project. Modifying the values in the configuration file can limit the system's behavior to some extent.

**Configuration File Values:**

- `falseAlarmProbability`: Specifies the probability of a Type I error when testing the hypothesis that the performance of the versions is the same.
- `accuracy`: Specifies the maximum relative width of the confidence interval.
- `minTestCount`: Specifies the minimum number of tests (runs) to be performed.
- `maxTestCount`: Specifies the maximum number of tests (runs) the user is willing to measure.
- `tolerance`: Specifies the maximum performance degradation (relative to the older version) that will not cause a failure.
- `git`: Specifies whether the project is under version control using Git. The values are `TRUE` or `FALSE`.
- `parserName`: The name of the parser to be used when processing the result files.
- `highDemandOfRuns`: Specifies whether PerfEval should report if more runs than `maxTestCount` are needed. The values are `TRUE` or `FALSE`.
- `improvedPerformance`: Specifies whether PerfEval should report if the performance of the newer version is better.
