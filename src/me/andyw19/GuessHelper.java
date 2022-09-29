package me.andyw19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static me.andyw19.Main.*;

public class GuessHelper {

    // Word lists
    private ArrayList<String> possibleWords = new ArrayList<>();
    private ArrayList<String> removeList = new ArrayList<>();

    // Char lists
    private ArrayList<Character> foundLetters = new ArrayList<>();
    private ArrayList<Character> knownLetters = new ArrayList<>();
    private ArrayList<Character> bannedChars = new ArrayList<>();

    private HashMap<Integer, ArrayList<Character>> notAtPos = new HashMap<Integer, ArrayList<Character>>();

    int numMandatory = 0;

    boolean hasFoundAtPos;
    boolean hasFoundInWord;
    boolean hasFoundBeginning;

    public GuessHelper() {
        // Placeholders
        for (int i = 0; i < 5; i++) {
            ArrayList<Character> tempArray = new ArrayList<Character>();
            notAtPos.put(i, tempArray);
            foundLetters.add('0');

        }
    }

    public String initalGuess() {
        return goodOpeningWords[0];
    }

    public String narrowList(int rowInc, String guess, String guessResult) {
        // Counter for number of mandatory letters found
        int tempMandatory = 0;
        boolean banned = false;
        removeList.clear();
        System.out.println(guessResult);

        if (rowInc == 0) {
            calcNewResults(guess, guessResult);

            for (char c : bannedChars) {
                System.out.println("Banned char: " + c);
            }

            // Found at least 1 char
            if (hasFoundInWord || hasFoundAtPos) {

                System.out.println(foundLetters);
                System.out.println(knownLetters);
                // Found first letter
                if (foundLetters.get(0) != '0') {
                    hasFoundBeginning = true;
                    char c = foundLetters.get(0);
                    // Grabs index of first instance of the letter in the main array
                    System.out.println(foundLetters.get(0));
                    System.out.println(firstInstanceIndex.get(c));
                    int startIndex = firstInstanceIndex.get(c);

                    boolean canLoop = true;
                    while (canLoop) {
                        banned = false;
                        tempMandatory = 1;
                        String word = fullWordArray.get(startIndex).toUpperCase().trim();
                        // If word doesn't contain any chars that aren't in the actual word
                        // Then add to the ArrayList
                        for (int i = 1; i < 5; i++) {
                            // Must not contain banned letters
                            if (bannedChars.contains(word.charAt(i))) {
                                System.out.println(word + " has been banned, banned char: " + word.charAt(i));
                                banned = true;
                                break;
                            } else {
                                if (guessResult.charAt(i) == '2') {
                                    if (word.charAt(i) == foundLetters.get(i)) {
                                        System.out.println("Found mandatory at pos: " + i + " for: " + word);
                                        tempMandatory++;
                                        // Don't add to possible
                                    }
                                }
                            }
                        }
                        // If its not banned yet, check if contain all known chars
                        if (!banned) {
                            // Check if contains all the known chars
                            banned = missingAllKnownChars(word);

                            // Add if not banned and meets all the mandatory requirements
                            System.out.println("Is banned? " + banned + " " + tempMandatory);
                            if (!banned && (tempMandatory == numMandatory)) {
                                if (!guess.equalsIgnoreCase(word)) {
                                    possibleWords.add(word);
                                }

                            }
                        }

                        startIndex++;
                        if (fullWordArray.get(startIndex).toUpperCase().charAt(0) != c) {
                            canLoop = false;
                        }


                    }
                } else {

                    // Char somewhere, not first
                    System.out.println("Contains at least 1 known char somewhere");
                    for (String str : fullWordArray) {
                        str = str.toUpperCase().trim();
                        // Contains all know chars and no banned chars
                        if (!missingAllKnownChars(str) && !containsBannedChar(str)) {
                            possibleWords.add(str);
                        }
                    }
                }


            } else {
                // No known chars
                for (String str : fullWordArray) {
                    if(!containsBannedChar(str)) {
                        possibleWords.add(str);
                    }
                }
            }
            possibleWords.remove(goodOpeningWords[0]);

        } else {
            // After first guess
            calcNewResults(guess, guessResult);
            System.out.println("New guess");

                System.out.println(possibleWords.toArray().length);


                for (String word : possibleWords) {
                    tempMandatory = 0;
                    // Check if contains all the known chars
                    if (missingAllKnownChars(word)) {
                        System.out.println(word + " was removed, missing at least 1 char");
                        System.out.println(Arrays.toString(knownLetters.toArray()));
                        removeList.add(word);
                        continue;
                    }
                    for (int i = 0; i < 5; i++) {

                        if (i == 0 && hasFoundBeginning) {
                            if (word.charAt(0) != foundLetters.get(0)) {
                                removeList.add(word);
                                System.out.println("Not same beginning char");
                                break;
                            }
                        }

                        // Must not contain banned letters
                        if (bannedChars.contains(word.charAt(i))) {
                            System.out.println(word + " has been banned, banned char: " + word.charAt(i));
                            removeList.add(word);
                            break;
                        } else {
                            if (foundLetters.get(i) != 0) {
                                if (word.charAt(i) == foundLetters.get(i)) {
                                    System.out.println("Found mandatory at pos: " + i + " for: " + word);
                                    tempMandatory++;
                                    // Don't add to possible
                                }
                            }
                        }
                    }

                    if (tempMandatory < numMandatory) {
                        removeList.add(word);
                        System.out.println("Removed " + word + " - temp != mandatory: " + tempMandatory + " " + numMandatory);
                    }
                }
        }
        possibleWords.removeAll(removeList);

        for (String str : possibleWords) {
            System.out.println(str);
        }

        if (possibleWords.contains(guess)) {
            possibleWords.remove(guess);
        }

        System.out.println("Possible words: " + possibleWords.toArray().length);

        for (int i = 0; i < 5; i++) {
            for (char c : notAtPos.get(i)) {
                System.out.println("Banned pos: " + i + " Banned char: " + c);
            }
        }

        return getGuessRec();
    }

    private String getGuessRec() {
        int len = possibleWords.toArray().length;
        Random rand = new Random();
        // Grabs random string from arraylist

        return possibleWords.get(rand.nextInt(len));
    }

    public void reset() {
        possibleWords.clear();
        removeList.clear();
        bannedChars.clear();
        knownLetters.clear();

        hasFoundInWord = false;
        hasFoundAtPos = false;
        hasFoundBeginning = false;

        numMandatory = 0;

        for (int i = 0; i < 5; i++) {
            foundLetters.set(i, '0');
            notAtPos.get(i).clear();
        }
    }

    private boolean missingAllKnownChars(String word){
        // Check if all know letters exist in word
        for (int i = 0; i < knownLetters.toArray().length; i++) {
            boolean contains = false;

            for (int j = 0; j < 5; j++) {
                if (word.charAt(j) == knownLetters.get(i)) {
                    contains = true;
                    System.out.println(word + " does contain: " + knownLetters.get(i));
                    break;
                }
            }

            if (!contains) {
                return true;
            }
        }
        // Passes all tests
        return false;
    }

    private boolean containsBannedChar(String word) {
        boolean banned = false;
        word = word.toUpperCase().trim();
        for (int i = 0; i < 5; i++) {
            if (bannedChars.contains(word.charAt(i))) {
                banned = true;
                break;
            }
            // Already check this position for this char
            if (notAtPos.get(i).contains(word.charAt(i))) {
                banned = true;
                break;
            }
        }

        if (!banned) {
            return false;
        }

        return true;
    }

    private void calcNewResults(String guess, String guessResult) {
        for (int i = 0; i < 5; i++) {
            char c = guessResult.charAt(i);
            char actualChar = guess.charAt(i);
            if (c == '2' && (foundLetters.get(i) == '0')) {
                if (i == 0) {
                    hasFoundBeginning = true;
                }
                hasFoundAtPos = true;
                numMandatory++;
                foundLetters.set(i, actualChar);
                if (!knownLetters.contains(actualChar)) {
                    knownLetters.add(actualChar);
                }

            } else if (c == '1') {
                hasFoundInWord = true;
                // Add char to blacklist for this position
                if (!notAtPos.get(i).contains(actualChar)) {
                    notAtPos.get(i).add(actualChar);
                }


                if (!knownLetters.contains(actualChar)) {
                    knownLetters.add(actualChar);
                }

            } else {
                // Not in known letters or banned already
                if (!bannedChars.contains(actualChar) && !knownLetters.contains(actualChar)) {
                    bannedChars.add(actualChar);
                }
                // Letter has been found at a pos but does not exist at the position
                // Word is SNAKE. If 'S' is at pos[0] and user guesses SANDS.
                // Result is 21100, there is only 1 'S', therefore the 'S' at pos[4] does not exist. Eg, 0
                if (knownLetters.contains(actualChar)) {
                    if (!notAtPos.get(i).contains(actualChar)) {
                        notAtPos.get(i).add(actualChar);
                    }

                }
            }
        }
    }
}