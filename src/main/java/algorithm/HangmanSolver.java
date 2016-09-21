package algorithm;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import common.Config;
import languages.*;
import logging.FOKLogger;

/**
 * A class that holds all methods and algorithms to solve a hangman puzzle.
 * 
 * @author frede
 *
 */
public class HangmanSolver {

	private static Language langOld;
	private static TabFile database;
	private static FOKLogger log = new FOKLogger(HangmanSolver.class.getName());

	/**
	 * A {@link List} that contains all characters and words that the computer
	 * has guessed.
	 */
	public static List<String> proposedSolutions = new ArrayList<String>();

	private static String currentWordCopy;

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
		
		ResultList resultList = new ResultList();

		if (!lang.equals(langOld) || database == null) {
			// Load language databases
			loadLanguageDatabases(lang);
		}

		// Split the pattern up in words
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(currentSequence.split(" ")));

		// Remove all words that don't contain an underscore as they are fully
		// solved
		/*int indexCorr = 0;
		for (int i = 0; i < words.size(); i++) {
			if (!words.get(i - indexCorr).contains("_")) {
				words.remove(i - indexCorr);
				indexCorr = indexCorr + 1;
			}
		}*/

		// Go through all words
		for (String word : words) {
			resultList.add(solveWord(word, lang));
		}

		ResultType globalResType = Collections.min(resultList.getResultTypeList());
		Result globalResult;

		if (globalResType == ResultType.word) {
			// all results are words so generate a phrase from it
			globalResult = Result.generatePhraseResult(resultList);
			globalResult.bestWordScore = TabFile.stringCorrelation(currentSequence, globalResult.result);
		} else if (resultList.size() == 1) {
			// We only got one result so return that
			globalResult = resultList.get(0);
		} else {
			// Generate a global solution from letters by taking the letter with
			// the highest score

			// Find the best score
			int maxIndex = 0;
			ResultList maxValues = new ResultList();

			for (int i = 0; i < resultList.size(); i++) {
				if (resultList.get(i).bestCharScore > resultList.get(maxIndex).bestCharScore && resultList.get(i).resultType==ResultType.letter) {
					// new max found
					maxIndex = i;
					maxValues = new ResultList();
					maxValues.add(resultList.get(i));
				} else if (resultList.get(i).bestCharScore == resultList.get(maxIndex).bestCharScore) {
					// found another letter with the same score so add it to the
					// list too
					maxValues.add(resultList.get(i));
				}
			}

			if (maxValues.size() == 0) {
				// only one maxValue
				globalResult = maxValues.get(0);
			} else {
				// More than one maxValue found, return the one that appears the
				// most
				Map<Character, Integer> charCounts = new HashMap<Character, Integer>();

				for (Result res : maxValues) {
					if (charCounts.containsKey(res.bestChar)) {
						// Increment the value
						charCounts.put(res.bestChar, charCounts.get(res.bestChar) + 1);
					} else {
						// Add new entry to the map
						charCounts.put(res.bestChar, 1);
					}
				}

				// Get the char with most appearances
				Map.Entry<Character, Integer> maxEntry = null;

				for (Entry<Character, Integer> entry : charCounts.entrySet())
				{
				    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
				    {
				        maxEntry = entry;
				    }
				}
				
				globalResult = new Result();
				globalResult = resultList.getFirstResultWithBestChar(maxEntry.getKey());
			}

			// generate the best word as a phrase
			globalResult.bestWord = Result.generatePhraseResult(resultList).result;
			// calculate global word score
			globalResult.bestWordScore = TabFile.stringCorrelation(currentSequence, globalResult.bestWord);
		}

		proposedSolutions.add(globalResult.result);
		return globalResult;
	}

	public static Result solveWord(String word, Language lang) {

		Result res = new Result();

		res.lang = lang;
		res.gameState = winDetector(word);

		currentWordCopy = word;

		// Get all words from the database with equal length
		List<String> wordsWithEqualLength = database.getValuesWithLength(2, word.length());

		// Check if there are words that match 90%
		String bestWord = database.getValueWithHighestCorrelation(2, word, proposedSolutions);
		boolean foundBestWord = true;

		log.getLogger().info("Best match in dictionary: " + bestWord);

		if (bestWord.length() == 0) {
			// dictionaries are both used up
			log.getLogger().severe("dictionary used up");
			foundBestWord = false;
		} else if (bestWord.equals("")) {
			foundBestWord = false;
		} else {
			res.bestWord = bestWord;
		}

		if (foundBestWord) {
			res.bestWordScore = TabFile.stringCorrelation(word, res.bestWord);

			// compute best letter (even if a bestWord has already been found,
			// for completeness)

			// Get all chars from the bestWord
			char[] priorityChars = res.bestWord.toCharArray();
			SingleChar chr = getMostFrequentChar(wordsWithEqualLength, priorityChars);
			res.bestChar = chr.chr;
			res.bestCharScore = chr.letterScore;

			// Set the result type
			if (res.bestWordScore >= Config.thresholdToSelectWord(word.length())) {
				// Specify the bestWord as the global result
				res.convertToWordResult();
			} else {
				res.convertToLetterResult();
			}

		} else {
			// No bestWord found
			log.getLogger().info("No best word found, using most common chars only");
			SingleChar chr = getMostFrequentChar(wordsWithEqualLength);
			res.bestChar = chr.chr;
			res.bestCharScore = chr.letterScore;
			res.convertToLetterResult();
		}

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
			log.getLogger().info("Loading language databases for " + lang.getHumanReadableName());
			langOld = lang;
			database = lang.getTabFile();
			lang.mergeWithOnlineVersionAsync();
		} catch (IOException e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
		}
	}

	/**
	 * Returns the most frequent char in the given word list.
	 * 
	 * @param wordsWithEqualLength
	 *            The list for which the most frequent char will be determined.
	 * @return The most frequent char.
	 */
	private static SingleChar getMostFrequentChar(List<String> wordsWithEqualLength) {
		return getMostFrequentChar(wordsWithEqualLength, new char[0]);
	}

	/**
	 * Returns the most frequent char in the given word list.
	 * 
	 * @param wordsWithEqualLength
	 *            The list for which the most frequent char will be determined.
	 * @param priorityChars
	 *            The ranking of most frequent chars will be filtered so that it
	 *            only contains priorityChars that were not yet proposed. If the
	 *            ranking is empty after filtering, no filter is appliyed and
	 *            the method acts like {@code getMostFrequentChar(words)}
	 * @return The most frequent char.
	 */
	private static SingleChar getMostFrequentChar(List<String> wordsWithEqualLength, char[] priorityChars) {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AtomicInteger currentIndex = new AtomicInteger(0);
		List<CustomAtomicInteger> charCounts = new ArrayList<CustomAtomicInteger>();

		System.out.println("Preparing...");
		for (int i = 0; i < Character.MAX_VALUE; i++) {
			CustomAtomicInteger temp = new CustomAtomicInteger(0);
			charCounts.add(temp);
		}

		// List<AtomicInteger> charCounts = new
		// ArrayList<AtomicInteger>(Collections.nCopies(Character.MAX_VALUE, new
		// AtomicInteger(0)));

		log.getLogger().info("Dictionary size: " + wordsWithEqualLength.size());
		System.out.println("Counting...");
		for (int i = 0; i < Config.getParallelThreadCount(); i++) {
			threads.add(new Thread() {
				@Override
				public void run() {
					int index = currentIndex.getAndIncrement();
					while (index < wordsWithEqualLength.size()) {
						countAllCharsInString(wordsWithEqualLength.get(index), charCounts);

						// Grab the next index
						index = currentIndex.getAndIncrement();
					}
				}
			});
			threads.get(i).start();
		}

		// Wait for threads
		for (int i = 0; i < Config.getParallelThreadCount(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				log.getLogger().log(Level.SEVERE, "An error occurred", e);
			}
		}

		// Find max count
		System.out.println("Finding max...");
		int maxCount = -1;
		int maxIndex = 0;

		// copy charCounts
		List<CustomAtomicInteger> sortedCharCounts = new ArrayList<CustomAtomicInteger>(charCounts);
		// sort the charCounts
		Collections.sort(sortedCharCounts);
		Collections.reverse(sortedCharCounts);

		// Max value is now at the top

		if (priorityChars.length != 0) {
			// priority chars specified

			// convert priorityChars to lower case
			for (int j = 0; j < priorityChars.length; j++) {
				// convert to lower case
				priorityChars[j] = Character.toLowerCase(priorityChars[j]);
			}

			for (int i = 0; i < charCounts.size(); i++) {
				char chr = (char) charCounts.indexOf(sortedCharCounts.get(i));
				if ((!proposedSolutions.contains(Character.toString(Character.toUpperCase(chr))))
						&& charArrayContais(priorityChars, Character.toLowerCase(chr))) {
					maxIndex = (int) chr;
					maxCount = charCounts.get((int) chr).get();
					break;
				}
			}
		}

		if (priorityChars.length == 0 || maxCount == -1) {
			// No priorityChars specified or all priority chars already proposed

			for (int i = 0; i < charCounts.size(); i++) {
				char chr = (char) charCounts.indexOf(sortedCharCounts.get(i));

				if (charCounts.get(i).get() > maxCount
						&& (!proposedSolutions.contains(Character.toString(Character.toUpperCase(chr))))) {
					maxIndex = (int) chr;
					maxCount = charCounts.get((int) chr).get();
					break;
				}
			}
		}
		System.out.println("Done!");
		return new SingleChar(Character.toUpperCase((char) maxIndex), maxCount);
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
	 * @param countList
	 *            A List where each index represents the number of times this
	 *            char is contained in the string. E. g. the number at the 97th
	 *            position represents how often the lower case {@code a} is
	 *            contained in the string as {@code a} is represented as 97 in a
	 *            char.<br>
	 *            As this method is designed to be used in a
	 *            multithreading-context, the List specified as
	 *            {@code countList} must contain
	 *            {@link CustomAtomicInteger}s.<br>
	 *            <b>Because of a better performance, this method uses a given
	 *            list and modifies it rather than creating a new one that has
	 *            to be summed up to existing ones in another slow
	 *            {@code for}-loop</b>
	 */
	private static void countAllCharsInString(String str, List<CustomAtomicInteger> countList) {
		for (char chr : str.toCharArray()) {
			countList.get((int) chr).getAndIncrement();
		}
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
			if (!currentWordCopy.toUpperCase().contains(Character.toString(Character.toUpperCase(chr)))
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
	 * Checks if the computer has won or lost the game or the game is still
	 * running
	 * 
	 * @param currentSequence
	 *            The current letter sequence.
	 * @return The current {@link GameState}
	 */
	public static GameState winDetector(String currentSequence) {
		// Split the pattern up in words
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(currentSequence.split(" ")));

		// Remove all words that don't contain an underscore as they are fully
		// solved
		int indexCorr = 0;
		int wordsSize = words.size();
		for (int i = 0; i < wordsSize; i++) {
			if (!words.get(i - indexCorr).contains("_")) {
				words.remove(i - indexCorr);
				indexCorr = indexCorr + 1;
			}
		}

		// If all words are solved, the list is now empty and the computer hhas
		// won the game.
		if (words.size() == 0) {
			return GameState.GAME_WON;
		}

		// If we did not win the game, it can be runnning or we could have lost
		// it.

		// If the current wrong guess count is bigger than or equal to the
		// permitted wrong guess count, we've lost.
		if (getWrongGuessCount() + 1 >= Config.maxTurnCountToLoose) {
			return GameState.GAME_LOST;
		}

		// If we did not win nor loose, the game is still running
		return GameState.GAME_RUNNING;
	}

	private static class SingleChar {
		public SingleChar(char chr, int letterScore) {
			this.chr = chr;
			this.letterScore = letterScore;
		}

		char chr;
		double letterScore;
	}
}
