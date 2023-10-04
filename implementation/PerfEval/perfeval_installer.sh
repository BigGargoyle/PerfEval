#!/bin/bash

INSTALL_DIR=$( pwd )
gradle clean
gradle jar
INSTALL_DIR+=/build/libs/
JARFILE=PerfEval-1.0-SNAPSHOT.jar

PERFEVAL_STARTER_FILE=perfeval_starter.sh
if [ -e "$PERFEVAL_STARTER_FILE" ]; then
    rm "$PERFEVAL_STARTER_FILE"
fi
touch $PERFEVAL_STARTER_FILE
echo \#!/bin/bash >> $PERFEVAL_STARTER_FILE
echo "DIRECTORY=\$( pwd )" >> $PERFEVAL_STARTER_FILE
echo "INSTALL_DIR=$INSTALL_DIR" >> $PERFEVAL_STARTER_FILE
echo "cd \$INSTALL_DIR" >> $PERFEVAL_STARTER_FILE
echo "JARFILE=\$INSTALL_DIR" >> $PERFEVAL_STARTER_FILE
echo "JARFILE+=$JARFILE" >> $PERFEVAL_STARTER_FILE
echo "java -jar \$JARFILE \$DIRECTORY \$@" >> $PERFEVAL_STARTER_FILE

chmod +x $PERFEVAL_STARTER_FILE

SHELL_NAME=$(basename "$SHELL")
if [ "$SHELL_NAME" = "bash" ]; then
    ALIAS_CONTENT="$(pwd)/$PERFEVAL_STARTER_FILE"
    echo "alias perfeval=\"$ALIAS_CONTENT\"" >> ~/.bashrc
    echo "Alias 'perfeval' has been added to ~/.bashrc"
    source ~/.bashrc
fi
