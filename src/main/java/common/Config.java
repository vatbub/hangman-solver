package common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import com.mongodb.MongoClientURI;
import languages.Language;
import logging.FOKLogger;

/**
 * A class to configure some parameters.
 * 
 * @author frede
 *
 */
public class Config {
	private static final FOKLogger log = new FOKLogger(Config.class.getName());
	private static int oldThreadCount = 0;

	// Project setup
	public static URL getUpdateRepoBaseURL() {
		URL res = null;
		try {
			res = new URL("http://dl.bintray.com/vatbub/fokprojectsSnapshots");
		} catch (MalformedURLException e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
		}

		return res;
	}

	public static final String artifactID = "hangmanSolver";
	public static final String groupID = "fokprojects";
	public static final String updateFileClassifier = "jar-with-dependencies";

	// algorithm
	/**
	 * The maximum number of parallel threads that are used to compute the next
	 * guess in the {@link algorithm.HangmanSolver} class. The number returned
	 * depends on the number of cpu cores offered to the app.
	 * 
	 * @return The maximum number of parallel threads that are used to compute
	 *         the next guess in the {@link algorithm.HangmanSolver} class.
	 */
	public static int getParallelThreadCount() {
		int threadCount = Runtime.getRuntime().availableProcessors() + 1;

		if (threadCount != oldThreadCount) {
			oldThreadCount = threadCount;
			log.getLogger().info("Now using " + threadCount + " threads");
		}

		return threadCount;
	};

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
	public static final int maxTurnCountToLoose = 11;

	// Language class
	/**
	 * The path pattern to find the merged word dictionary. {langCode} will be
	 * replaced by the language code
	 */
	public static final String languageDictPattern = "/mergedLanguages/wn-merged-{langCode}.tab";

	/**
	 * The app saves an offline copy of the dictionary merged using
	 * {@link Language#mergeWithOnlineVersion()} at the specified spot. The
	 * specified file here is saved in a subfolder of the apps appData folder.
	 * {langCode} will be replaced by the language code
	 */
	public static final String languageDictEnhancedPattern = "dictionaries" + File.separator + "{langCode}.tab";

	/**
	 * The path pattern to find the language code database.
	 */
	public static final URL languageCodes = Language.class.getResource("/mergedLanguages/LanguageCodes.tab");

	// View
	/**
	 * The best word must have a score bigger or equal to this to be shown as a
	 * "thought" in the gui.
	 * 
	 * @see #thresholdToSelectWord(int) thresholdToSelectWord
	 */
	public static final double thresholdToShowWord = 0.2;

	// IFTTT
	/**
	 * The api key for the <a href="https://ifttt.com/maker">IFTTT Maker
	 * Channel</a>
	 */
	public static final String iftttMakerApiKey = "dbjf67CBpZit4QOBthB0xW";
	/**
	 * The name of the event that is sent to the
	 * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
	 * computer wins a game.
	 */
	public static final String iftttWinEvent = "hangmanSolverWon";
	/**
	 * The name of the event that is sent to the
	 * <a href="https://ifttt.com/maker">IFTTT Maker Channel</a> when the
	 * computer looses a game.
	 */
	public static final String iftttLooseEvent = "hangmanSolverLost";

	// MongoDB
	/**
	 * The {@link MongoClientURI} to reach the database where all submitted
	 * words are saved.
	 */
	public static final MongoClientURI mongoDBServerAddress = new MongoClientURI(
			"mongodb://user:ljkhfgsd98675@ds019634.mlab.com:19634/hangmanstats");
	/**
	 * The name of the <a href="https://www.mongodb.com/">MongoDB</a> database
	 * where all submitted words are saved.
	 */
	public static final String mongoDBDatabaseName = "hangmanstats";
	/***
	 * The name of the <a href="https://www.mongodb.com/">MongoDB</a> collection
	 * where all submitted words are saved.
	 */
	public static final String mongoDBWordsUsedCollectionName = "wordsused";
}
