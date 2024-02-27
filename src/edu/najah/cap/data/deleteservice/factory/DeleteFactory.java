package edu.najah.cap.data.deleteservice.factory;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.deleteservice.HardDelete;
import edu.najah.cap.data.deleteservice.IDeleteService;
import edu.najah.cap.data.deleteservice.SoftDelete;
import edu.najah.cap.data.exceptionhandler.IDataBackup;
import edu.najah.cap.data.exceptionhandler.IDataRestore;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteFactory {
    private static final Logger logger = Logger.getLogger(DeleteFactory.class.getName());

    public static IDeleteService createInstance(DeleteType deleteType, MongoDatabase database, IDataRestore dataRestore, IDataBackup dataBackup) {
        switch (deleteType) {
            case HARD:
                return new HardDelete(database, dataRestore, dataBackup);
            case SOFT:
                return new SoftDelete(database, dataRestore, dataBackup);
            default:
                logger.log(Level.WARNING, "Invalid delete type: {0}", deleteType);
        }
        return null;
    }
}
