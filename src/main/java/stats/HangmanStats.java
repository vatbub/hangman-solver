package stats;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import languages.Language;

/**
 * This class is intended to count the words used in the solver for the social
 * experiment.
 * 
 * @author Frederik Kammel
 *
 */
public class HangmanStats {

	private static List<String> alreadySubmittedWordsInThisSession = new ArrayList<String>();
	private static LinkedBlockingQueue<Document> docQueue = new LinkedBlockingQueue<Document>();

	
	public static Thread uploadThread = new Thread() {
		private boolean interrupted = false;

		@Override
		public void run() {
			this.setName("uploadThread");
			System.out.println("Starting " + this.getName() + "...");
			while (!interrupted) {
				if (MongoSetup.isReachable()) {
					if (!docQueue.isEmpty()) {
						Document newDoc = docQueue.remove();
						String word = newDoc.getString("word");
						String langCode = newDoc.getString("lang");
						MongoCollection<Document> coll = MongoSetup.getWordsUsedCollection();
						Document doc = coll.find(Filters.and(Filters.eq("word", word), Filters.eq("lang", langCode)))
								.first();

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
			}
		}

		@Override
		public void interrupt() {
			interrupted = true;
			System.out.println("Shutting " + this.getName() + " down...");
		}
	};

	/**
	 * Adds a word to the mongodb database
	 * 
	 * @param word
	 *            The word to be added
	 * @param lang
	 *            The Language the user is currently playing in
	 */
	public static void addWordToDatabase(String word, Language lang) {
		
		if (uploadThread.isAlive()==false){
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
