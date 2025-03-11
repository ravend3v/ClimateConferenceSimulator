package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import utils.EnvUtils;

public class CreateChatterCollection {

    private static final String COLLECTION_NAME = "chatter";

    public static void createCollection() {
        MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
        if (database.getCollection(COLLECTION_NAME) == null) {
            database.createCollection(COLLECTION_NAME);
        }
    }

    public static void insertChatterDocuments() {
        MongoDatabase database = DatabaseUtils.getDatabase(EnvUtils.getEnv("DB_NAME"));
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        List<Document> chatterDocuments = Arrays.asList(
                new Document("message", "Welcome to the Climate Conference!"),
                new Document("message", "Main Stage: Discussing Fossil Fuels vs Renewable Fuels."),
                new Document("message", "Speaker 1: Fossil fuels are non-renewable resources."),
                new Document("message", "Speaker 2: Renewable fuels are sustainable."),
                new Document("message", "Speaker 1: Transitioning to renewable energy is crucial."),
                new Document("message", "Speaker 2: The impact of fossil fuels on climate change is significant."),
                new Document("message", "Speaker 1: Benefits of renewable energy sources include reduced emissions."),
                new Document("message", "Audience: What are the challenges in transitioning to renewable energy?"),
                new Document("message", "Speaker 2: Challenges include infrastructure and initial costs."),
                new Document("message", "Speaker 1: However, long-term benefits outweigh the challenges."),
                new Document("message", "Audience: How can we accelerate the transition?"),
                new Document("message", "Speaker 2: Policies and incentives can play a major role."),
                new Document("message", "Speaker 1: Public awareness and education are also key.")
        );

        collection.insertMany(chatterDocuments);
    }

    public static void main(String[] args) {
        createCollection();
        insertChatterDocuments();
    }
}
