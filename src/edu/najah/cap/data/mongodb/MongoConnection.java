package edu.najah.cap.data.mongodb;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoConnection instance;

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    private MongoConnection(String connectionString, String databaseName) throws MongoException {
        try {
            this.mongoClient = MongoClients.create(connectionString);
            this.database = mongoClient.getDatabase(databaseName);
            System.out.println("Connected to " + databaseName);
        } catch (MongoException e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
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
                System.err.println("Error closing MongoDB client: " + e.getMessage());
            }
        }
    }
}