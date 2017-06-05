@echo off
cd ..
mkdir bin
javac -d bin JJTree/*.java
javac -d bin tuner/*.java
cd scripts
pause
