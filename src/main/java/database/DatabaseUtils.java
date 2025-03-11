package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import utils.EnvUtils;

import java.util.ArrayList;
import java.util.List;

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

    // Method to get chatter messages
    public static List<String> getChatter() {
        List<String> chatterMessages = new ArrayList<>();
        MongoDatabase database = getDatabase(EnvUtils.getEnv("DB_NAME"));
        MongoCollection<Document> collection = database.getCollection("chatter");
        FindIterable<Document> documents = collection.find();
        for (Document doc : documents) {
            chatterMessages.add(doc.getString("message"));
        }
        return chatterMessages;
    }
}