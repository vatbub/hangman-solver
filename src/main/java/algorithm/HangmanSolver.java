package algorithm;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import common.Config;
import languages.*;
import stats.HangmanStats;

/**
 * A class that holds all methods and algorithms to solve a hangman puzzle.
 * 
 * @author frede
 *
 */
public class HangmanSolver {

	private static Language langOld;
	private static TabFile wiktDatabase;
	private static TabFile cldrDatabase;

	/**
	 * A {@link List} that contains all characters and words that the computer
	 * has guessed.
	 */
	public static List<String> proposedSolutions = new ArrayList<String>();

	private static String currentSequenceCopy;

	/**
	 * Solves a Hangman puzzle.
	 * 
	 * @param currentSequence
	 *            The current letter sequence. The sequence must be something
	 *            like this: ___a__ where the underscores _ are the unknown
	 *            letters.
	 * @param lang
	 *            The {@link Language} that the user is playing in.
	 * @return An Object that contains info about the current game and the next
	 *         guess.
	 * @see Result
	 */
	public static Result solve(String currentSequence, Language lang) {

		Result res = new Result();

		res.lang = lang;
		res.gameState = winDetector(currentSequence);
		
		currentSequenceCopy = currentSequence;;

		int counter = 0;

		if (!lang.equals(langOld) || wiktDatabase == null || cldrDatabase == null) {
			// Load language databases
			loadLanguageDatabases(lang);
		}

		// Split the pattern up in words
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(currentSequence.split(" ")));

		// Remove all words that don't contain an underscore as they are fully
		// solved
		int indexCorr = 0;
		for (int i = 0; i < words.size(); i++) {
			if (!words.get(i - indexCorr).contains("_")) {
				if (!words.get(i - indexCorr).equals("")) {
					// Submit the word to the internet db.
					// Although this method is called quite often, it keeps
					// track of
					// the submissions to avoid duplicates.
					HangmanStats.addWordToDatabase(words.get(i - indexCorr), lang);
				}
				words.remove(i - indexCorr);
				indexCorr = indexCorr + 1;
			}
		}

		// Go through all words
		for (String word : words) {
			if (counter == 2) {
				System.out.println("Stopping...");
			}
			System.out.println("counter = " + counter);
			System.out.println("word = " + word);
			counter++;
			
			// Get all words from the database with equal length
			List<String> wordsWithEqualLength = wiktDatabase.getValuesWithLength(2, word.length());
			wordsWithEqualLength.addAll(cldrDatabase.getValuesWithLength(2, word.length()));

			// Check if there are words that match 90%
			String bestWordWikt = wiktDatabase.getValueWithHighestCorrelation(2, word, proposedSolutions);
			String bestWordCldr = cldrDatabase.getValueWithHighestCorrelation(2, word, proposedSolutions);
			boolean foundBestWord = true;

			System.out.println(bestWordWikt);
			System.out.println(bestWordCldr);

			if (bestWordWikt.length() == 0 && bestWordCldr.length() == 0) {
				// dictionaries are both used up
				System.out.println("dictionaries used up");
				foundBestWord = false;
			} else if (bestWordWikt.equals("")) {
				res.bestWord = bestWordCldr;
			} else if (bestWordCldr.equals("")) {
				res.bestWord = bestWordWikt;
			} else if (TabFile.stringCorrelation(word, bestWordWikt) >= TabFile.stringCorrelation(word, bestWordCldr)) {
				res.bestWord = bestWordWikt;
			} else {
				res.bestWord = bestWordCldr;
			}

			if (foundBestWord) {
				res.bestWordScore = TabFile.stringCorrelation(word, res.bestWord);

				System.out.println(res.bestWordScore);

				if (res.bestWordScore >= Config.thresholdToSelectWord(word.length())) {
					proposedSolutions.add(res.bestWord);
					res.result = res.bestWord;
					return res;
				}

				// apparently no direct match, get the most used letter from the
				// results

				// Get all chars from the bestWord
				char[] priorityChars = res.bestWord.toCharArray();

				res.result = Character.toString(getMostFrequentChar(wordsWithEqualLength, priorityChars));
			} else {
				// No bestWord found
				System.out.println("No best word found, using most common chars only");
				res.result = Character.toString(getMostFrequentChar(wordsWithEqualLength));
			}
			proposedSolutions.add(res.result);
			return res;

		}

		res.result = "";
		return res;
	}

	/**
	 * Loads a language database for processing.
	 * 
	 * @param lang
	 *            The {@link Language} to load.
	 */
	private static void loadLanguageDatabases(Language lang) {
		try {
			System.out.println("Loading language databases for " + lang.getHumanReadableName());
			wiktDatabase = new TabFile(lang.getWiktName());
			cldrDatabase = new TabFile(lang.getCldrName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the most frequent char in the given word list.
	 * 
	 * @param words
	 *            The list for which the most frequent char will be determined.
	 * @return The most frequent char.
	 */
	private static char getMostFrequentChar(List<String> words) {
		return getMostFrequentChar(words, new char[0]);
	}

	/**
	 * Returns the most frequent char in the given word list.
	 * 
	 * @param words
	 *            The list for which the most frequent char will be determined.
	 * @param priorityChars
	 *            The ranking of most frequent chars will be filtered so that it
	 *            only contains priorityChars that were not yet proposed. If the
	 *            ranking is empty after filtering, no filter is appliyed and
	 *            the method acts like {@code getMostFrequentChar(words)}
	 * @return The most frequent char.
	 */
	private static char getMostFrequentChar(List<String> words, char[] priorityChars) {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AtomicInteger currentIndex = new AtomicInteger(0);
		List<AtomicInteger> charCounts = new ArrayList<AtomicInteger>();

		for (int i = 0; i < 26; i++) {
			AtomicInteger temp = new AtomicInteger(0);
			charCounts.add(temp);
		}

		System.out.println("Size: " + words.size());

		for (int i = 0; i < Config.parallelThreadCount; i++) {
			threads.add(new Thread() {
				@Override
				public void run() {
					int index = currentIndex.getAndIncrement();
					while (index < words.size()) {
						int[] counts = countAllCharsInString(words.get(index));

						for (int i = 0; i < 26; i++) {
							charCounts.get(i).set(charCounts.get(i).get() + counts[i]);
						}

						// Grab the next index
						index = currentIndex.getAndIncrement();
					}
				}
			});
			threads.get(i).start();
		}

		// Wait for threads
		for (int i = 0; i < Config.parallelThreadCount; i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Find max count
		int maxCount = -1;
		int maxIndex = 0;

		if (priorityChars.length != 0) {
			// priority chars specified

			// convert priorityChars to lower case
			for (int j = 0; j < priorityChars.length; j++) {
				// convert to lower case
				priorityChars[j] = Character.toLowerCase(priorityChars[j]);
			}

			for (int i = 0; i < 26; i++) {
				if (charCounts.get(i).get() > maxCount
						&& (!proposedSolutions.contains(Character.toString((char) ('A' + i))))
						&& charArrayContais(priorityChars, (char) ('a' + i))) {
					maxIndex = i;
					maxCount = charCounts.get(i).get();
				}
			}
		}

		if (priorityChars.length == 0 || maxCount == -1) {
			// No priorityChars specified or all priority chars already proposed

			for (int i = 0; i < 26; i++) {
				if (charCounts.get(i).get() > maxCount
						&& !proposedSolutions.contains(Character.toString((char) ('A' + i)))) {
					maxIndex = i;
					maxCount = charCounts.get(i).get();
				}
			}
		}

		return (char) ('A' + maxIndex);
	}

	/**
	 * Checks if the specified array contains the specified char.
	 * 
	 * @param array
	 *            The array to be checked.
	 * @param value
	 *            The char to look for.
	 * @return {@code true} if the char can be found in the array, {@code false}
	 *         otherwise.
	 */
	private static boolean charArrayContais(char[] array, char value) {
		for (char c : array) {
			if (c == value) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Counts how often each char appears in the given {@link String}
	 * 
	 * @param str
	 *            The string of which the chars shall be counted.
	 * @return an array where the 0-position shows
	 */
	private static int[] countAllCharsInString(String str) {
		int[] res = new int[26];

		for (int i = 0; i < 26; i++) {
			res[i] = countCharInString(str, (char) ('A' + i)) + countCharInString(str, (char) ('a' + i));
		}

		return res;
	}

	/**
	 * Counts, how often the specified char appears in the given string.
	 * 
	 * @param str
	 *            The {@link String} to be looked through
	 * @param chr
	 *            The {@code char} to be counted.
	 * @return The number of appearances of {@code chr} in {@code str}
	 */
	private static int countCharInString(String str, char chr) {
		int res = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == chr) {
				res = res + 1;
			}
		}

		return res;
	}

	/**
	 * Checks if the given word contains a char that is proven to be wrong.
	 * 
	 * @param word
	 *            The word to be checked.
	 * @return {@code true} if the word contains a wrong char, {@code false}
	 *         otherwise.
	 */
	public static boolean wordContainsWrongChar(String word) {

		char[] chars = word.toCharArray();
		
		for (char chr : chars) {
			if (!currentSequenceCopy.toUpperCase().contains(Character.toString(Character.toUpperCase(chr)))
					&& proposedSolutions.contains(Character.toString(Character.toUpperCase(chr)))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the number of wrong guesses done so far.
	 * 
	 * @return The number of wrong guesses done so far.
	 */
	public static int getWrongGuessCount() {
		List<String> wrongSolutions = new ArrayList<String>();
		for (String solution : proposedSolutions) {
			if (wordContainsWrongChar(solution)) {
				wrongSolutions.add(solution);
			}
		}

		// -1 to exclude the last guess (as the user did not respond to it yet
		// if it is wrong or not)
		return wrongSolutions.size() - 1;
	}

	/**
	 * Checks if the computer has won or lost the game or the game is still running
	 * @param currentSequence The current letter sequence.
	 * @return The current {@link GameState}
	 */
	public static GameState winDetector(String currentSequence) {
		// Split the pattern up in words
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(currentSequence.split(" ")));

		// Remove all words that don't contain an underscore as they are fully
		// solved
		int indexCorr = 0;
		for (int i = 0; i < words.size(); i++) {
			if (!words.get(i - indexCorr).contains("_")) {
				words.remove(i - indexCorr);
				indexCorr = indexCorr + 1;
			}
		}
		
		// If all words are solved, the list is now empty and the computer hhas won the game.
		if (words.size()==0){
			return GameState.GAME_WON;
		}
		
		// If we did not win the game, it can be runnning or we could have lost it.
		
		// If the current wrong guess count is bigger than or equal to the permitted wrong guess count, we've lost.
		if (getWrongGuessCount()+1>=Config.maxTurnCountToLoose){
			return GameState.GAME_LOST;
		}
		
		// If we did not win nor loose, the game is still running
		return GameState.GAME_RUNNING;
	}
}
