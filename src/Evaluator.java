import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Evaluator {

    private final static Path PATH_TO_SERVER_OUTPUT = Path.of("res", "evaluation", "output.txt");
    private final static Path PATH_TO_LATEST_PARAMS = Path.of("res", "evaluation", "latestParameters.txt");
    private final static Path PATH_TO_COUNTER = Path.of("res", "evaluation", "counter.txt");
    private final static Path PATH_TO_DONE_SIGNAL = Path.of("res", "evaluation", "done.txt");

    private final static String[] STARTING_ORDER = {"ALPHA", "BETA", "GAMMA"};
    private final static Path PATH_TO_START_PARAMS = Path.of("res", "evoStartValues.txt");

    private final static int RANDOMNESS_INDEX = 0;

    private final static int MAX_EVOLUTION_STEPS = 25;

    public static void main(String[] args) {

        cleanAll();
        initEvolution();

        while(!hasCounterReachedLimit(MAX_EVOLUTION_STEPS)) {
            if(!isEvolutionStepDone())
                continue;

            System.out.println("---WINNING ORDER GEN "+currentCounterValue()+"---");
            printLastRoundsParameters();

            int[] nextRandoms = nextGenRandomness();
            callEvalScript(String.valueOf(nextRandoms[0]), String.valueOf(nextRandoms[1]), String.valueOf(nextRandoms[2]));
            incrementCounter();

            //dont clean up if this was the last iteration
            if(!hasCounterReachedLimit(MAX_EVOLUTION_STEPS)){
                // cleanUpAfterEvolutionStep();
            }
        }

        System.out.println("END OF EVOLUTION CHAIN REACHED!");
        System.out.println("WINNING VALUES:");
        // Print winning parameters
        printLastRoundsParameters();

        // callEvalScript(String.valueOf(12), String.valueOf(42), String.valueOf(16));
    }

    private static void printLastRoundsParameters() {
        System.out.println(Arrays.toString(fetchParameters(fetchOrderOfWinners()[0])));
        System.out.println(Arrays.toString(fetchParameters(fetchOrderOfWinners()[1])));
        System.out.println(Arrays.toString(fetchParameters(fetchOrderOfWinners()[2])));
    }

    private static int[] nextGenRandomness() {
        int devianceInChildren = 3;
        int maxValue = 100;

        int firstPlaceParams = fetchParameters(fetchOrderOfWinners()[0])[RANDOMNESS_INDEX];
        int secondPlaceParams = fetchParameters(fetchOrderOfWinners()[1])[RANDOMNESS_INDEX];

        int average = average(firstPlaceParams, secondPlaceParams, maxValue);

        return recombination(average, devianceInChildren, maxValue);
    }

    private static int[] recombination(int baselineValue, int deltaChange, int maxInclusive) {
        int[] valuesOfChildren = new int[GameState.MAX_PLAYERS];
        
        //a bit messy, but we can guarantee that array size will be 3 for now
        valuesOfChildren[0] = capValues(baselineValue + deltaChange, 0, maxInclusive);
        valuesOfChildren[1] = baselineValue;
        valuesOfChildren[2] = capValues(baselineValue - deltaChange, 0, maxInclusive);

        return valuesOfChildren;
    }

    private static int average(int valueA, int valueB, int maxInclusive) {
        valueA = capValues(valueA, 0, maxInclusive);
        valueB = capValues(valueB, 0, maxInclusive);

        int average = (valueA + valueB) / 2;
        average = capValues(average, 0, maxInclusive);
        return average;
    }

    private static int capValues(int value, int minInclusive, int maxInclusive) { 
        if(value < minInclusive)
            value = minInclusive;
        if(value > maxInclusive)
            value = maxInclusive;

        return value;
    }

    private static Integer[] fetchParameters(String key) {
        HashMap<String, Integer[]> paramsUsed = mapUsedParameters(splitLinesIntoArray(readFile(PATH_TO_LATEST_PARAMS)));
        return paramsUsed.get(key);
    } 

    private static HashMap<String, Integer[]> mapUsedParameters(String[] usedParams) {
        HashMap<String, Integer[]> map = new HashMap<String,Integer[]>();

        for(String s : usedParams) {
            ArrayList<Integer> currentParams = new ArrayList<Integer>();

            String[] parts = s.split("\\s+"); //splits at spaces
            for(int i = 1; i < parts.length; i++) { //fills the parameter list
                currentParams.add(Integer.valueOf(parts[i]));
            }

            map.put(parts[0], currentParams.toArray(new Integer[0]));
        }

        return map;
    }

    private static String[] fetchOrderOfWinners() {
        if(!java.nio.file.Files.exists(PATH_TO_SERVER_OUTPUT)) {
            return STARTING_ORDER;
        }

        String fileContent = readFile(PATH_TO_SERVER_OUTPUT);
        String[] results = splitLinesIntoArray(extractResults(fileContent));
        HashMap<String, Integer> mappedResult = mapResults(results);

        return mappedResult.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toArray(String[]::new);
    }

    private static HashMap<String, Integer> mapResults(String[] results) {
        HashMap<String, Integer> map = new HashMap<String,Integer>();
        
        for (String result : results) {
            String[] parts = result.split(":\\s*");
            if (parts.length == 2) {
            map.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
        return map;
    }

    private static String[] splitLinesIntoArray(String results){
        return results.split("\\R");
    }

    private static String extractResults(String content) {
        String[] lines = content.split("\\R");
        return java.util.Arrays.stream(lines)
                .skip(Math.max(0, lines.length - 3))
                .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
    }

    private static String readFile(Path path) {
        try {
            return java.nio.file.Files.readString(path);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void callEvalScript(String firstRandom, String secondRandom, String thirdRandom) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "cmd.exe", "/c", "EvalScript.bat", firstRandom, secondRandom, thirdRandom
            );
            // pb.inheritIO(); // Optional: shows script output in console
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("EvalScript.bat exited with code " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int currentCounterValue() {
        try {
        // Read the current value
        String content = java.nio.file.Files.readString(PATH_TO_COUNTER).trim();
        return Integer.parseInt(content);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static boolean hasCounterReachedLimit(int limit) {
        boolean limitReached = false;
        
        try {
        // Read the current value
        String content = java.nio.file.Files.readString(PATH_TO_COUNTER).trim();
        int value = Integer.parseInt(content);

        if(value >= limit)
            limitReached = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return limitReached;
    }

    private static void incrementCounter() {
        try {
        // Read the current value
        String content = java.nio.file.Files.readString(PATH_TO_COUNTER).trim();
        int value = Integer.parseInt(content);
        value++; // Increment

        // Write the new value back
        java.nio.file.Files.writeString(PATH_TO_COUNTER, String.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cleanUpAfterEvolutionStep() {
        try {
            java.nio.file.Files.deleteIfExists(PATH_TO_LATEST_PARAMS);
            java.nio.file.Files.deleteIfExists(PATH_TO_SERVER_OUTPUT);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static void cleanAll() {
        try {
            java.nio.file.Files.deleteIfExists(PATH_TO_LATEST_PARAMS);
            java.nio.file.Files.deleteIfExists(PATH_TO_SERVER_OUTPUT);
            java.nio.file.Files.deleteIfExists(PATH_TO_COUNTER);
            java.nio.file.Files.deleteIfExists(PATH_TO_DONE_SIGNAL);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static void initEvolution() {
        // If PATH_TO_LATEST_PARAMS does not exist, copy contents from PATH_TO_START_PARAMS
        if (!java.nio.file.Files.exists(PATH_TO_LATEST_PARAMS)) {
            try {
            java.nio.file.Files.copy(
                PATH_TO_START_PARAMS,
                PATH_TO_LATEST_PARAMS,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            } catch (java.io.IOException e) {
            e.printStackTrace();
            }
        }
        // Creating the signal that the next evo step can start
        try {
            if (!java.nio.file.Files.exists(PATH_TO_DONE_SIGNAL)) {
                java.nio.file.Files.createFile(PATH_TO_DONE_SIGNAL);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        if (!java.nio.file.Files.exists(PATH_TO_COUNTER)) {
            try {
                java.nio.file.Files.writeString(PATH_TO_COUNTER, "0");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isEvolutionStepDone(){
        return java.nio.file.Files.exists(PATH_TO_DONE_SIGNAL);
    }
}
