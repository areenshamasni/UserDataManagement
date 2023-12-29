package edu.najah.cap.data.exportservice.converting;

import edu.najah.cap.iam.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// PaymentPdfConverter.java
public class PaymentPdfConverter implements PdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(PaymentPdfConverter.class);
    @Override
    public void convertToPdf(UserProfile userProfile, String outputPath) {
        // Implementation to convert payment information to PDF
        logger.info("Converting payment information to PDF");
    }
}
