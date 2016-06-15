package common;
import java.net.URL;

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

	// Language class
	// {langCode} will be replaced by the language code
	public static String cldrNamePattern = "/languages/cldr/wn-cldr-{langCode}.tab";
	public static String wiktNamePattern = "/languages/wikt/wn-wikt-{langCode}.tab";
	public static URL languageCodes = Language.class.getResource("/languages/LanguageCodes.tab");
}
