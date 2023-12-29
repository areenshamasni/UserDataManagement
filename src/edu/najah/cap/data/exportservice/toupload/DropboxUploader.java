package edu.najah.cap.data.exportservice.toupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropboxUploader implements FileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DropboxUploader.class);
    public void uploadFile(String filePath) {
        // Upload the file to Dropbox storage service
        // Code to upload file to Dropbox goes here
        logger.info("Uploading file to Dropbox storage service");
    }
}
