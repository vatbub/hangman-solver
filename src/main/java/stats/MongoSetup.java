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


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import common.AppConfig;
import org.bson.Document;

import java.io.IOException;
import java.net.InetAddress;

/**
 * This class sets up the connection to the
 * <a href="https://www.mongodb.com/">MongoDB</a> that saves the words used in
 * the Hangman Solver.
 *
 * @author frede
 * @see HangmanStats
 */
public class MongoSetup {

    private static final MongoClient mongoClient = new MongoClientImpl(AppConfig.mongoClientSettings, null);
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
     * up to 10 seconds if not.
     *
     * @return {@code true} if the
     * <a href="https://www.mongodb.com/">MongoDB</a> is reachable,
     * {@code false} otherwise.
     */
    public static boolean isReachable() {
        try {
            for (String host : AppConfig.mongoClientConnectionString.getHosts()) {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (!inetAddress.isReachable(10000))
                    return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
