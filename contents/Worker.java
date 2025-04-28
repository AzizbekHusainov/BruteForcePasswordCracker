import net.lingala.zip4j.core.*;
import net.lingala.zip4j.exception.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Worker extends Thread {
    private int threadNumber;
    private static Manager myManager;
    private String subset;
    private String characters;
    private int length;
    private List<String> results;
    private File file;
    private File tempFile;

    /**
     * Constructs a new Worker thread.
     * 
     * @param number the thread number.
     * @param myManager the shared Manager object.
     * @param start the subset of prefixes assigned to this thread.
     * @param characters the characters used for password generation which is the alphabet.
     * @param length the length of the password to crack.
     * @param results the shared list to store results.
     * @param file the password-protected zip file.
     */
    public Worker(int number, Manager myManager, String start, String characters, int length, List<String> results, File file) {
        this.threadNumber = number;
        this.myManager = myManager;
        this.characters = characters;
        this.length = length;
        this.results = results;
        this.file = file;
        this.subset = start;
    }

    @Override
    public void run() {
        this.tempFile = new File("temp_" + threadNumber + ".zip");
        String outputDir = "contents-" + threadNumber;
        try {
            Files.copy(file.toPath(), tempFile.toPath());
        } catch (IOException e) {
            System.err.println("Thread " + threadNumber + ": Error creating file copy: " + e.getMessage());
            return;
        }

        for (char c : subset.toCharArray()) { //Determines the starting letters to assign while making sure each thread is capable of guessing the entire alphabet
            generateCombinations(characters, String.valueOf(c), length, results, outputDir);
        }

        tempFile.delete();
        deleteDirectory(Paths.get(outputDir));
    }

    /**
     * Recursively generates all combinations of passwords and attempts to crack the zip file.
     * 
     * @param characters the characters used for password generation.
     * @param current the current password being tested.
     * @param length the target length of the password.
     * @param results the shared list to store results.
     * @param outputDir the directory to extract zip contents if the password is correct.
     */
    public void generateCombinations(String characters, String current, int length, List<String> results,
            String outputDir) {
        if (myManager.getCracked()) {
            return; // Stop if the password is already cracked
        }

        // Ai
        if (current.length() == length) {
            // Base case
            results.add(current);
            try {
                ZipFile zipFile = new ZipFile(tempFile);
                zipFile.setPassword(current.toCharArray());
                zipFile.extractAll(outputDir);
                System.out.println("Password found: " + current);
                myManager.setCracked(true);
            } catch (ZipException e) {
                // Incorrect password
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (int i = 0; i < characters.length(); i++) {
                if (myManager.getCracked()) {
                    return; // Additional check inside loop to prevent further recursive calls
                }
                // Recursive call
                generateCombinations(characters, current + characters.charAt(i), length, results, outputDir);
            }
        }
        // Ai, had help from ai to compute the three letter password. Then altered it to
        // take any length and have it be recorded in an ArrayList. Just citing in case.
    }

    /**
     * Deletes the specified directory and its contents.
     * 
     * @param directory the path of the directory to delete.
     */
    private static void deleteDirectory(Path directory) {
        try {
            Files.walk(directory)
                    .sorted((path1, path2) -> path2.compareTo(path1)) // Reverse order to delete files before
                                                                      // directories
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete " + path + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.print("");
        }
    }
}
