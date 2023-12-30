package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;

public interface PaymentExporter {
    void exportPaymentInformation(String username,  MongoDatabase database);
}
