package edu.najah.cap.data.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoConnection {
    private static MongoConnection instance;

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);

    private MongoConnection(String connectionString, String databaseName) throws MongoException {
        try {
            this.mongoClient = MongoClients.create(connectionString);
            this.database = mongoClient.getDatabase(databaseName);
            logger.info("Connected to {}" , databaseName);
        } catch (MongoException e) {
            logger.error("Failed to connect to {}", databaseName);
            throw e;
        }
    }

    public static synchronized MongoConnection getInstance(String connectionString, String databaseName) throws MongoException {
        if (instance == null)
            instance = new MongoConnection(connectionString, databaseName);
        return instance;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public void closeMongoClient() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
            } catch (MongoException e) {
                logger.error("Failed to close MongoDB client: {}", e.getMessage());
            }
        }
    }
}
