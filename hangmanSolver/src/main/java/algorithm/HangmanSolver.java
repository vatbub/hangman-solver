package algorithm;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import common.Config;
import languages.*;

public class HangmanSolver {

	private static Language langOld;
	private static TabFile wiktDatabase;
	private static TabFile cldrDatabase;

	public static List<String> proposedSolutions = new ArrayList<String>();

	public static Result solve(String currentSequence, Language lang) {
		
		Result res = new Result();

		if (!lang.equals(langOld) || wiktDatabase == null || cldrDatabase == null) {
			// Load language databases
			loadLanguageDatabases(lang);
		}

		// Split the pattern up in words
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(currentSequence.split(" ")));

		// Remove all words that don't contain an underscore as they are fully
		// solved
		int indexCorr = 0;
		for (int i=0; i<words.size(); i++) {
			if (!words.get(i).contains("_")) {
				words.remove(i-indexCorr);
				indexCorr = indexCorr+1;
			}
		}

		// Go through all words
		for (String word : words) {
			// Get all words from the database with equal length
			List<String> wordsWithEqualLength = wiktDatabase.getValuesWithLength(2, word.length());
			wordsWithEqualLength.addAll(cldrDatabase.getValuesWithLength(2, word.length()));

			// Check if there are words that match 90%
			String bestWordWikt = wiktDatabase.getValueWithHighestCorrelation(2, word, proposedSolutions);
			String bestWordCldr = cldrDatabase.getValueWithHighestCorrelation(2, word, proposedSolutions);

			System.out.println(bestWordWikt);
			System.out.println(bestWordCldr);
			System.out.println(TabFile.stringCorrelation(word, bestWordWikt)); // __l__h_n

			if (TabFile.stringCorrelation(word, bestWordWikt) >= TabFile.stringCorrelation(word, bestWordCldr)) {
				res.bestWord = bestWordWikt;
			} else {
				res.bestWord = bestWordCldr;
			}
			
			res.bestWordScore=TabFile.stringCorrelation(word, res.bestWord);

			if (res.bestWordScore >= Config.thresholdToSelectWord(word.length())) {
				proposedSolutions.add(res.bestWord);
				res.result=res.bestWord;
				return res;
			}

			// apparently no direct match, get the most used letter from the
			// results

			// Get all chars from the bestWord
			char[] priorityChars = res.bestWord.toCharArray();

			res.result = Character.toString(getMostFrequentChar(wordsWithEqualLength, priorityChars));
			proposedSolutions.add(res.result);
			return res;

		}
		
		res.result = "";
		return res;
	}

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
			for (int j=0; j<priorityChars.length; j++) {
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
		
		if (priorityChars.length == 0 || maxCount==-1){
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
	
	private static boolean charArrayContais(char[] array, char value){
		for (char c:array){
			if (c==value){
				return true;
			}
		}
		
		return false;
	}

	private static int[] countAllCharsInString(String str) {
		int[] res = new int[26];

		for (int i = 0; i < 26; i++) {
			res[i] = countCharInString(str, (char) ('A' + i)) + countCharInString(str, (char) ('a' + i));
		}

		return res;
	}

	private static int countCharInString(String str, char chr) {
		int res = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == chr) {
				res = res + 1;
			}
		}

		return res;
	}
	
	public static boolean wordContainsProposedChar(String word){
		
		char[] chars = word.toCharArray();
		
		for (char chr:chars){
			// if (proposedSolutions.contains(Character.toString(Character.toUpperCase(chr)))){
			if (proposedSolutions.contains(Character.toString((chr)))){
				return true;
			}
		}
		
		return false;
	}
}
