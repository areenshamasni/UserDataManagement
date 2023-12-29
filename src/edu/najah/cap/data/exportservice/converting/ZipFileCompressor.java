package edu.najah.cap.data.exportservice.converting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZipFileCompressor implements FileCompressor {
    private static final Logger logger = LoggerFactory.getLogger(ZipFileCompressor.class);
    @Override
    public void compressFiles(List<String> filePaths, String outputPath) {
        logger.info("Compressing {}" , filePaths);
    }
}


