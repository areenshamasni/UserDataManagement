package edu.najah.cap.data.deleteservice.exceptionhandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UserDataBackup implements IDataBackup {
    private final MongoDatabase database;
    private static final Logger logger = LoggerFactory.getLogger(UserDataBackup.class);
    public UserDataBackup(MongoDatabase database) {
        this.database = database;
    }
    @Override
    public Map<String, List<Document>> backupUserData(String userName) {
        Map<String, List<Document>> userBackupData = new HashMap<>();
        backupCollection(userBackupData, "users", "userId", userName);
        backupCollection(userBackupData, "activities", "userId", userName);
        backupCollection(userBackupData, "payments", "userName", userName);
        backupCollection(userBackupData, "posts", "Author", userName);
        logger.info("User data for {} backed up successfully", userName);
        return userBackupData;
    }
    private void backupCollection(Map<String, List<Document>> userBackupData, String collectionName, String fieldName, String userName) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            List<Document> documents = new ArrayList<>();

            for (Document document : collection.find(new Document(fieldName, userName))) {
                documents.add(document);
            }
            userBackupData.put(collectionName, documents);
            logger.info("Backed up {} documents from {}", documents.size(), collectionName);
        } catch (Exception e) {
            logger.error("Error backing up collection: {} for user: {}", collectionName, userName, e);
        }
    }
}
