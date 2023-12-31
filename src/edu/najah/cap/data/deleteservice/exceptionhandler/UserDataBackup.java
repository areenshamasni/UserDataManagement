package edu.najah.cap.data.deleteservice.exceptionhandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UserDataBackup implements IDataBackup {
    private static final Logger logger = LoggerFactory.getLogger(UserDataBackup.class);
    private final MongoDatabase database;
    private final Map<String, Map<String, List<Document>>> userBackupData;

    public UserDataBackup(MongoDatabase database) {
        this.database = database;
        this.userBackupData = new HashMap<>();
    }

    @Override
    public void backupUserData(String userName) {
        Map<String, List<Document>> userBackup = Collections.synchronizedMap(new HashMap<>());
        backupCollection("users", userName, userBackup);
        backupCollection("activities", userName, userBackup);
        backupCollection("payments", userName, userBackup);
        backupCollection("posts", userName, userBackup);
        userBackupData.put(userName, userBackup);
        logger.info("User data for {} backed up successfully", userName);
    }

    private void backupCollection(String collectionName, String userName, Map<String, List<Document>> userBackup) {
        try {
            MongoCollection<Document> collection = database.getCollection(collectionName);
            String fieldName = getFieldNameForCollection(collectionName);
            List<Document> documents = collection.find(Filters.eq(fieldName, userName)).into(new ArrayList<>());
            userBackup.put(collectionName, documents);
        } catch (Exception e) {
            logger.error("Error backing up collection: {} for user: {}", collectionName, userName, e);
        }
    }

    private String getFieldNameForCollection(String collectionName) {
        switch (collectionName) {
            case "users":
            case "activities":
                return "userId";
            case "posts":
                return "Author";
            case "payments":
                return "userName";
            default:
                return "userId";
        }
    }
@Override
    public Map<String, List<Document>> getUserBackupData(String userName) {
        return userBackupData.get(userName);
    }
}
