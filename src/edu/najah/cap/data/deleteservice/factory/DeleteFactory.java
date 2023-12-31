package edu.najah.cap.data.deleteservice.factory;
import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.deleteservice.HardDelete;
import edu.najah.cap.data.deleteservice.IDeleteService;
import edu.najah.cap.data.deleteservice.SoftDelete;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataBackup;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteFactory {
    private static final Logger logger = Logger.getLogger(DeleteFactory.class.getName());
    public static IDeleteService createInstance(DeleteType deleteType, MongoDatabase database,  IDataRestore dataRestore) {
        switch (deleteType) {
            case HARD:
                return new HardDelete(database,  dataRestore);
            case SOFT:
                return new SoftDelete(database,  dataRestore);
            default:
                logger.log(Level.WARNING, "Invalid delete type: {0}", deleteType);
        }
        return null;
    }
}
