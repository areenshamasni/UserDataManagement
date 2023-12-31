package edu.najah.cap.data.exportservice.converting;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

// PaymentPdfConverter.java
public class PaymentIPdfConverter implements IPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(PaymentIPdfConverter.class);
    @Override
    public File convertToPdf(Document document, String outputPath) {
        File file = new File(outputPath);
        // Implementation to convert payment information to PDF
        logger.info("Converting payment information to PDF");
        return file;
    }
}
