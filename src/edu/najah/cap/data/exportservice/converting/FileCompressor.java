package edu.najah.cap.data.exportservice.converting;

import java.util.List;


public interface FileCompressor {
    void compressFiles(List<String> filePaths, String outputPath);
}
