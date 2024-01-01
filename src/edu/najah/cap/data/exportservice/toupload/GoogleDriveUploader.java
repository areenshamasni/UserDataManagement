package edu.najah.cap.data.exportservice.toupload;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveUploader implements IFileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUploader.class);

    public void uploadFile(String filePath, String outputPath) {
        try {
            Drive driveService = GoogleDriveServiceInitializer.initializeDriveService();

            // Create file metadata
            File fileMetadata = new File();
            fileMetadata.setName("UserData.zip");
            fileMetadata.setParents(Collections.singletonList(outputPath));

            // Create media content
            FileContent mediaContent = new FileContent("application/zip", new java.io.File(filePath));

            // Upload the file
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();

            // Print the ID and name of the uploaded file
            System.out.println("File ID: " + uploadedFile.getId());
            System.out.println("File Name: " + uploadedFile.getName());
            logger.info("Uploading file to Google Drive storage service completed successfully");
        } catch (IOException | GeneralSecurityException e) {
            logger.error("Error uploading file to Google Drive: {}", e.getMessage());
        }

    }
}
