#!/bin/bash
mkdir -p bin

javac -d bin JJTree/*.java
javac -d bin tuner/*.java

java -cp bin tuner.Auto c_files/*.c

echo "Press any key to continue . . ."
read -r var
