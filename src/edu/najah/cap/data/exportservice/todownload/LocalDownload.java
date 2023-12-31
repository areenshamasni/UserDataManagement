package edu.najah.cap.data.exportservice.todownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LocalDownload implements ILocalStorage {
    private static final Logger logger = LoggerFactory.getLogger(LocalDownload.class);

    private final String downloadDirectory;

    public LocalDownload(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    @Override
    public void downloadFile(String filePath) {
        File sourceFile = new File(filePath);
//        if (!sourceFile.exists()) {
//            logger.error("File does not exist: {}", filePath);
//            return;
//        }

        File destinationFile = new File(downloadDirectory, sourceFile.getName());
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("File downloaded successfully to {}", destinationFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error occurred during file download: {}", e.getMessage());
        }
    }
}
