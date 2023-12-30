package edu.najah.cap.data.exportservice.converting;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class UserProfileIPdfConverter implements IPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileIPdfConverter.class);
    @Override
    public File convertToPdf(Document document, String outputPath) {
        File file = new File(outputPath);
      logger.info("Converting user profile to PDF");
      return file;
    }
}
