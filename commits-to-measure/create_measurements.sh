#!/bin/bash

# file with commit hashes
file="$1"
# directory to store the test results
test_result_dir=$2

iteration_count=5
test_result_subdir_name="crate-results-"

# path to the test jar from crate root directory
path_to_test_jar="./benchmarks/target/benchmarks.jar"

# command to start the tests
start_test="java -jar $path_to_test_jar -rf json -rff "


if [ ! -f "$file" ]; then
    echo "Error: '$file' is not a valid file."
    exit 1
fi

if [ ! -d "$test_result_dir" ]; then
    echo "Error: '$test_result_dir' is not a valid directory."
    exit 1
fi

grep '^commit' "$file" | while IFS= read -r line; do
    # get the second column of the line and remove all non-alphanumeric characters
    second_column=$(echo "$line" | cut -d' ' -f2 |sed 's/[^[:alnum:]]//g')
    # create a subdirectory name for the test results
    subdir_name="$test_result_subdir_name$second_column"
    # create full relative path to the subdirectory
    subdir_path="$test_result_dir/$subdir_name"

    # if the subdirectory does not exist, create it and run the tests
    if [ ! -d "$subdir_path" ]; then
        mkdir -p "$subdir_path"

        for ((i=1; i<=$iteration_count; i++)); do
            git checkout $second_column --force
            test_result_file="$subdir_path/$second_column-$i.json"
            $start_test $test_result_file
        done
    fi

done
