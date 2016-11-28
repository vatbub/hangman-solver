package stats;

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


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import common.Prefs;
import languages.Language;
import languages.TabFile;
import logging.FOKLogger;

/**
 * This class is intended to count the words used in the solver in a
 * <a href="https://www.mongodb.com/">MongoDB</a> for the social experiment.
 * 
 * @author Frederik Kammel
 *
 */
public class HangmanStats {

	private static FOKLogger log = new FOKLogger(HangmanStats.class.getName());

	/**
	 * A {@link List} that contains all words that were already submitted. This
	 * ensures that the word counts in the
	 * <a href="https://www.mongodb.com/">MongoDB</a> are correct.
	 */
	private static List<String> alreadySubmittedWordsInThisSession = new ArrayList<String>();
	/**
	 * The current upload queue.
	 */
	private static LinkedBlockingQueue<Document> docQueue = new LinkedBlockingQueue<Document>();
	/**
	 * This object is used to save a copy of the upload queue on the disc to
	 * keep it even if the app is relaunched.
	 */
	private static Prefs preferences = initPrefs();
	/**
	 * The pref key where the offline copy of the upload queue is saved.
	 */
	private static String persistentDocQueueKey = "docQueue";

	/**
	 * This thread runs in the background and uploads all submitted words. This
	 * concept ensures that all words are submitted even if the player was
	 * offline while playing.
	 */
	public static Thread uploadThread = new Thread() {
		private boolean interrupted = false;

		@Override
		public void run() {
			this.setName("uploadThread");
			log.getLogger().info("Starting " + this.getName() + "...");

			readDocQueueFromPreferences();

			while (!interrupted) {
				try {
					if (MongoSetup.isReachable()) {
						if (!docQueue.isEmpty()) {
							Document newDoc = docQueue.remove();
							String word = newDoc.getString("word");
							String langCode = newDoc.getString("lang");
							MongoCollection<Document> coll = MongoSetup.getWordsUsedCollection();
							Document doc = coll
									.find(Filters.and(Filters.eq("word", word), Filters.eq("lang", langCode))).first();

							log.getLogger().info("Transferring word " + word + "...");

							if (doc == null) {
								// word never added prior to this
								doc = new Document("word", word).append("lang", langCode).append("count", 1);
								coll.insertOne(doc);
							} else {
								// word already known in the database
								coll.updateOne(Filters.and(Filters.eq("word", word), Filters.eq("lang", langCode)),
										Updates.inc("count", 1));
							}
						}
					}
				} catch (Exception e) {
					System.err.println(
							"Something went wrong while transferring a document to the MongoDB but don't worry, the document was probably saved on your hard drive and will be transferred after launching the app again.");
				}
			}
		}

		@Override
		public void interrupt() {
			interrupted = true;
			saveDocQueueToPreferences();
			log.getLogger().info("Shutting " + this.getName() + " down...");
		}
	};

	/**
	 * Takes the content of the docQueue and saves its content using the Prefs
	 * class from the common project
	 */
	private static void saveDocQueueToPreferences() {
		if (preferences != null) {
			log.getLogger().info("Saving docQueue to disk...");
			String res = "";
			while (!docQueue.isEmpty()) {
				Document doc = docQueue.remove();
				res = res + doc.toJson();
				if (!docQueue.isEmpty()) {
					// Still other objects left so add a line break
					res = res + "\n";
				}
			}

			preferences.setPreference(persistentDocQueueKey, res);
		} else {
			log.getLogger().info("Cannot save docQueue to disk as preferences could not be initialized.");
		}
	}

	/**
	 * Reads the persistent copy of the docQueue if one exists and merges it
	 * with the docQueue in memory.
	 */
	private static void readDocQueueFromPreferences() {
		if (preferences != null) {
			log.getLogger().info("Reading docQueue from disk...");

			String persStr = preferences.getPreference(persistentDocQueueKey, "");

			if (!persStr.equals("")) {
				String[] docs = persStr.split("\n");

				for (String newDoc : docs) {
					Document doc = Document.parse(newDoc);
					if (!docQueue.contains(doc)) {
						try {
							docQueue.put(doc);
						} catch (InterruptedException e) {
							log.getLogger().log(Level.SEVERE, "An error occurred", e);
						}
					}
				}
			}
		} else {
			log.getLogger().info("Cannot read docQueue to disk as preferences could not be initialized.");
		}

	}

	private static Prefs initPrefs() {
		Prefs res = null;

		try {
			res = new Prefs(HangmanStats.class.getName());
		} catch (Exception e) {
			// Disable offline cache of stats
		}

		return res;

	}

	/**
	 * Adds a word to the mongodb database
	 * 
	 * @param word
	 *            The word to be added
	 * @param lang
	 *            The Language the user is currently playing in
	 */
	public static void addWordToDatabase(String word, Language lang) {

		if (uploadThread.isAlive() == false) {
			uploadThread.start();
		}

		String[] words = word.split(" ");

		for (String w : words) {
			if (!alreadySubmittedWordsInThisSession.contains(w)) {
				// word not submitted yet
				alreadySubmittedWordsInThisSession.add(w);
				log.getLogger().info("Submitting word '" + w + "' to MongoDB...");
				Document doc = new Document("word", w).append("lang", lang.getLanguageCode()).append("count", 1);
				try {
					docQueue.put(doc);
				} catch (InterruptedException e) {
					log.getLogger().log(Level.SEVERE, "An error occurred", e);
				}
				log.getLogger().info("Submission done.");
			}
		}

	}

	/**
	 * Merges the database entries with the given dictionary to enhance the
	 * dictionary. After merging the online database and the local one, the
	 * local copy will be sorted by the amount that users used a word so that
	 * most used words will be preffered.
	 * 
	 * @param dictionary
	 *            The Dictionary to be merged
	 * @param lang
	 *            The language requested
	 */
	public static void mergeWithDictionary(TabFile dictionary, Language lang) {
		log.getLogger()
				.info("Merging offline dictionary for language " + lang.getLanguageCode() + " with online database...");
		MongoCollection<Document> coll = MongoSetup.getWordsUsedCollection();
		for (Document doc : coll.find(Filters.eq("lang", lang.getLanguageCode()))) {
			String word = doc.get("word").toString();
			int count = doc.getInteger("count");

			List<Integer> indexList = dictionary.indexOfIgnoreCase(word, 2);
			if (indexList.isEmpty()) {
				// Word not yet present in dictionary so add it
				dictionary.addRow(new String[] { "fromOnlineDatabase", lang.getLanguageCode() + ":lemma", word,
						Integer.toString(count) });
			} else {
				dictionary.setValueAt(Integer.toString(count), indexList, 3);
			}
		}
		log.getLogger().info("Merge finished, sorting TabFile now...");

		dictionary.sortDescending(3);
		

		log.getLogger().info("Sorting finished.");
	}
}
