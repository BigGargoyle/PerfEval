# PerfEval
PerfEval is a console application for evaluating performance test results. It allows programmers to compare performance measurements with statistical analysis, making it a useful tool for software analysis. However, PerfEval does not perform the measurements itself. It requires a compatible measurement framework, and currently supports JSON output from the JMH and BenchmarkDotNET frameworks.

PerfEval is developed in Java and has been tested on Linux using Java 17 and Java 21. While it can also run on Windows, it is recommended to use WSL for optimal performance.

## Directory Contents
This directory contains the source code for PerfEval, which is part of my Bachelor's thesis. The source code is located in the `PerfEval` subdirectory. The `clean-test` subdirectory contains shell scripts that demonstrate PerfEval's functionalities. The crate-micro subdirectory holds performance measurement results from selected commits of the CrateDB GitHub project, measured using the JMH framework.

### PerfEval Directory
This directory includes scripts for compiling the PerfEval app:
	- `gradlew` (Linux)
	- `gradle.bat` (Windows)

Java 17 or 21 must be installed for PerfEval to be built successfully. On Linux, use the command `./gradlew build` to build the app; on Windows, use `gradle.bat build`.

After installation, the `PerfEval.jar` file will be located in the `build/libs` directory. You can run the app with the command `java -jar PerfEval.jar` or by using the provided script `perfeval.sh` (`perfeval.bat` on Windows). This script simply executes the `java -jar PerfEval.jar` command.

To generate the JavaDoc documentation for PerfEval, use the command `bash gradlew javadoc`. The generated documentation will be located in the `build/docs` directory.

Note: PerfEval was successfully built and tested in the Rotunda labs at Charles University using Java 17, with the test script `show_me.sh` completing successfully.

### Quick Start
This quick start guide explains how to install PerfEval and compare two versions of software using the system. It is aimed at Linux users, and WSL is recommended for Windows users who need to execute Linux commands.

1. Run `bash gradlew build` in the source code directory.
2. Navigate to your project directory.
3. Run `bash perfeval.sh init --benchmark-parser parser-name`. If you don’t know the parser name, either leave it empty or skip the `--benchmark-parser` argument. The system will list available parsers. Choose the appropriate parser based on the performance testing framework and output format you are using.
4. Add the results of a reference version using the command `bash perfeval.sh index-new-result --path path-to-result-file --version 1.0`.
5. Add the results of the new version using the command `bash perfeval.sh index-new-result --path path-to-result-file --version 2.0`.
6. Run `bash perfeval.sh evaluate` to compare the performance measurements.
7. A comparison table will be generated, showing the differences between versions 1.0 and 2.0. PerfEval will exit with a non-zero code if any performance test shows a regression in the new version.

### crate-micro Directory

The `crate-micro` directory contains performance measurement results for the CrateDB GitHub project (https://github.com/crate/crate). Once PerfEval is installed and initialized, you can add these results to its database for comparison.

### clean-test Directory

This directory demonstrates how to integrate PerfEval into a GitLab project for continuous integration. The `.gitlab-ci.yml` file is an example of how to use PerfEval during GitLab CI. The CI process should initialize PerfEval, add performance measurement results to its database, and then run the `evaluate` command to compare these results. The CI can then take further action based on the comparison.

### show-perfeval Directory

The `show-perfeval` directory contains the bash script `show_me.sh`, which showcases as many of PerfEval’s features as possible. To run this script, you must first install PerfEval using the `build.sh` script.