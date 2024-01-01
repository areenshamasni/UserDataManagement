package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserProfExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(UserProfExporter.class);

    @Override
    public List<Document> exportDoc(MongoDatabase database, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("User ID is null or empty. Cannot export user profile data.");
            return new ArrayList<>();
        }

        List<Document> documents = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("users");
            Document userProfile = collection.find(Filters.eq("userId", userId)).first();

            if (userProfile == null) {
                logger.info("No user profile found for userId: {}", userId);
                return documents;
            }

            if(userProfile.getString("firstName") == null && userProfile.getString("lastName") == null) {
                Document basicInfo = new Document()
                        .append("userId", userProfile.getString("userId"))
                        .append("email", userProfile.getString("email"));
                documents.add(basicInfo);
                logger.info("User with userId: {} has been soft deleted. Exporting limited data.", userId);
            }
            else{
                documents.add(userProfile);
            }
            logger.info("Successfully exported user profile for userId: {}", userId);
        } catch (MongoException e) {
            logger.error("MongoDB exception occurred while exporting user profile data for userId {}: {}", userId, e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while exporting user profile data for userId {}: {}", userId, e.getMessage());
        }
        return documents;
    }
}