#!/bin/bash

# This script is an example of how to use the performance-unit-test-evaluator (PerfEval) tool.

CALL_PERFEVAL="../PerfEval/perfeval.sh"

# INIT COMMAND

echo "Calling perfeval init comand"
PARSER="BenchmarkDotNetJSONParser"
echo "Using $PARSER as the parser."

if ! $CALL_PERFEVAL init --benchmark-parser $PARSER; then
    echo "Init failed. Using --force flag."
    if ! $CALL_PERFEVAL init --force --benchmark-parser $PARSER; then
        echo "Init failed again. Exiting."
        exit 1
    fi
fi

echo
echo "Perfeval init command executed successfully."
echo
echo

# Index-all-results COMMAND

echo "Calling perfeval index-new-result command"
WORSE_RESULT_PATH="./tests/benchmark-dot-net-results/BenchmarkDotNetResult-12.json"
BETTER_RESULT_PATH="./tests/benchmark-dot-net-results/BenchmarkDotNetResult-18.json"

echo "Calling perfeval index-new-result command for $WORSE_RESULT_PATH"
if ! $CALL_PERFEVAL index-new-result --path $WORSE_RESULT_PATH --version 1.0; then
    echo "Index-new-result failed for $WORSE_RESULT_PATH. Exiting."
    exit 1
fi

echo "Calling perfeval index-new-result command for $BETTER_RESULT_PATH"
if ! $CALL_PERFEVAL index-new-result --path $BETTER_RESULT_PATH --version 2.0; then
    echo "Index-new-result failed for $BETTER_RESULT_PATH. Exiting."
    exit 1
fi

# Evaluate COMMAND

echo "Calling perfeval evaluate command"
$CALL_PERFEVAL evaluate
exit_code=$?
if [ $exit_code -gt 100 ]; then
    echo "Evaluate failed. Exiting."
    exit 1
fi

if [ ! $exit_code -eq 0 ]; then
    echo "Performance test failed. There is some worse performance."
else
    echo "Performance test passed. There is no worse performance."
fi

echo
echo "Evaluation executed successfully for BenchmarkDotNet."
echo
echo

# INIT COMMAND

echo "Calling perfeval init comand"
PARSER="JMHJSONParser"
echo "Using $PARSER as the parser."

if ! $CALL_PERFEVAL init --benchmark-parser $PARSER; then
    echo "Init failed. Using --force flag."
    if ! $CALL_PERFEVAL init --force --benchmark-parser $PARSER; then
        echo "Init failed again. Exiting."
        exit 1
    fi
fi

echo
echo "Perfeval init command executed successfully."
echo
echo

# INDEX-ALL-RESULTS COMMAND

echo "Calling perfeval index-all-results command"
WORSE_RESULTS_PATH="./tests/worse_performance"
echo "This command will index all the results from $WORSE_RESULTS_PATH in the database."
WORSE_VERSION="1.0.0"
echo "The version of the software that generated the results is $WORSE_VERSION."

if ! $CALL_PERFEVAL index-all-results --path $WORSE_RESULTS_PATH --version $WORSE_VERSION; then
    echo "Index-all-results failed. Exiting."
    exit 1
fi

echo
echo "Results indexed successfully."
echo
echo

# INDEX-NEW-RESULT COMMAND

echo "Calling perfeval index-new-result command to all files in the directory"

BETTER_RESULTS_PATH="./tests/better_performance"

echo "This command will index all the results from $BETTER_RESULTS_PATH in the database."

BETTER_VERSION="2.0.0"

echo "The version of the software that generated the results is $BETTER_VERSION."

# Check if the directory exists
if [ -d "$BETTER_RESULTS_PATH" ]; then
    # Iterate over files in the directory
    for file in "$BETTER_RESULTS_PATH"/*; do
        # Check if the item is a file (not a directory)
        if [ -f "$file" ]; then
            # Call the perfeval command on each file
            if ! $CALL_PERFEVAL index-new-result --path "$file" --version "$BETTER_VERSION"; then
                echo "Index-new-result failed for $file. Exiting."
                exit 1
            fi
        fi
    done
else
    echo "Directory $BETTER_RESULTS_PATH does not exist."
    exit 1
fi

echo
echo "Results indexed successfully."
echo
echo

# LIST-RESULTS COMMAND

echo "Calling perfeval list-results command"
echo "This command will list all the results in the database."

if ! $CALL_PERFEVAL list-results; then
    echo "List-results failed. Exiting."
    exit 1
fi

echo
echo "Results listed successfully."
echo
echo

# EVALUATE COMMAND

echo "Calling perfeval evaluate command"
echo "This command will compare the results from the two versions of the software."

$CALL_PERFEVAL evaluate
exit_code=$?

if [ $exit_code -gt 100 ]; then
    echo "Evaluate failed with exit code greater than 100. Exiting."
    exit 1
fi

if [ ! $exit_code -eq 0 ]; then
    echo "Performance test failed. There is some worse performance."
else
    echo "Performance test passed. There is no worse performance."
fi

echo
echo "Evaluation executed successfully."
echo
echo

# EVALUATE COMMAND WITH FLAGS --HTML-OUTPUT

echo "Calling perfeval evaluate --html-output command"
echo "This command will compare the results from the two versions of the software and generate an HTML report."

$CALL_PERFEVAL evaluate --html-output
exit_code=$?
if [ $exit_code -gt 100 ]; then
    echo "Evaluate --html-output failed. Exiting."
    exit 1
fi

if [ ! $exit_code -eq 0 ]; then
    echo "Performance test failed with flag --html-output. There is some worse performance."
else
    echo "Performance test passed. There is no worse performance."
fi

echo
echo "Evaluation executed successfully."
echo
echo

# EVALUATE COMMAND WITH FLAGS --JSON-OUTPUT

echo "Calling perfeval evaluate --json-output command"
echo "This command will compare the results from the two versions of the software and generate a JSON report."

$CALL_PERFEVAL evaluate --json-output > report.json
exit_code=$?
if [ $exit_code -gt 100 ]; then
    echo "Evaluate --json-output failed. Exiting."
    exit 1
else
    echo "JSON report generated successfully."
    echo "Report redirected to report.json file."
fi

if [ ! $exit_code -eq 0 ]; then
    echo "Performance test failed with flag --json-output. There is some worse performance."
else
    echo "Performance test passed. There is no worse performance."
fi

echo
echo "Evaluation executed successfully."
echo
echo

# EVALUATE COMMAND WITH FLAGS --T-TEST

echo "Calling perfeval evaluate --t-test command"
echo "This command will compare the results from the two versions of the software using a t-test."
echo "T-test is used instead of the default bootstrapping method."

$CALL_PERFEVAL evaluate --t-test
exit_code=$?
if [ $exit_code -gt 100 ]; then
    echo "Evaluate --t-test failed. Exiting."
    exit 1
fi

if [ ! $exit_code -eq 0 ]; then
    echo "Performance test failed with flag --t-test. There is some worse performance."
else
    echo "Performance test passed. There is no worse performance."
fi

echo
echo "Evaluation executed successfully."
echo
echo