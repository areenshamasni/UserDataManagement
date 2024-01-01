package edu.najah.cap.data.exportservice.toupload;

import java.io.IOException;

public interface IFileUploadStrategy {
    void uploadFile(String filePath, String outputPath)throws IOException;
}
