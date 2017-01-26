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

	private static final MongoClient mongoClient = new MongoClient(AppConfig.mongoDBServerAddress);
	private static final MongoDatabase mongoDatabase = mongoClient.getDatabase(AppConfig.mongoDBDatabaseName);
	private static final MongoCollection<Document> wordsUsedCollection = mongoDatabase
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
