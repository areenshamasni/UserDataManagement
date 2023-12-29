package edu.najah.cap.data.exportservice.toupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleDriveUploader implements FileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUploader.class);
    public void uploadFile(String filePath) {
        logger.info("Uploading file to Google Drive storage service");
    }
}
