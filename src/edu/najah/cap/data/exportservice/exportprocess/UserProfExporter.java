package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserProfExporter implements IUserProfileExporter {
    private static final Logger logger = LoggerFactory.getLogger(UserProfExporter.class);
    @Override
    public Document exportUserProfile(String username, MongoDatabase database) {
        Document query = new Document("userId", username);
        MongoCollection<Document> collection = database.getCollection("users");
        MongoCursor<Document> cursor = collection.find(query).iterator();
        Document userDocument;
        try {
            if (cursor.hasNext()) {
                userDocument = cursor.next();
                logger.info("User Profile for '{}' exported from mongodb", username);
                return userDocument;
            } else {
                logger.error("'{}' Profile not found", username);
                return null;
            }
        } finally {
            cursor.close();
        }
    }
}
