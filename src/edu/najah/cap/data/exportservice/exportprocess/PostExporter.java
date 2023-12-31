package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PostExporter implements IDocExporter {
    public static final Logger logger = LoggerFactory.getLogger(PostExporter.class);

    @Override
    public List<Document> exportDoc(String username, MongoDatabase database) {
        List<Document> posts = new ArrayList<>();
        Document query = new Document("Author", username);
        MongoCollection<Document> collection = database.getCollection("posts");

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document postDocument = cursor.next();
                posts.add(postDocument);
            }
        }

        if (posts.isEmpty()) {
            logger.error("No posts found for '{}'", username);
            return null;
        } else {
            logger.info("Posts for '{}' exported from MongoDB", username);
        }
        return posts;
    }
}
