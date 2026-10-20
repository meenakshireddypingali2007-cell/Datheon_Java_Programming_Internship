import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads a text file and reports basic word and sentence statistics. */
public class WordAnalyzer {
    private static final Pattern SENTENCE_ENDING = Pattern.compile("[.!?]+(?=\\s|$)");
    private static final Pattern NON_WORD_CHARACTERS = Pattern.compile("[^\\p{L}\\p{N}\\s]");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.print("Enter the path to a UTF-8 text file: ");
            if (!scanner.hasNextLine()) {
                System.out.println("No file path was provided.");
                return;
            }

            String enteredPath = scanner.nextLine().trim();
            if (enteredPath.isEmpty()) {
                System.out.println("File path cannot be empty.");
                return;
            }

            Path filePath;
            try {
                filePath = Path.of(enteredPath);
            } catch (RuntimeException exception) {
                System.out.println("Invalid file path: " + exception.getMessage());
                return;
            }

            try {
                String text = Files.readString(filePath, StandardCharsets.UTF_8);
                analyze(text, filePath);
            } catch (java.nio.file.NoSuchFileException exception) {
                System.out.println("File not found: " + filePath);
            } catch (IOException exception) {
                System.out.println("Could not read the file: " + exception.getMessage());
            } catch (SecurityException exception) {
                System.out.println("Permission denied while reading the file: " + filePath);
            }
        }
    }

    private static void analyze(String text, Path filePath) {
        String lowercaseText = text.toLowerCase(java.util.Locale.ROOT);
        String cleanedText = NON_WORD_CHARACTERS.matcher(lowercaseText).replaceAll(" ");
        String trimmedText = cleanedText.trim();
        String[] words = trimmedText.isEmpty() ? new String[0] : trimmedText.split("\\s+");

        HashMap<String, Integer> wordFrequencies = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                wordFrequencies.merge(word, 1, Integer::sum);
            }
        }

        int sentenceCount = countSentences(text);
        double averageSentenceLength = sentenceCount == 0 ? 0.0 : (double) words.length / sentenceCount;

        System.out.println("\n========================================");
        System.out.println("             TEXT ANALYSIS");
        System.out.println("========================================");
        System.out.println("File:                 " + filePath);
        System.out.printf("Total words:          %d%n", words.length);
        System.out.printf("Unique words:         %d%n", wordFrequencies.size());
        System.out.printf("Sentences:            %d%n", sentenceCount);
        if (sentenceCount > 0) {
            System.out.printf("Average sentence size: %.2f words%n", averageSentenceLength);
        } else {
            System.out.println("Average sentence size: N/A (no sentences detected)");
        }

        if (words.length == 0) {
            System.out.println("\nThe input contains no words.");
        }
        if (sentenceCount == 0) {
            System.out.println("The input contains no sentences.");
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordFrequencies.entrySet());
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));

        System.out.println("\nTop 10 words");
        System.out.println("----------------------------------------");
        System.out.printf("%-5s %-22s %s%n", "Rank", "Word", "Frequency");
        System.out.println("----------------------------------------");
        int limit = Math.min(10, sortedEntries.size());
        for (int index = 0; index < limit; index++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(index);
            System.out.printf("%-5d %-22s %d%n", index + 1, entry.getKey(), entry.getValue());
        }
        if (limit == 0) {
            System.out.println("(no words to rank)");
        }
        System.out.println("========================================");
    }

    /** Counts groups of sentence-ending punctuation followed by whitespace or end of input. */
    private static int countSentences(String text) {
        Matcher matcher = SENTENCE_ENDING.matcher(text);
        int sentenceCount = 0;
        while (matcher.find()) {
            sentenceCount++;
        }
        return sentenceCount;
    }
}
