package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoDatabase;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteFactory {
    private static final Logger logger = Logger.getLogger(DeleteFactory.class.getName());

    public static IDeleteService createInstance(DeleteType deleteType, MongoDatabase database) {
        switch (deleteType) {
            case HARD:
                return new HardDelete(database);
            case SOFT:
                return new SoftDelete(database);
            default:
                logger.log(Level.WARNING, "Invalid delete type: {0}", deleteType);
        }
        return null;
    }

}