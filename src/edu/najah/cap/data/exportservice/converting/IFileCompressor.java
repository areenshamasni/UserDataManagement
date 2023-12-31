package edu.najah.cap.data.exportservice.converting;
import java.io.File;
import java.io.IOException;
import java.util.List;
public interface IFileCompressor {
    File compressFiles(List<File> files, String outputPath)throws IOException;
}