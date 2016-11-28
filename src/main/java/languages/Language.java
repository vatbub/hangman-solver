package languages;

/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import com.mongodb.MongoTimeoutException;

import common.Common;
import common.AppConfig;
import logging.FOKLogger;
import stats.HangmanStats;

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

	private static TabFile languageCodesFile;

	/**
	 * The {@link TabFile} that contains the dictionary of this language
	 */
	private TabFile dictionaryTabFile;

	/**
	 * Creates a new {@link Language}-Object that contains all information about
	 * the specified language
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 */
	public Language(String languageCode) {
		this.languageCode = languageCode;
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
	 * Looks up the {@link URL} to the resource file for the specified language.
	 * Uses the dictionary files shipped in this jar.
	 * 
	 * @param languageCode
	 *            The ISO 639-3 language code
	 * @return The {@link File}-representation of the resource file that
	 *         contains the word list for the specified language or {@code null}
	 *         if the language is not supported.
	 */
	private URL getTabFileNameInternalResource() {

		// Try to get the resource file, if it fails, the language is not
		// supported
		return Language.class.getResource(AppConfig.languageDictPattern.replace("{langCode}", this.getLanguageCode()));
	}

	private File getMergedDictionaryFile() {
		return new File(Common.getAndCreateAppDataPath()
				+ AppConfig.languageDictEnhancedPattern.replace("{langCode}", this.getLanguageCode()));
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
				languageCodesFile = new TabFile(AppConfig.languageCodes);
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
	private static String getHumanReadableName(String languageCode) {
		try {
			if (languageCodesFile == null) {
				// Open the LanguageCodes.tab-file
				languageCodesFile = new TabFile(AppConfig.languageCodes);
			}

			// Go through all records to find the language
			for (int i = 0; i < languageCodesFile.getRowCount(); i++) {
				if (languageCodesFile.getValueAt(i, 0).equals(languageCode)) {
					return languageCodesFile.getValueAt(i, 3);
				}
			}

			// We only arrive here if the language was not found
			return null;
		} catch (IOException e) {
			log.getLogger().log(Level.SEVERE, "An error occurred", e);
			return null;
		}
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof Language) {
			if (this.getLanguageCode().equals(((Language) anObject).getLanguageCode())) {
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
	 * Returns the URL pointing to the dictionary file that was merged with the
	 * online word list (if one is already downloaded) or else the resource file
	 * shipped with this jar as a *.tab file
	 *
	 * @return The URL pointing to the resource file that contains the word list
	 *         provided by the cldr as a *.tab file
	 */
	public URL getTabFileName() {
		try {
			// Check if we have a merged version available
			if (getMergedDictionaryFile().exists()) {
				return getMergedDictionaryFile().toURI().toURL();
			} else {
				return getTabFileNameInternalResource();
			}
		} catch (MalformedURLException e) {
			// This error should never happen because all generated urls only
			// depend on the app config in common.Config
			log.getLogger().log(Level.SEVERE, "An error occurred that should never occur", e);
			return null;
		}
	}

	public TabFile getTabFile() throws IOException {
		if (dictionaryTabFile == null) {
			dictionaryTabFile = new TabFile(getTabFileName());
		}

		return dictionaryTabFile;
	}

	/**
	 * Merges the content of this languages offline dictionary with the online
	 * database and caches the result offline. Once the job finishes, the
	 * TabFile instance that you retrieved using {@link #getTabFile()} is
	 * refreshed automatically.
	 * 
	 * @throws IOException
	 *             If the offline copy cannot be saved
	 */
	public void mergeWithOnlineVersion() throws IOException {
		TabFile file = this.getTabFile();

		HangmanStats.mergeWithDictionary(file, this);

		file.save(this.getMergedDictionaryFile());
	}

	/**
	 * Same as {@link #mergeWithOnlineVersion()} but does the job
	 * asynchronously.<br>
	 * Override {@link #mergeWithOnlineVersionAsyncOnIOException()} if you wish
	 * to handle the IOException that might be thrown by
	 * {@link #mergeWithOnlineVersion()}
	 */
	public void mergeWithOnlineVersionAsync() {
		Language me = this;
		Thread mergeWithOnlineVersionAsyncThread = new Thread() {
			@Override
			public void run() {
				try {
					me.mergeWithOnlineVersion();
				} catch (IOException e) {
					me.mergeWithOnlineVersionAsyncOnIOException();
				}catch (MongoTimeoutException e3){
					// Just print it to the log
					log.getLogger().log(Level.SEVERE, "You are probably not connected to the internet, are you?", e3);
				}
			}
		};
		mergeWithOnlineVersionAsyncThread.setName("mergeWithOnlineVersionAsyncThread");
		mergeWithOnlineVersionAsyncThread.start();
	}

	public void mergeWithOnlineVersionAsyncOnIOException() {

	}
}
