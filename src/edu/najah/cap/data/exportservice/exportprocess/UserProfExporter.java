package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserProfExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(UserProfExporter.class);

    @Override
    public List<Document> exportDoc(String username, MongoDatabase database) {
        List<Document> userProfiles = new ArrayList<>();
        Document query = new Document("userId", username);
        MongoCollection<Document> collection = database.getCollection("users");

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document userProfileDocument = cursor.next();
                userProfiles.add(userProfileDocument);
            }
        }

        if (userProfiles.isEmpty()) {
            logger.error("User profile for '{}' not found", username);
            return null; // or return an empty list based on your requirement
        } else {
            logger.info("User profile(s) for '{}' exported from MongoDB", username);
        }
        return userProfiles;
    }
}
