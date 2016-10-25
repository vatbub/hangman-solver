package stats;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.*;

import common.AppConfig;

/**
 * This class sets up the connection to the
 * <a href="https://www.mongodb.com/">MongoDB</a> that saves the words used in
 * the Hangman Solver.
 * 
 * @see HangmanStats
 * @author frede
 *
 */
public class MongoSetup {

	private static MongoClient mongoClient = new MongoClient(AppConfig.mongoDBServerAddress);
	private static MongoDatabase mongoDatabase = mongoClient.getDatabase(AppConfig.mongoDBDatabaseName);
	private static MongoCollection<Document> wordsUsedCollection = mongoDatabase
			.getCollection(AppConfig.mongoDBWordsUsedCollectionName);

	/**
	 * Returns the collection where the used words are saved.
	 * 
	 * @return The collection where the used words are saved.
	 */
	public static MongoCollection<Document> getWordsUsedCollection() {
		return wordsUsedCollection;
	}

	/**
	 * Closes the connection to the
	 * <a href="https://www.mongodb.com/">MongoDB</a> properly.
	 */
	public static void close() {
		mongoClient.close();
	}

	/**
	 * Checks if the <a href="https://www.mongodb.com/">MongoDB</a> is
	 * reachable.<br>
	 * NOTE: This method will take a very short time to run if the
	 * <a href="https://www.mongodb.com/">MongoDB</a> is reachable but will take
	 * up to 30 seconds if not due to the way the
	 * <a href="https://docs.mongodb.com/ecosystem/drivers/">MongoDB driver</a>
	 * works.
	 * 
	 * @return {@code true} if the
	 *         <a href="https://www.mongodb.com/">MongoDB</a> is reachable,
	 *         {@code false} otherwise.
	 */
	public static boolean isReachable() {
		try {
			mongoClient.getAddress();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
