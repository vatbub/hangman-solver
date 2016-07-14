package stats;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import common.Prefs;
import languages.Language;

/**
 * This class is intended to count the words used in the solver in a
 * <a href="https://www.mongodb.com/">MongoDB</a> for the social experiment.
 * 
 * @author Frederik Kammel
 *
 */
public class HangmanStats {

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
	 * This object is used to save a copy of the upload queue on the disc to keep it even if the app is relaunched.
	 */
	private static Prefs preferences = new Prefs(HangmanStats.class.getName());
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
			System.out.println("Starting " + this.getName() + "...");

			readDocQueueFromPreferences();

			while (!interrupted) {
				try {
				if (MongoSetup.isReachable()) {
					if (!docQueue.isEmpty()) {
						Document newDoc = docQueue.remove();
						String word = newDoc.getString("word");
						String langCode = newDoc.getString("lang");
						MongoCollection<Document> coll = MongoSetup.getWordsUsedCollection();
						Document doc = coll.find(Filters.and(Filters.eq("word", word), Filters.eq("lang", langCode)))
								.first();

						System.out.println("Transferring word " + word + "...");

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
				}}catch (Exception e){
					System.err.println("Something went wrong while transferring a document to the MongoDB but don't worry, the document was probably saved on your hard drive and will be transferred after launching the app again.");
				}
			}
		}

		@Override
		public void interrupt() {
			interrupted = true;
			saveDocQueueToPreferences();
			System.out.println("Shutting " + this.getName() + " down...");
		}
	};

	/**
	 * Takes the content of the docQueue and saves its content using the Prefs
	 * class from the common project
	 */
	private static void saveDocQueueToPreferences() {
		System.out.println("Saving docQueue to disk...");
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
	}

	/**
	 * Reads the persistent copy of the docQueue if one exists and merges it
	 * with the docQueue in memory.
	 */
	private static void readDocQueueFromPreferences() {
		System.out.println("Reading docQueue from disk...");

		String persStr = preferences.getPreference(persistentDocQueueKey, "");

		if (!persStr.equals("")) {
			String[] docs = persStr.split("\n");

			for (String newDoc : docs) {
				Document doc = Document.parse(newDoc);
				if (!docQueue.contains(doc)) {
					try {
						docQueue.put(doc);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

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

		if (!alreadySubmittedWordsInThisSession.contains(word)) {
			// word not submitted yet
			alreadySubmittedWordsInThisSession.add(word);
			System.out.println("Submitting word '" + word + "' to MongoDB...");
			Document doc = new Document("word", word).append("lang", lang.getLanguageCode()).append("count", 1);
			try {
				docQueue.put(doc);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Submission done.");
		}

	}
}
