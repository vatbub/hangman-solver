package languages;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import common.Config;

public class Language {

	/**
	 * Cached list of supported languages
	 */
	private static List<Language> supportedLanguages;

	/**
	 * The ISO 639-3 language code
	 */
	private String languageCode;
	/**
	 * The URL pointing to the resource file that contains the word list
	 * provided by the cldr as a *.tab file
	 */
	private URL cldrName;
	/**
	 * The URL pointing to the resource file that contains the word list
	 * provided by Wiktionary as a *.tab file
	 */
	private URL wiktName;

	/**
	 * Creates a new {@link Language}-Object that contains all information about
	 * the specified language
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 */
	public Language(String languageCode) {
		this.languageCode = languageCode;
		cldrName = getCldrName(languageCode);
		wiktName = getWiktName(languageCode);
	}

	/**
	 * Looks up the {@link URL} to the resource file for the specified language
	 * coming from cldr
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 * @return The {@link URL} to the resource file that contains the word list
	 *         for the specified language or {@code null} if the language is not
	 *         supported by the cldr.
	 */
	private URL getCldrName(String languageCode) {

		// Try to get the resource file, if it fails, the language is not
		// supported
		return Language.class.getResource(Config.cldrNamePattern.replace("{langCode}", languageCode));
	}

	/**
	 * Looks up the {@link URL} to the resource file for the specified language
	 * coming from wiktionary
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 * @return The {@link URL} to the resource file that contains the word list
	 *         for the specified language or {@code null} if the language is not
	 *         supported by the wiktionary.
	 */
	private URL getWiktName(String languageCode) {

		// Try to get the resource file, if it fails, the language is not
		// supported
		return Language.class.getResource(Config.wiktNamePattern.replace("{langCode}", languageCode));
	}

	/**
	 * Returns a {@link List} of supported languages. The method result is
	 * cached, which is why the first function call will take longer that the
	 * following ones.
	 * 
	 * @return A {@link List} of supported languages or {@code null} if an
	 *         exception occurs.
	 */
	public static List<Language> getSupportedLanguages() {

		if (supportedLanguages != null) {
			// we've got a cached version so return that one
			return supportedLanguages;
		} else {
			// No cached version available so generate a new one
			List<Language> res = new ArrayList<Language>();

			// Open the LanguageCodes.tab-file
			TabFile languageCodesFile;
			try {
				languageCodesFile = new TabFile(Config.languageCodes);
				// Go through all records and check if the word databases to all
				// files
				// can be found for each language
				for (int i = 0; i < languageCodesFile.getRowCount(); i++) {
					Language temp = new Language(languageCodesFile.getValueAt(i, 0));

					if (temp.getCldrName() != null && temp.getWiktName() != null) {
						// Found all databases, so the language is supported
						res.add(temp);
					}
				}

				supportedLanguages = res;
				return res;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			if (this.getCldrName().equals(((Language) anObject).getCldrName())
					&& this.getWiktName().equals(((Language) anObject).getWiktName())
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
		return getHumanReadableName(this.languageCode);
	}

	/**
	 *
	 * Returns the URL pointing to the resource file that contains the word list
	 * provided by the cldr as a *.tab file
	 *
	 * @return The URL pointing to the resource file that contains the word list
	 *         provided by the cldr as a *.tab file
	 */
	public URL getCldrName() {
		return cldrName;
	}

	/**
	 *
	 * Returns the URL pointing to the resource file that contains the word list
	 * provided by Wiktionary as a *.tab file
	 *
	 * @return The URL pointing to the resource file that contains the word list
	 *         provided by Wiktionary as a *.tab file
	 */
	public URL getWiktName() {
		return wiktName;
	}
}
