package edu.najah.cap.data.exportservice.toupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropboxUploader implements IFileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DropboxUploader.class);
    public void uploadFile(String filePath) {
        logger.info("Uploading file to Dropbox storage service");
    }
}
