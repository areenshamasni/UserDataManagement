package edu.najah.cap.data.deleteservice.exceptionhandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.najah.cap.data.mongodb.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public class UserDataRestore implements IDataRestore {
    private static final Logger logger = LoggerFactory.getLogger(UserDataRestore.class);
    private final MongoDatabase dataBase;
    private final IDataBackup backup;
    private final UserMapper userMapper;
    private final UserActivityMapper userActivityMapper;
    private final TransactionMapper transactionMapper;
    private final PostMapper postMapper;
    private final MongoDataInserter mongoDataInserter;

    public UserDataRestore(MongoDatabase database, IDataBackup backup,
            UserMapper userMapper, UserActivityMapper userActivityMapper,
             TransactionMapper transactionMapper, PostMapper postMapper, MongoDataInserter mongoDataInserter )
    {
        this.dataBase = database;
        this.backup = backup;
        this.userMapper = userMapper;
        this.userActivityMapper = userActivityMapper;
        this.transactionMapper = transactionMapper;
        this.postMapper = postMapper;
        this.mongoDataInserter = mongoDataInserter;
    }
    @Override
    public void restoreUserData(String userName) {
        Map<String, List<Document>> userBackup = backup.getUserBackupData(userName);

        if (userBackup == null) {
            logger.warn("No backup found for user: {}", userName);
            return;
        }

        for (String collectionName : userBackup.keySet()) {
            List<Document> documents = userBackup.get(collectionName);
            if (documents != null) {
                for (Document doc : documents) {
                    try {
                        String fieldName = getFieldNameForCollection(collectionName);
                        Bson filter = Filters.eq(fieldName, userName);
                        MongoCollection<Document> collection = dataBase.getCollection(collectionName);
                        collection.deleteMany(filter);
                        collection.insertOne(doc);
                        logger.info("Restored document in collection '{}', user: {}", collectionName, userName);
                    } catch (Exception e) {
                        logger.error("Error restoring document in collection '{}', user: {}", collectionName, userName, e);
                    }
                }
            }
        }

        logger.info("User data for {} restored successfully", userName);
    }
    private String getFieldNameForCollection(String collectionName) {
        switch (collectionName) {
            case "users", "activities":
                return "userId";
            case "posts":
                return "Author";
            case "payments":
                return "userName";
            default:
                return "userId";
        }
    }

}
