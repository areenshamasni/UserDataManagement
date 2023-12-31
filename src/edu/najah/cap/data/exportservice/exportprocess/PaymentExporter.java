package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PaymentExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(PaymentExporter.class);

    @Override
    public List<Document> exportDoc(String username, MongoDatabase database) {
        List<Document> payments = new ArrayList<>();
        Document query = new Document("userName", username);
        MongoCollection<Document> collection = database.getCollection("payments");

        try (MongoCursor<Document> cursor = collection.find(query).iterator()) {
            while (cursor.hasNext()) {
                Document paymentDocument = cursor.next();
                payments.add(paymentDocument);
            }
        }

        if (payments.isEmpty()) {
            logger.info("No payment information found for '{}'", username); // Changed to info level
            // No need to return null; an empty list can be returned
        } else {
            logger.info("Payment information for '{}' exported from MongoDB", username);
        }
        return payments; // Always return the list, whether empty or full
    }
}
