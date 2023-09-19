#/bin/bash

DIRECTORY=$( pwd )
INSTALL_DIR=/home/dominik/Desktop/performance-unit-test-evaluator/implementation/PerfEval/build/classes/java/main
cd $INSTALL_DIR
#echo "java org.example.Main $DIRECTORY $@"

java org.example.Main $DIRECTORY $@