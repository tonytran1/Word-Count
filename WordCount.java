import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
*   Word Count
*   @author Tony Tran
*
*   This program reads UTF-8 text files from multiple directories and
*   prints out the most frequently used words.
*
*   If the path is a file then process the file normally (Read lines and count words).
*   If the path is a directory: all files within that directory, including files
*   within subdirectories will be processed.
*
*   This program was tested and compiled using Java 8.
*
*   This program sets the absolute path to the directory of where this program is ran.
*   Therefore, it was intended to use test files in the same or close location to the project.
*
*   This program has a minimum of two arguments to run.
*   The first argument is the requested number of words to be printed out.
*
*   The next argument, and any other arbritary sequential number of arguments following the first,
*   are the directories or file names of where the user wants the program to read.
*
*   Each word will be printed out along with the number of times the word showed up
*   from highest to lowest across all files read.
*
*   Please refer to README.md for instructions and an example on how to run.
*/
class WordCount {
  /* Class Variables */
  private static Map<String, Integer> wordCount = new HashMap<String, Integer>();
  private static int numberOfWords;

  /**
  *  Program Initialization, grabs user inputted information from command line arguments.
  *  Passes the path arguments to start retrieving files.
  *  When computation is complete, print result.
  */
  public static void main(String[] args) {
    try {
      numberOfWords = Integer.parseInt(args[0]);
      retrieveFiles(args);
      printResult();
    } catch (Exception exception) {
      System.out.println("Please enter a valid input.\nExample:");
      System.out.println("java WordCount 5 /test/ /test-two/new.txt");
    }
  }

  /**
  *  Traverses through all user inputted paths to begin
  *  searching for files.
  *
  *  Sets the absolute path to the directory of where the program is ran
  *  unless the user already specified so.
  *
  *  @param paths String array containing each path the user inputs.
  */
  public static void retrieveFiles(String[] paths) {
    for (int i = 1; i < paths.length; i++) {
      if (paths[i].charAt(0) == '/') {
        paths[i] = "." + paths[i];
      }
      searchDirectory(paths[i]);
    }
  }

  /**
  *  Tries to process the path as a directory, otherwise catch and
  *  process the path as a file.
  *
  *  @param path One of the paths the user inputted.
  */
  private static void searchDirectory(String path) {
    try {
      Files.walk(Paths.get(path))
        .filter(Files::isRegularFile)
        .parallel()
        .forEach(WordCount::processFilePath);
    } catch (Exception e) {
      processFilePath(path);
    }
  }

  /**
  *  Processes the file path by opening up a reader and then
  *  initiates processing the file for word counting.
  *
  *  Generic method used to take into account different formats of the file path.
  *  (java.lang.String or java.nio.file.Path)
  *
  *  @param path One of the paths the user inputted.
  */
  private static <T> void processFilePath(T path) {
    try {
      File file = obtainFile(path.toString());
      BufferedReader reader = new BufferedReader(new FileReader(file));
      processFile(reader);
    } catch (Exception exception) {
      System.out.println("Error parsing path.");
      System.exit(1);
    }
  }

  /**
  *  Validates the file exists before continuing to process the file.
  *
  *  @param fileName full file name/path of the file.
  *  @return file Validated file ready to be read.
  */
  private static File obtainFile(String fileName) {
    File file = new File(fileName);
    try {
      if (!file.isFile())
        throw new IOException("File/Directory does not exist");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Please enter a valid input.\nExample:");
      System.out.println("java WordCount 5 /test/ /test-two/new.txt");
      System.exit(1);
    }
    return file;
  }

  /**
  *  Reads the file while counting the words line by line
  *  until the file is completely read.
  *
  *  @param reader The opened and declared buffered reader for the file to be processed.
  */
  private static void processFile(BufferedReader reader) {
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        countWords(line);
      }
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
  *  Counts the number of words from the given string.
  *  Parses the result to remove some punctuation.
  *
  *  Assumption: A word can have letters, a apostrophe ('), and/or a hyphen (-)
  *
  *  @param content a string containing words.
  */
  private static void countWords(String content) {
    String[] words = content.split("\\s+");
    for (String word : words) {
      word = word.toLowerCase().replaceAll("[^a-zA-Z-']", "");
      if (word.isEmpty()) { continue; } // Skip. Edge case after removing punctuation.
      incrementCount(word);
    }
  }

  /**
  *  Simple helper function to increment the word in the map count.
  *
  *  @param word The word that will have its count incremented
  */
  private static void incrementCount(String word) {
    if (wordCount.get(word) == null) {
      wordCount.put(word, 0);
    }
    wordCount.put(word, wordCount.get(word) + 1);
  }

  /**
  *  Uses entry set to sort map by highest value, limiting the number of
  *  results to the number requested by the user, and then sends
  *  each entry to be formated by the formatResult method.
  */
  public static void printResult() {
    wordCount.entrySet().stream()
      .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
      .limit(numberOfWords)
      .forEach(entry -> { formatResult(entry); });
  }

  /* Formats the results to be easily read */
  private static void formatResult(Map.Entry<String, Integer> entry) {
    String result = "Word '<" + entry.getKey() + ">' occured <" + entry.getValue() + "> times";
    System.out.println(result);
  }
}
