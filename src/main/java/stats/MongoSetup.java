package stats;

import java.io.Closeable;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.*;

import common.Config;

public class MongoSetup {

	private static MongoClient mongoClient = new MongoClient(Config.mongoDBServerAddress);
	private static MongoDatabase mongoDatabase = mongoClient.getDatabase(Config.mongoDBDatabaseName);
	private static MongoCollection<Document> wordsUsedCollection = mongoDatabase.getCollection(Config.mongoDBWordsUsedCollectionName);
	
	public static MongoCollection<Document> getWordsUsedCollection(){
		return wordsUsedCollection;
	}

	public static void close() {
		mongoClient.close();
	}

	
}
