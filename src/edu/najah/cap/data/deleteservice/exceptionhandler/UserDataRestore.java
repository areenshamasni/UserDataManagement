package edu.najah.cap.data.deleteservice.exceptionhandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
public class UserDataRestore implements IDataRestore {
    private static final Logger logger = LoggerFactory.getLogger(UserDataRestore.class);
    private final MongoDatabase database;
    private final IDataBackup backup;

    public UserDataRestore(MongoDatabase database, IDataBackup backup) {
        this.database = database;
        this.backup = backup;
    }

    @Override
    public void restoreUserData(String userName) {
        Map<String, List<Document>> userBackup = backup.backupUserData(userName);
        if (userBackup == null) {
            logger.warn("No backup found for user: {}", userName);
            return;
        }
        for (String collectionName : userBackup.keySet()) {
            List<Document> documents = userBackup.get(collectionName);
            if (documents != null) {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                for (Document document : documents) {
                    try {
                        collection.insertOne(document);

                    } catch (Exception e) {
                        logger.error("Error inserting document in collection '{}', user: {}", collectionName, userName, e);
                    }
                }
                logger.info("Inserted document in collection '{}', user: {}", collectionName, userName);
            }
        }
        logger.info("User data for {} restored successfully", userName);
    }
}
