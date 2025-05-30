import java.nio.file.Path;
import java.util.HashMap;

public class Evaluator {

    private final static Path pathToServerOutput = Path.of("res", "evaluation", "output.txt");
    private final static Path pathToUsedParams = Path.of("res", "evaluation", "latestParameters.txt");

    public static void main(String[] args) {

        callEvalScript(String.valueOf(12), String.valueOf(42), String.valueOf(16));


        String fileContent = readFile(pathToServerOutput);
        String[] results = split(extractResults(fileContent));
        HashMap<String, Integer> mappedResult = mapResults(results);


        System.out.println(mappedResult.toString());

        // callEvalScript(String.valueOf(12), String.valueOf(42), String.valueOf(16));
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

    private static String[] split(String results){
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
}
