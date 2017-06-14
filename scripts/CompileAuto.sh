#!/bin/bash
cd .. || exit
mkdir -p bin
javac -d bin src/JJTree/*.java
javac -d bin src/tuner/*.java
cd scripts
echo "Press any key to continue . . ."
read -r var
