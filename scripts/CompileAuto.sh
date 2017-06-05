#!/bin/bash
cd .. || exit
mkdir -p bin
javac -d bin JJTree/*.java
javac -d bin tuner/*.java
cd scripts
echo "Press any key to continue . . ."
read -r var
