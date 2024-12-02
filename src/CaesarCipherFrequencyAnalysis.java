/** Project: Voynich Manuscript
 * Purpose Details: Voynich Manuscript decryption
 * Course: IST242
 * Author: Ryan Zheng
 * Date Developed: 11/28/24
 * Last Date Changed: 12/1/24
 * Rev: 2
 * using latin/spanish/italian frequencies analysis for translation
 */
import java.io.*;
import java.util.*;


/**
 * code was base on professor joe oakes CaesarCipher Frequency Analysis
 * spanish frequency source: https://www.sttmedia.com/characterfrequency-spanish
 * italian frequency source: https://www.sttmedia.com/characterfrequency-italian
 * Latin frequency source: https://www.sttmedia.com/characterfrequency-latin
 */
public class CaesarCipherFrequencyAnalysis {

    // Combined letter frequencies for Latin, Spanish, and Italian
    private static final double[] COMBINED_FREQUENCIES = {
            10.8, 1.3, 4.0, 4.8, 12.7, 0.7, 1.6, 0.7, 8.1, 0.2,
            0.0, 5.1, 2.7, 6.7, 8.7, 2.5, 0.5, 6.1, 6.4, 6.4,
            3.0, 1.4, 0.0, 0.1, 0.4, 0.6
    };

    public static void main(String[] args) throws IOException {
        //  file path
        String filePath = "src/ciphertext.txt";

        // read  the file
        String ciphertext = readFile(filePath);

        // decrypt using frequency analysis
        String bestGuess = decryptUsingFrequencyAnalysis(ciphertext);

        // print out the possible output the decrypted text
        System.out.println("Decrypted text (best guess): " + bestGuess);
    }



    // Method to read the contents of a file into a String
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }

        return content.toString().trim();
    }

    // Method to decrypt using frequency analysis
    public static String decryptUsingFrequencyAnalysis(String ciphertext) {
        String bestDecryption = "";
        double lowestChiSquare = Double.MAX_VALUE;

        for (int shift = 0; shift < 26; shift++) {
            String decryptedText = decryptWithShift(ciphertext, shift);
            double chiSquare = calculateChiSquare(decryptedText);

            if (chiSquare < lowestChiSquare) {
                lowestChiSquare = chiSquare;
                bestDecryption = decryptedText;
            }
        }

        return bestDecryption;
    }

    // Method to decrypt text with a specific shift
    public static String decryptWithShift(String text, int shift) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                decryptedText.append((char) ((c - base - shift + 26) % 26 + base));
            } else {
                decryptedText.append(c); // Keep non-alphabetic characters unchanged
            }
        }

        return decryptedText.toString();
    }

    // Method to calculate Chi-Square statistic for frequency analysis
    public static double calculateChiSquare(String text) {
        int[] letterCounts = new int[26];
        int totalLetters = 0;

        // Count letter occurrences
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char lowerCaseChar = Character.toLowerCase(c);
                letterCounts[lowerCaseChar - 'a']++;
                totalLetters++;
            }
        }

        // Calculate Chi-Square statistic
        double chiSquare = 0.0;

        for (int i = 0; i < 26; i++) {
            double observed = letterCounts[i];
            double expected = totalLetters * COMBINED_FREQUENCIES[i] / 100;
            if (expected != 0) { // Avoid division by zero
                chiSquare += Math.pow(observed - expected, 2) / expected;
            }
        }

        return chiSquare;
    }
}
