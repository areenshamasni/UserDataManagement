package edu.najah.cap.data.exportservice.toupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleDriveUploader implements FileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUploader.class);
    public void uploadFile(String filePath) {
        // Upload the file to Google Drive storage service
        // Code to upload file to Google Drive goes here
        logger.info("Uploading file to Google Drive storage service");
    }
}
