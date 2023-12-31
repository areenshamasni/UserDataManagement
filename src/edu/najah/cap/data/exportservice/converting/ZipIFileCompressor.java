package edu.najah.cap.data.exportservice.converting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipIFileCompressor implements IFileCompressor {
    private static final Logger logger = LoggerFactory.getLogger(ZipIFileCompressor.class);

    @Override
    public File compressFiles(List<File> files, String outputPath) {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputPath))) {
            for (File fileToZip : files) {
                if (fileToZip == null || !fileToZip.exists()) {
                    logger.error("File not found, skipping: {}", fileToZip);
                    continue;
                }

                zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zipOut.write(buffer, 0, length);
                    }
                }
                zipOut.closeEntry();
            }

            logger.info("Successfully compressed files into {}", outputPath);
            return new File(outputPath);
        } catch (IOException e) {
            logger.error("Error occurred during file compression: {}", e.getMessage());
            return null;
        }
    }
}
