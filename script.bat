@echo off

mkdir bin

javac -d bin JJTree/*.java
javac -d bin tuner/*.java

java -cp bin tuner.Auto c_files/*.c

pause
