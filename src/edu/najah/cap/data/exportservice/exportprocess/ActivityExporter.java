package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivityExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(ActivityExporter.class);

    @Override
    public List<Document> exportDoc(MongoDatabase database, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("User ID is null or empty. Cannot export activity data.");
            return new ArrayList<>();
        }
        List<Document> documents;
        try {
            MongoCollection<Document> collection = database.getCollection("activities");
            documents = collection.find(Filters.eq("userId", userId)).into(new ArrayList<>());
            if (documents.isEmpty()) {
                logger.info("No activity data found for userId: {}", userId);
            } else {
                logger.info("Successfully exported {} activity documents for userId: {}", documents.size(), userId);
            }
        } catch (Exception e) {
            logger.error("An error occurred while exporting activity data for userId {}: {}", userId, e.getMessage());
            return new ArrayList<>();
        }
        return documents;
    }
}
