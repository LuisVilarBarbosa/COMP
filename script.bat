@echo off

mkdir bin

javac -d bin tuner/*.java

java -cp bin tuner.Auto c_files/*.c

pause
