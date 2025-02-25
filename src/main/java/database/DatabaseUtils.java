package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import utils.EnvUtils;

public class DatabaseUtils {

    // Variables
    private static final String MONGODB_URI = EnvUtils.getEnv("MONGODB_URI");
    private static MongoClient mongoClient = null;

    // Method to get MongoDB client
    private static MongoClient getMongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(MONGODB_URI);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase(String dbName) {
        return getMongoClient().getDatabase(dbName);
    }
}