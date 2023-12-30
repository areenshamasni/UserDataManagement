package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentExporter implements IDocExporter {
    private static final Logger logger = LoggerFactory.getLogger(PaymentExporter.class);
    @Override
    public Document exportDoc(String username, MongoDatabase database) {
        Document query = new Document("userName", username);
        MongoCollection<Document> collection = database.getCollection("payments");
        MongoCursor<Document> cursor = collection.find(query).iterator();
        Document paymentDocument;
        try {
            if (cursor.hasNext()) {
                paymentDocument = cursor.next();
                logger.info("Payment Information exported for {}", username);
                return paymentDocument;
            } else {
                logger.error("'{}' Payments information not found", username);
                return null;
            }
        } finally {
            cursor.close();
        }
    }
}
