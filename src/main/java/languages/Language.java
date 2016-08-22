package languages;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import common.Config;
import logging.FOKLogger;

public class Language {
	
	private static FOKLogger log = new FOKLogger(Language.class.getName());

	/**
	 * Cached list of supported languages
	 */
	private static LanguageList supportedLanguages;

	/**
	 * Map that maps every ISO-2 language code to a iso-1 language code
	 */
	private static Map<String, Locale> languageCodeMap = initLanguageCodeMap();

	/**
	 * The ISO 639-3 language code
	 */
	private String languageCode;
	/**
	 * The URL pointing to the resource file that contains the word list as a
	 * *.tab file
	 */
	private URL tabfileName;

	/**
	 * Creates a new {@link Language}-Object that contains all information about
	 * the specified language
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 */
	public Language(String languageCode) {
		this.languageCode = languageCode;
		tabfileName = getCldrName(languageCode);
	}

	private static Map<String, Locale> initLanguageCodeMap() {
		String[] languages = Locale.getISOLanguages();
		Map<String, Locale> localeMap = new HashMap<String, Locale>(languages.length);
		for (String language : languages) {
			Locale locale = new Locale(language);
			localeMap.put(locale.getISO3Language(), locale);
		}

		return localeMap;
	}

	/**
	 * Looks up the {@link URL} to the resource file for the specified language
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 * @return The {@link URL} to the resource file that contains the word list
	 *         for the specified language or {@code null} if the language is not
	 *         supported.
	 */
	private URL getCldrName(String languageCode) {

		// Try to get the resource file, if it fails, the language is not
		// supported
		return Language.class.getResource(Config.languageDictPattern.replace("{langCode}", languageCode));
	}

	/**
	 * Returns a {@link List} of supported languages. The method result is
	 * cached, which is why the first function call will take longer that the
	 * following ones.
	 * 
	 * @return A {@link List} of supported languages or {@code null} if an
	 *         exception occurs.
	 */
	public static LanguageList getSupportedLanguages() {

		if (supportedLanguages != null) {
			// we've got a cached version so return that one
			return supportedLanguages;
		} else {
			// No cached version available so generate a new one
			LanguageList res = new LanguageList();

			// Open the LanguageCodes.tab-file
			TabFile languageCodesFile;
			try {
				languageCodesFile = new TabFile(Config.languageCodes);
				// Go through all records and check if the word databases to all
				// files
				// can be found for each language
				for (int i = 0; i < languageCodesFile.getRowCount(); i++) {
					Language temp = new Language(languageCodesFile.getValueAt(i, 0));

					if (temp.getTabFileName() != null) {
						// Found all databases, so the language is supported
						res.add(temp);
					}
				}

				supportedLanguages = res;
				return res;
			} catch (IOException e) {
				log.getLogger().log(Level.SEVERE, "An error occurred", e);
				return null;
			}
		}
	}

	/**
	 * Returns the human readable name of the language taken from the resource
	 * specified in {@link languageCodes}.
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 * @return The human readable name of the specified language or {@code null}
	 *         if the {@code languageCode} could not be found in the
	 *         language-code-list.
	 */
	private String getHumanReadableName(String languageCode) {
		try {
			// Open the LanguageCodes.tab-file
			TabFile languageCodesFile = new TabFile(Config.languageCodes);

			// Go through all records to find the language
			for (int i = 0; i < languageCodesFile.getRowCount(); i++) {
				if (languageCodesFile.getValueAt(i, 0).equals(languageCode)) {
					return languageCodesFile.getValueAt(i, 3);
				}
			}

			// We only arrive here if the language was not found
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof Language) {
			if (this.getTabFileName().equals(((Language) anObject).getTabFileName())
					&& this.getLanguageCode().equals(((Language) anObject).getLanguageCode())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Returns the ISO 639-3 language code of this language.
	 * 
	 * @return The ISO 639-3 language code of this language.
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/***
	 * Returns the human readable name of this language.
	 * 
	 * @return The human readable name of this language.
	 */
	public String getHumanReadableName() {
		Locale locale = languageCodeMap.get(this.getLanguageCode());
		String name;

		try {
			name = locale.getDisplayLanguage(Locale.ENGLISH);
		} catch (NullPointerException e) {
			name = getHumanReadableName(this.languageCode);
		}

		return name;
	}

	/***
	 * Returns the human readable name of this language translated into the
	 * current display language. If no translation can be found, this returns
	 * the same as {@link #getHumanReadableName()}
	 * 
	 * @return The human readable name of this language translated into the
	 *         current display language.
	 */
	public String getHumanReadableTranslatedName() {
		Locale locale = languageCodeMap.get(this.getLanguageCode());
		String name;

		try {
			name = locale.getDisplayLanguage();
		} catch (NullPointerException e) {
			name = getHumanReadableName(this.languageCode);
		}

		return name;
	}

	/**
	 *
	 * Returns the URL pointing to the resource file that contains the word list
	 * as a *.tab file
	 *
	 * @return The URL pointing to the resource file that contains the word list
	 *         provided by the cldr as a *.tab file
	 */
	public URL getTabFileName() {
		return tabfileName;
	}

	public TabFile getTabFile() throws IOException {
		return new TabFile(getTabFileName());
	}
}
