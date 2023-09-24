import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.awt.event.KeyEvent;
import java.io.*;

//Handles the backend logic for the JWordle game, evaluating player guesses and maintaining
//the state of the game
public class JWordleLogic{
   
   
  
   //Number of words in the provided words.txt file
   private static final int WORDS_IN_FILE = 5758;
   
   //Use for generating random numbers!
   private static final Random rand = new Random();
   
   //Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;
   
   //Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;
   
   //The null character value (used to represent an "empty" value for a spot on the game grid)
   public static final char NULL_CHAR = 0;
   
   //Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); //(Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); //(Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; //(Dark Gray [obviously])
   private static final Color DEFAULT_KEYBOARD_COLOR = new Color(160, 163, 168); //(Light Gray)
   
   //Name of file containing all the five letter words
   private static final String WORDS_FILENAME = "words.txt";
   
   //Secret word used when the game is running in debug mode
   private static final char[] DEBUG_SECRET_WORD = {'B', 'A', 'N', 'A', 'L'};
   //int secretWord = rand.nextInt(WORDS_IN_FILE);
   //private static char[] finalWord = new char[5]; 
   
   
   
   //...Feel free to add more final variables of your own!
            
   
   
   
   
   //******************   NON-FINAL GLOBAL VARIABLES   ******************
   //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ******** 
   
   
   //Array storing all words read out of the file
   private static String[] words;
   
   //The current row/col where the user left off typing
   private static int currentRow, currentCol;
      
   
   //*******************************************************************
   
   
   
   
   
   //This function gets called ONCE when the game is very first launched
   //before the user has the opportunity to do anything.
   //
   //Should return the randomly chosen "secret word" the player needs to guess
   //as a char array
   public static char[] initGame() {
      fileReader();
      if (GameLauncher.DEBUG_USE_HARDCODED_WORD){
         return DEBUG_SECRET_WORD;
      }
      int secretWord = rand.nextInt(WORDS_IN_FILE);
      String targetWord = words[secretWord];
      return targetWord.toCharArray();
   }
   
               
   
   
   //This function gets called everytime the user types a valid key on the
   //keyboard (alphabetic character, enter, or backspace) or clicks one of the
   //keys on the graphical keyboard interface.
   //
   //The key pressed is passed in as a char value.
   public static void keyPressed(char key){
      if (key == ENTER_KEY){
         if (currentCol>=5){
            if (isValid()){
               setGrid();
               currentCol=0;
               currentRow++;
            }
            else{
               JWordleGUI.wiggleGrid(currentRow);
            }
         }
         else{
            JWordleGUI.wiggleGrid(currentRow);
         }
      }
      else if (key == BACKSPACE_KEY){
         if (currentCol <= 5 && currentCol > 0){
            currentCol--;
            JWordleGUI.setGridLetter(currentRow, currentCol, NULL_CHAR);
         }
      }
      else{
         if (currentCol<5){
            JWordleGUI.setGridLetter(currentRow, currentCol, key);
            currentCol++;
         }
      }
   }
   
   public static void setGrid(){
      char[] secretWord = JWordleGUI.getSecretWord();
      int corLetters=0;
      for(int gCol=0; gCol<MAX_COLS; gCol++){
         char gridletter=JWordleGUI.getGridLetter(currentRow, gCol);
         if (gridletter==secretWord[gCol]){
            JWordleGUI.setGridColor(currentRow, gCol, CORRECT_COLOR);
            JWordleGUI.setKeyColor(gridletter, CORRECT_COLOR);
            corLetters++;
            secretWord[gCol] = NULL_CHAR;
         }
      }
      for(int gCol=0; gCol<MAX_COLS; gCol++){
         char gridletter=JWordleGUI.getGridLetter(currentRow, gCol);
         int letter_idx = containsLetter(secretWord, gridletter);
         if (letter_idx!=-1){
            JWordleGUI.setGridColor(currentRow, gCol, WRONG_PLACE_COLOR);
            secretWord[letter_idx] = NULL_CHAR;
            if(JWordleGUI.getKeyColor(gridletter)!=CORRECT_COLOR){
               JWordleGUI.setKeyColor(gridletter, WRONG_PLACE_COLOR);
            }
         }
         else if (JWordleGUI.getGridColor(currentRow, gCol)!=CORRECT_COLOR){
            JWordleGUI.setGridColor(currentRow, gCol, WRONG_COLOR);
            if(JWordleGUI.getKeyColor(gridletter)!=CORRECT_COLOR
            && (JWordleGUI.getKeyColor(gridletter)!=WRONG_PLACE_COLOR)){
               JWordleGUI.setKeyColor(gridletter, WRONG_COLOR);
            }
         }
      }
      if (corLetters==5){
         JWordleGUI.endGame(true);
      }
      else if (currentRow + 1 == 6) {
         JWordleGUI.endGame(false);
      }
   }
   
   public static int containsLetter(char[] letters, char key){
      for (int i=0; i<letters.length; i++){
         if (letters[i]==key){
            return i;
         }
      } return -1;
   }

   public static boolean isValid(){ // checks if inputted word is on the words list 
      if (GameLauncher.DEBUG_ALLOW_ANY_GUESS){
         return true;
      }
      char[] inputWord = new char[MAX_COLS];
      for (int i=0; i<MAX_COLS; i++){
         char letter = JWordleGUI.getGridLetter(currentRow, i);
         inputWord[i]=letter;
      }
      for (int i=0; i<words.length-1; i++){
         boolean b = Arrays.equals(words[i].toCharArray(), inputWord);
         if (b){
            return true;
         }
      }
      return false;
   }
   
   public static void fileReader() {
      try (
      Scanner read = new Scanner(new File(WORDS_FILENAME))) {
         words = new String[WORDS_IN_FILE]; 
         int i=0;
         String word;
         while (read.hasNextLine()){
            word = read.nextLine();
            words[i]=word.toUpperCase();
            i++;
         }
         read.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }

}
