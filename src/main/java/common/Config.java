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
	
	// HangmanView
	public static double windowGap = 5;
}
