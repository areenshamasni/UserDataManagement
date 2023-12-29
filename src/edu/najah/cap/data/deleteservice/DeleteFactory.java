package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.mongodb.MongoConnection;

public class DeleteFactory {
    public static IDeleteService createInstance(DeleteType deleteType, MongoDatabase database) {
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
