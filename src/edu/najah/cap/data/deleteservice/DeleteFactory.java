package edu.najah.cap.data.deleteservice;
import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataBackup;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteFactory {
    private static final Logger logger = Logger.getLogger(DeleteFactory.class.getName());

    public static IDeleteService createInstance(DeleteType deleteType, MongoDatabase database, IDataBackup dataBackup, IDataRestore dataRestore) {
        switch (deleteType) {
            case HARD:
                return new HardDelete(database, dataBackup, dataRestore);
            case SOFT:
                return new SoftDelete(database, dataBackup, dataRestore);
            default:
                logger.log(Level.WARNING, "Invalid delete type: {0}", deleteType);
        }
        return null;
    }
}
