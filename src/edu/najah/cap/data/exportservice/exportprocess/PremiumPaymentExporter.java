package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportservice.converting.UserProfilePdfConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PremiumPaymentExporter implements PaymentExporter {
    private static final Logger logger = LoggerFactory.getLogger(PremiumPaymentExporter.class);
    @Override
    public void exportPaymentInformation(String username, MongoDatabase database) {
        logger.info("export Payment Information for {}", username);

    }
}
