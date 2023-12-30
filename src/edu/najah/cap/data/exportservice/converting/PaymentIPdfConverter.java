package edu.najah.cap.data.exportservice.converting;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// PaymentPdfConverter.java
public class PaymentIPdfConverter implements IPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(PaymentIPdfConverter.class);
    @Override
    public void convertToPdf(Document document, String outputPath) {
        // Implementation to convert payment information to PDF
        logger.info("Converting payment information to PDF");
    }
}
