echo "START!"

echo "launching server..."
start cmd /K "java -Djava.library.path=lib/lib/native -jar "lib\eimer.jar""

echo "launching client 1"
start cmd /K "cd AClient & java -cp "bin;lib\eimer.jar" AClient ALPHA 0"
echo "launching client 2"
start cmd /K "cd AClient & java -cp "bin;lib\eimer.jar" DemoClient"
echo "launching client 3"
start cmd /K "cd AClient & java -cp "bin;lib\eimer.jar" DemoClient"