package edu.najah.cap.data.exportservice.converting;

import java.io.File;
import java.util.List;


public interface IFileCompressor {
    void compressFiles(List<File> files, String outputPath);
}
