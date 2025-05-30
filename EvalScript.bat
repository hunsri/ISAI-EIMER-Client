@echo off

set "FirstName=ALPHA"
set "SecondName=BETA"
set "ThirdName=GAMMA"

set "FirstRandom=%1"
set "SecondRandom=%2"
set "ThirdRandom=%3"

(set LF=^
%=DONT REMOVE THIS. THIS IS A HACK TO ALLOW A NEWLINE VARIABLE=%
)

set "UsedParameters=%FirstName% %FirstRandom%!LF!%SecondName% %SecondRandom%!LF!%ThirdName% %ThirdRandom%"
setlocal EnableDelayedExpansion
echo %UsedParameters% > res/evaluation/latestParameters.txt

echo "launching server..."
start cmd /K "java -Djava.library.path=lib/lib/native -jar "lib\eimer.jar" 8 headless > res/evaluation/output.txt"

start cmd /C "cd AClient & java -cp "bin;lib\eimer.jar" AClient %FirstName% %FirstRandom%"

start cmd /C "cd AClient & java -cp "bin;lib\eimer.jar" AClient %SecondName% %SecondRandom%"

start cmd /C "cd AClient & java -cp "bin;lib\eimer.jar" AClient %ThirdName% %ThirdRandom%"