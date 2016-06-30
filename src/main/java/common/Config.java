package common;
import java.io.File;
import java.net.URL;

import com.mongodb.MongoClientURI;
import languages.Language;

public class Config {
	// algorithm
	public static int parallelThreadCount = 4;
	public static double thresholdToSelectWord(int wordLength){
		if (wordLength<=4){
			return 0.7;
		}else {
			return 0.8;
		}
	}
	
	public static int maxTurnCountToLoose = 11;

	// Language class
	// {langCode} will be replaced by the language code
	public static String cldrNamePattern = "/languages/cldr/wn-cldr-{langCode}.tab";
	public static String wiktNamePattern = "/languages/wikt/wn-wikt-{langCode}.tab";
	public static URL languageCodes = Language.class.getResource("/languages/LanguageCodes.tab");
	
	// View
	public static double thresholdToShowWord = 0.2; 
	
	// IFTTT
	public static String iftttMakerApiKey = "dbjf67CBpZit4QOBthB0xW";
	public static String iftttWinEvent = "hangmanSolverWon";
	public static String iftttLooseEvent = "hangmanSolverLost";
	
	//MongoDB
	public static MongoClientURI mongoDBServerAddress = new MongoClientURI("mongodb://user:ljkhfgsd98675@ds019634.mlab.com:19634/hangmanstats");
	public static String mongoDBDatabaseName = "hangmanstats";
	public static String mongoDBWordsUsedCollectionName = "wordsused";
	
	//General
	
	/**
	 * Gets the appData directory of the os. In case the current OS is Windows,
	 * it returns C:\Users\(username)\AppData\Roaming, in case of Mac, it
	 * returns (home directory)/Library/Application Support and in case of
	 * Linux, it returns the home directory.<br>
	 * <br>
	 * 
	 * @return The sub directory of the home directory of the os where the app
	 *         can save all files that need to persist, e. g. settings
	 */
	public static String getAppDataPath() {
		String workingDirectory;
		// here, we assign the name of the OS, according to Java, to a
		// variable...
		String OS = (System.getProperty("os.name")).toUpperCase();
		// to determine what the workingDirectory is.
		// if it is some version of Windows
		if (OS.contains("WIN")) {
			// it is simply the location of the "AppData" folder
			workingDirectory = System.getenv("AppData");
		}
		// Otherwise, we assume Linux or Mac
		else {
			// in either case, we would start in the user's home directory
			workingDirectory = System.getProperty("user.home");
			// if we are on a Mac, we are not done, we look for "Application
			// Support"
			workingDirectory += "/Library/Application Support";
		}

		return workingDirectory + File.separator + "hangmanSolver" + File.separator;
	}
	
	// HangmanView
	public static double windowGap = 5;
}
