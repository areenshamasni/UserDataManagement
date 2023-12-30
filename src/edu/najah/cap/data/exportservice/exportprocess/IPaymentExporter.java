package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface IPaymentExporter {
    Document exportPaymentInformation(String username, MongoDatabase database);
}
