package edu.najah.cap.data.exportservice.todownload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class localDownload implements localStorage{
    private static final Logger logger = LoggerFactory.getLogger(localDownload.class);
    @Override
    public void downloadFile(String filePath) {
        logger.info("Downloading file {}" , filePath);
    }
}
