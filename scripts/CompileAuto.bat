@echo off
cd ..
mkdir bin
javac -d bin src/JJTree/*.java
javac -d bin src/tuner/*.java
cd scripts
pause
