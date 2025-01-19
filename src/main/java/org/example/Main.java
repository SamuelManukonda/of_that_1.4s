package org.example;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("moby.txt")) {

            if (inputStream == null) {
                System.out.println("File not found, please check the file name and path");
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // List to store all words
            List<String> words = new ArrayList<>();
            // Exclusions
            Set<String> excludeWords = Set.of("in", "on", "at", "he", "she", "it", "and", "or", "but", "a", "an", "the", "is", "was");

            Map<String, Integer> freq = new HashMap<>();

            // Parse the txt file to have just the words
            while ((line = bufferedReader.readLine()) != null) {
                // Regex to identify only words.
                String[] split = line.split("\\W+");
                // Build the list of words
                Arrays.stream(split).filter(it -> !it.isEmpty()).forEach(words::add);
            }

            // Convert lower and upper case as same and does not have exclusions.
            Set<String> allWordsInFileWithExclusions = words.stream().filter(it -> !excludeWords.contains(it)).map(String::toLowerCase).collect(Collectors.toSet());

            // 50 Unique words in document
            List<String> uniqueWordsWithExclusions = getUniqueWordsWithExclusions(allWordsInFileWithExclusions);

            // Frequency of each word
            getWordFrequency(words, excludeWords, freq);

            // Top 5 words
            LinkedHashMap<String, Integer> wordsSorted = freq.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(5)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


            // Output
            System.out.println("Total number of words in the file are " + allWordsInFileWithExclusions.size());
            System.out.println("Unique words in the document are " + uniqueWordsWithExclusions);
            System.out.println("Top 5 words in the file are ");
            wordsSorted.forEach((key, value) -> System.out.println("Word \"" + key + "\" count is " + value));

            long end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getWordFrequency(List<String> words, Set<String> excludeWords, Map<String, Integer> freq) {
        words.stream()
                .filter(it -> !excludeWords.contains(it))
                .filter(Main::isNonNumericalWithOrdinalCheck)
                .collect(Collectors.toList())
                .forEach(it -> {
                            if (freq.containsKey(it)) {
                                freq.put(it, freq.get(it) + 1);
                            } else {
                                freq.put(it, 1);
                            }
                        }
                );
    }

    private static List<String> getUniqueWordsWithExclusions(Set<String> allWordsInFileWithExclusions) {
        return allWordsInFileWithExclusions.stream()
                .filter(Main::isNonNumericalWithOrdinalCheck)
                .sorted()
                .distinct()
                .limit(50)
                .collect(Collectors.toList());
    }

    private static boolean isNonNumericalWithOrdinalCheck(String input) {
        try {
            // Regular expression for ordinals of number
            if (input.matches("\\d+(st|nd|rd|th|ST|ND|RD|TH)$")) {
                return false;
            } else {
                Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}