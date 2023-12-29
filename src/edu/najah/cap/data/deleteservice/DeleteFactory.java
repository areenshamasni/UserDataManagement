package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.mongodb.MongoConnection;

public class DeleteFactory {
    public static IDeleteService createInstance(DeleteType deleteType, String connectionString, String databaseName) {
        MongoDatabase database = MongoConnection.getInstance(connectionString, databaseName).getDatabase();
        switch (deleteType) {
            case HARD:
                return new HardDelete(database);
            case SOFT:
                return new SoftDelete(database);
            default:
                throw new IllegalArgumentException("Invalid delete type");
        }
    }
}
