#/bin/bash

DIRECTORY=$( pwd )
INSTALL_DIR=/home/dominik/Desktop/performance-unit-test-evaluator/implementation/PerfEval/build/libs/
cd $INSTALL_DIR
JARFILE=$INSTALL_DIR
JARFILE+=PerfEval-1.0-SNAPSHOT.jar
#echo $JARFILE
#echo "java org.example.Main $DIRECTORY $@"
java -jar $JARFILE $DIRECTORY $@