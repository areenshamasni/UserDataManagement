package edu.najah.cap.data.exportservice.converting;

import java.util.List;


public interface IFileCompressor {
    void compressFiles(List<String> filePaths, String outputPath);
}
