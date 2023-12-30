package edu.najah.cap.data.exportServices.pdfAndZip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class ZipFileService {
    public static void createZipFile(List<String> filePaths, String zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String filePath : filePaths) {
                File fileToZip = new File(filePath);
                if (!fileToZip.exists()) {
                    System.err.println("File not found, skipping: " + filePath);
                    continue;
                }
                try {
                    zos.putNextEntry(new ZipEntry(fileToZip.getName()));
                    Files.copy(Paths.get(filePath), zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    System.err.println("Error processing file " + filePath + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred during ZIP file creation: " + e.getMessage());
            throw e;
        }
    }
}