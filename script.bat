@echo off

mkdir bin

javac -d bin *.java

java -cp bin Auto c_files/*.c

pause
