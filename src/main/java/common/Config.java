package common;

import java.net.URL;

import com.mongodb.MongoClientURI;
import languages.Language;

/**
 * A class to configure some parameters.
 * 
 * @author frede
 *
 */
public class Config {
	// algorithm
	/**
	 * The maximum number of parallel threads that are used to compute the next
	 * guess in the {@link algorithm.HangmanSolver} class.
	 */
	public static int parallelThreadCount = 4;

	/**
	 * The {@link algorithm.HangmanSolver}-algorithm will find the word in in
	 * the dictionary that matches the current word sequence the best. This is
	 * done by calculating a score using
	 * {@link languages.TabFile#stringCorrelation}. If the computed score is
	 * equal or bigger to the value returned by this method, the best word will
	 * be accepted as the next guess.
	 * 
	 * @param wordLength
	 *            The length of the word that is evaluated.
	 * @return The required correlation score to accept a word as the next
	 *         guess.
	 * 
	 * @see languages.TabFile#stringCorrelation
	 */
	public static double thresholdToSelectWord(int wordLength) {
		if (wordLength <= 4) {
			return 0.7;
		} else {
			return 0.8;
		}
	}

	/**
	 * If the computer needs more guesses to guess the word than specified here,
	 * the computer looses.
	 */
	public static int maxTurnCountToLoose = 11;

	// Language class
	/**
	 * The path pattern to find the cldr word dictionary. {langCode} will be
	 * replaced by the language code
	 */
	public static String cldrNamePattern = "/languages/cldr/wn-cldr-{langCode}.tab";
	/**
	 * The path pattern to find the wiktionary word dictionary. {langCode} will
	 * be replaced by the language code
	 */
	public static String wiktNamePattern = "/languages/wikt/wn-wikt-{langCode}.tab";
	/**
	 * The path pattern to find the language code database.
	 */
	public static URL languageCodes = Language.class.getResource("/languages/LanguageCodes.tab");

	// View
	/**
	 * The best word must have a score bigger or equal to this to be shown as a
	 * "thought" in the gui.
	 * 
	 * @see #thresholdToSelectWord(int) thresholdToSelectWord
	 */
	public static double thresholdToShowWord = 0.2;

	// IFTTT
	/**
	 * The api key for the <a href="https://ifttt.com/maker">IFTTT Maker
	 * Channel</a>
	 */
	public static String iftttMakerApiKey = "dbjf67CBpZit4QOBthB0xW";
	/**
	 * The name of the event that is sent to the
	 * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
	 * computer wins a game.
	 */
	public static String iftttWinEvent = "hangmanSolverWon";
	/**
	 * The name of the event that is sent to the
	 * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
	 * computer looses a game.
	 */
	public static String iftttLooseEvent = "hangmanSolverLost";

	// MongoDB
	/**
	 * The {@link MongoClientURI} to reach the database where all submitted
	 * words are saved.
	 */
	public static MongoClientURI mongoDBServerAddress = new MongoClientURI(
			"mongodb://user:ljkhfgsd98675@ds019634.mlab.com:19634/hangmanstats");
	/**
	 * The name of the MongoDB database where all submitted words are saved.
	 */
	public static String mongoDBDatabaseName = "hangmanstats";
	/***
	 * The name of the MongoDb collection where all submitted words are saved.
	 */
	public static String mongoDBWordsUsedCollectionName = "wordsused";
}
