package stats;

import java.util.*;

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

	/**
	 * Adds a word to the mongodb database
	 * 
	 * @param word
	 *            The word to be added
	 * @param lang
	 *            The Language the user is currently playing in
	 */
	public static void addWordToDatabase(String word, Language lang) {
		Thread t = new Thread() {
			@Override
			public void run() {

				if (!alreadySubmittedWordsInThisSession.contains(word)) {
					// word not submitted yet
					alreadySubmittedWordsInThisSession.add(word);
					System.out.println("Submitting word '" + word + "' to MongoDB...");
					MongoCollection<Document> coll = MongoSetup.getWordsUsedCollection();
					Document doc = coll.find(Filters.and(Filters.eq("word", word), Filters.eq("lang", lang.getLanguageCode()))).first();

					if (doc == null) {
						// word never added prior to this
						doc = new Document("word", word).append("lang", lang.getLanguageCode()).append("count", 1);
						coll.insertOne(doc);
					} else {
						// word already known in the database
						coll.updateOne(Filters.and(Filters.eq("word", word), Filters.eq("lang", lang.getLanguageCode())), Updates.inc("count", 1));
					}
					System.out.println("Submission done.");
				}
			}
		};
		t.start();
	}
}
