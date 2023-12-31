package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivityExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(ActivityExporter.class);

    @Override
    public List<Document> exportDoc(String username, MongoDatabase database) {
        List<Document> activities = new ArrayList<>();
        Document query = new Document("userId", username);
        MongoCollection<Document> collection = database.getCollection("activities");

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document activityDocument = cursor.next();
                activities.add(activityDocument);
            }
        }

        if (activities.isEmpty()) {
            logger.error("No activity data found for '{}'", username);
            return null;
        } else {
            logger.info("Activity data for '{}' exported from MongoDB", username);
        }
        return activities;
    }
}
