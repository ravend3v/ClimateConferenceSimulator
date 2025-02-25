package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import utils.EnvUtils;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        try {
            String dbName = EnvUtils.getEnv("DB_NAME");
            MongoDatabase database = DatabaseUtils.getDatabase(dbName);

            MongoCollection<Document> collection = database.getCollection("testCollection");

            Document doc = new Document("name", "test")
                    .append("type", "database")
                    .append("count", 1)
                    .append("info", new Document("x", 203).append("y", 102));

            collection.insertOne(doc);

            System.out.println("Document inserted successfully!");
        } catch (Exception e) {
            System.err.println("An error occured: " + e.getMessage());
        }

    }
}