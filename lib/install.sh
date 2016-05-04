#!/bin/bash

echo "Installiere Abbozza.jar ..."

SOURCE_DIR=`pwd`/Abbozza.jar
TARGET_DIR=$HOME/Arduino/tools/Abbozza/tool

# echo "Source :" $SOURCE_DIR
# echo "Creating target directory " $TARGET_DIR
mkdir -p $TARGET_DIR

if [ -e $TARGET_DIR/Abbozza.jar ]; then
    echo
    echo $TARGET_DIR/Abbozza.jar "existiert bereits!"
    read -p "Überschreiben? [j/N]  " ANSWER
    if [ $ANSWER = "j" ]; then
        echo "Die Datei " $TARGET_DIR/Abbozza.jar "wird überschrieben." 
        rm $TARGET_DIR/Abbozza.jar
        cp -f $SOURCE_DIR $TARGET_DIR
    else
       echo "Die Installation wurde abgebrochen."
    fi
else
    cp $SOURCE_DIR $TARGET_DIR
fi
