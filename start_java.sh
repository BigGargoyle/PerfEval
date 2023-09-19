#/bin/bash

DIRECTORY=$( pwd )
INSTALL_DIR=/home/dominik/Desktop/performance-unit-test-evaluator/implementation/PerfEval/build/classes/java/main
cd $INSTALL_DIR
#echo "java org.example.Main $DIRECTORY $@"
CLASSPATH="$CLASSPATH:$INSTALL_DIR:/home/dominik/Desktop/performance-unit-test-evaluator/implementation/PerfEval/build/libs/PerfEval-1.0-SNAPSHOT.jar"
java org.example.Main $DIRECTORY $@