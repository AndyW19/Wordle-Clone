package me.andyw19;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static me.andyw19.Main.*;
import static me.andyw19.Main.fullWordArray;

public class Main extends JPanel {

    public static final Dictionary dict = new Dictionary("words.txt");
    public static final Dictionary allowable = new Dictionary("allowable.txt");
    public static final ArrayList<String> fullWordArray = new ArrayList<>();
    private static final int windowXLength = 800;
    private static final int windowYLength = 500;

    public static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    // Keeps record of the first index instance of each letter in the main dictionary array
    public static HashMap<Character, Integer> firstInstanceIndex = new HashMap<>();

    public static String [] goodOpeningWords = {"SOARE", "CRANE", "SHIRE", "ORBIT", "QUIRK", "FLAKY"};

    public static WordleWindow window;


    public static void main(String[] args) {

        generateWordArray();
        

        window = new WordleWindow(windowXLength, windowYLength);
    }

    public static String getRandWord() {
        return dict.getWord(getRand(0, dict.getSize()));
    }

    public static int getRand(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }


    // Generate alphabetically sorted array of both words.txt and allowable.txt
    private static void generateWordArray() {

        // Incrementer for both arrays
        int mainInc = 0;
        int allowInc = 0;

        // Local reference to both Dictionary arrays
        String [] mainArray = dict.getArray();
        String [] allowableArray = allowable.getArray();
        char currentLetter = 0;
        
        for (int i = 0; i < (dict.getSize() + allowable.getSize()); i++) {

            // If checked all words.txt, remaining allowable is in order
            if (mainInc >= dict.getSize()) {
                fullWordArray.add(allowableArray[allowInc]);
                allowInc++;
                continue;
            }

            // Compare the 2 word arrays, if returns less than 0, mainArray[mainInc]
            // is alphabetically smaller than allowableArray[allowInc]
            // Both arrays have no overlapping words

            if (i== 0) {
                currentLetter = 'A';
                firstInstanceIndex.put(currentLetter, 0);
            }


            if (mainArray[mainInc].trim().compareTo(allowableArray[allowInc].trim()) < 0) {
                fullWordArray.add(mainArray[mainInc].toUpperCase().trim());
                if (fullWordArray.get(i).charAt(0) != currentLetter) {
                    currentLetter = fullWordArray.get(i).charAt(0);
                    firstInstanceIndex.put(currentLetter, i);
                }
                mainInc++;
            } else {
                fullWordArray.add(allowableArray[allowInc].toUpperCase().trim());

                if (fullWordArray.get(i).charAt(0) != currentLetter) {
                    currentLetter = fullWordArray.get(i).charAt(0);
                    firstInstanceIndex.put(currentLetter, i);
                }

                allowInc++;
            }
            
        }

        for (int i = 0; i < 26; i++) {
            System.out.println(alphabet[i] + " " + firstInstanceIndex.get(alphabet[i]));
        }

    }
}

class Dictionary{

    private String input[];

    public Dictionary(String path){
        input = load("src/me/andyw19/" + path);
    }

    public int getSize(){
        return input.length;
    }

    public String [] getArray() {
        return input;
    }

    public String getWord(int n){
        return input[n];
    }

    private String[] load(String file) {
        File aFile = new File(file);
        StringBuffer contents = new StringBuffer();
        BufferedReader input = null;
        try {
            input = new BufferedReader( new FileReader(aFile) );
            String line = null;
            int i = 0;
            while (( line = input.readLine()) != null){
                contents.append(line);
                i++;
                contents.append(System.getProperty("line.separator"));
            }
        }catch (FileNotFoundException ex){
            System.out.println("Can't find the file - are you sure the file is in this location: "+file);
            ex.printStackTrace();
        }catch (IOException ex){
            System.out.println("Input output exception while processing file");
            ex.printStackTrace();
        }finally{
            try {
                if (input!= null) {
                    input.close();
                }
            }catch (IOException ex){
                System.out.println("Input output exception while processing file");
                ex.printStackTrace();
            }
        }
        String[] array = contents.toString().split("\n");
        for(String s: array){
            s.trim();
        }
        return array;
    }
}

class WordleWindow {

    // UI Objects
    public JFrame jFrame;
    public JPanel panel;
    public JTextField input;
    public JTextField endOfGame;
    public JTextField invalidWord;
    public JTextField recommendedGuess;
    public Button playAgain;
    public Button button;
    TextSquare textArea[][];

    public GuessHelper helper;

    private Font font1 = new Font("SansSerif", Font.BOLD, 20);

    public String actualWord;
    public String guessWord;

    private int rowIncrementer = 0;

    private boolean won;
    private boolean invalidExists;
    private boolean debug = false;

    private final Color emptyColor = new Color(46, 63, 71);
    private final Color textInputCol = new Color(100, 100, 100);

    // Helper params
    private ArrayList<String> helperArray = new ArrayList<>();
    private boolean foundFirstLetter = false;

    public WordleWindow(int x, int y) {
        jFrame = new JFrame();
        jFrame.setBounds(0, 0, x, y);
        createPanel();
        jFrame.setSize(x, y);
        jFrame.setTitle("Wordle Clone");
        jFrame.setVisible(true);
        helper = new GuessHelper();
        if (debug) {
            Scanner sc = new Scanner(System.in);
            actualWord = sc.nextLine().toUpperCase().trim();
            sc.close();
        } else {
            actualWord = getRandWord().toUpperCase();
        }

        System.out.println(actualWord);
    }

    // Construct the UI
    private void createPanel() {
        panel = new JPanel();
        panel.setBackground(emptyColor);
        panel.setBounds(0, 0, 400, 400);
        panel.setSize(400, 400);
        panel.setMaximumSize(new Dimension(400, 400));

        textArea = new TextSquare[5][6];

        // Construct the text grid
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 5; x++) {
                textArea[x][y] = new TextSquare(jFrame);
                JTextField text = textArea[x][y].getTextField();
                text.setEditable(false);
                // 70 * 70 square with 10px gap between each square
                text.setBounds((x * 75) + 5, (y * 75) + 5, 70, 70);
                text.setBackground(emptyColor);
                text.setText("");
                text.setHorizontalAlignment(JTextField.CENTER);
                panel.add(text);
            }
        }

        Font font1 = new Font("SansSerif", Font.BOLD, 30);

        // Recommended Guess
        recommendedGuess = new JTextField();
        recommendedGuess.setEditable(false);
        recommendedGuess.setBounds(400, 400, 300, 50);
        recommendedGuess.setBackground(textInputCol);
        recommendedGuess.setHorizontalAlignment(JTextField.CENTER);
        recommendedGuess.setFont(new Font("SansSerif", Font.BOLD, 20));
        // Pick opening word
        recommendedGuess.setText("Recommended word: " + goodOpeningWords[0]);

        // Input text box
        input = new JTextField();
        input.setEditable(true);
        input.setBounds(400, 300, 150, 70);
        input.setFont(font1);
        input.setBackground(textInputCol);

        // Button to try guess
        button = new Button();
        button.setBounds(600, 300, 150, 70);
        button.setBackground(textInputCol);
        button.setLabel("Click to guess");
        // Listener that fires when button is pressed
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                // Get guess word and check if acceptable
                guessWord = input.getText().toUpperCase().trim();
                boolean allowable = binarySearch();

                // If not acceptable
                if (guessWord.length() != 5 || !allowable) {
                    invalidWord();
                    return;
                }

                // If last guess was invalid, reset invalid text
                if (invalidExists) {
                    invalidWord.setText("");
                }

                // Reset input text
                input.setText("");

                // Return if reached last row
                if (rowIncrementer == 6) {
                    return;
                }

                // Update the row with the guess
                UpdateRow(rowIncrementer, guessWord);
                rowIncrementer++;

                // Check if reached end of the game
                if (rowIncrementer == 6 || won) {
                    System.out.println("Word was : " + actualWord);
                    GameEnd();
                }
            }
        });

        // Add elements to the panel and set visible
        panel.add(input);
        panel.add(button);
        panel.add(recommendedGuess);

        panel.setLayout(null);
        jFrame.add(panel);
        panel.setVisible(true);
    }

    // Takes in the row and guess and will preform updates on each TextSquare
    public void UpdateRow(int y, String guess) {
        char[] chars = guess.toCharArray();
        boolean exists = false;
        int wordSameCount = 0;
        StringBuilder guessResult = new StringBuilder();

        // Loop through each TextSquare in the row
        for (int x = 0; x < 5; x++) {
            exists = false;
            textArea[x][y].setSquareText(chars[x]);
            // Char exists at the TextSquare
            if (actualWord.charAt(x) == chars[x]) {
                textArea[x][y].setStatus(TextSquare.SquareStatus.ExistsAtPos);
                wordSameCount++;
                guessResult.append("2");
                System.out.println("Found 2 at Pos: " + x);
            } else {
                // Check if the char exists else where in the word
                for (int i = 0; i < 5; i++) {
                    if (chars[x] == actualWord.charAt(i)) {
                        // Check if the char has already been counted
                        if (textArea[i][y].getStatus() != TextSquare.SquareStatus.ExistsAtPos) {
                            textArea[x][y].setStatus(TextSquare.SquareStatus.ExistsInWord);
                            exists = true;
                            guessResult.append("1");
                            System.out.println("Found 1 at Pos: " + x);
                            break;
                        }

                    }
                }
                // If does not exist, not in word
                if (!exists) {
                    textArea[x][y].setStatus(TextSquare.SquareStatus.NotInWord);
                    guessResult.append("0");
                    System.out.println("Found 0 at Pos: " + x);
                }

            }
        }

        if (wordSameCount == 5) {
            won = true;
            return;
        }

        recommendedGuess.setText("Recommended word: " + helper.narrowList(rowIncrementer, guess, guessResult.toString()));
    }

    // Generates GameEnd UI elements
    private void GameEnd() {
        panel.remove(input);
        panel.remove(button);

        endOfGame = new JTextField();
        endOfGame.setEditable(false);
        endOfGame.setBounds(400, 150, 300, 100);
        font1 = new Font("SansSerif", Font.BOLD, 20);
        endOfGame.setFont(font1);
        endOfGame.setBackground(textInputCol);
        endOfGame.setHorizontalAlignment(JTextField.CENTER);

        playAgain = new Button();

        playAgain.setBounds(400, 300, 200, 100);
        playAgain.setFont(font1);
        playAgain.setBackground(textInputCol);
        playAgain.setLabel("Play Again?");
        playAgain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                resetRows();

            }
        });

        panel.add(endOfGame);
        panel.add(playAgain);

        if (invalidExists) {
            panel.remove(invalidWord);
            invalidExists = false;
        }

        String text;
        if (won) {
            text = "You won, word was : " + actualWord.toUpperCase();
        } else {
            text = "You lost, word was : " + actualWord.toUpperCase();
        }
        endOfGame.setText(text);

        if (invalidWord != null) {
            invalidWord.setText("");
        }


    }

    private void resetRows() {
        for (int y = 0; y < 6; y++) {

            for (int x = 0; x < 5; x++) {
                textArea[x][y].resetSquare();
            }

        }
        panel.remove(endOfGame);
        panel.remove(playAgain);

        // Resets the counter
        rowIncrementer = 0;
        if (won) won = false;
        panel.add(button);
        panel.add(input);
        helper.reset();
        actualWord = getRandWord().toUpperCase();
        recommendedGuess.setText("Recommended word: " + goodOpeningWords[0]);
    }

    // Generates Invalid Word UI object
    private void invalidWord() {
        invalidExists = true;

        if (invalidWord == null) {
            invalidWord = new JTextField();
        }

        invalidWord.setText("Word not in allowed list");
        invalidWord.setEditable(false);
        font1 = new Font("SansSerif", Font.BOLD, 20);
        invalidWord.setFont(font1);
        invalidWord.setBackground(textInputCol);
        invalidWord.setBounds(400, 20, 300, 100);
        invalidWord.setHorizontalAlignment(JTextField.CENTER);

        panel.add(invalidWord);
    }

    // Compares guess against allowable words in a binary search
    private boolean binarySearch() {
        int left = 0;
        int right = fullWordArray.toArray().length - 1;

        while (left <= right) {

            int mid = left + ((right - left) / 2);
            int compareVal = guessWord.compareTo(fullWordArray.get(mid));

            if (compareVal == 0) {
                return true;
            }

            if (compareVal > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

}
