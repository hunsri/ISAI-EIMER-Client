## Student Project for Interactive Systems AI (SoSe 2025)

To demo run the app under Windows you can use the following BATCH commands

```batch
echo "START!"

echo "launching server..."
start java -Djava.library.path=eimer/lib/native -jar "eimer\eimer.jar"


echo "launching client 1"
start cmd /K "cd AClient & java -cp "bin;lib\eimer.jar" AClient"

echo "launching client 2"
start cmd /C "cd AClient & java -cp "bin;lib\eimer.jar" DemoClient"

echo "launching client 3"
start cmd /C "cd AClient & java -cp "bin;lib\eimer.jar" DemoClient"
```

