package edu.najah.cap.data.exportservice;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportservice.converting.IFileCompressor;
import edu.najah.cap.data.exportservice.converting.IPdfConverter;
import edu.najah.cap.data.exportservice.exportprocess.IDocExporter;
import edu.najah.cap.data.exportservice.todownload.ILocalStorage;
import edu.najah.cap.data.exportservice.toupload.IFileUploadStrategy;
import edu.najah.cap.iam.UserType;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static edu.najah.cap.data.exportservice.exportprocess.PostExporter.logger;

public class FileExportContext {
    private final IDocExporter userProfileExporter;
    private final IDocExporter postExporter;
    private final IDocExporter activityExporter;
    private final IDocExporter paymentExporter;
    private final IPdfConverter pdfConverter;
    private final IFileCompressor fileCompressor;
    private final ILocalStorage localDownload;
    private final IFileUploadStrategy fileUploadStrategy;
    public FileExportContext(
            IDocExporter userProfileExporter,
            IDocExporter postExporter,
            IDocExporter activityExporter,
            IDocExporter paymentExporter,
            IPdfConverter pdfConverter,
            IFileCompressor fileCompressor,
            ILocalStorage localDownload,
            IFileUploadStrategy fileUploadStrategy,
            IFileUploadStrategy dropboxUploader) {
        this.userProfileExporter = userProfileExporter;
        this.postExporter = postExporter;
        this.activityExporter = activityExporter;
        this.paymentExporter = paymentExporter;
        this.pdfConverter = pdfConverter;
        this.fileCompressor = fileCompressor;
        this.localDownload = localDownload;
        this.fileUploadStrategy = fileUploadStrategy;
    }

    public void exportData(String username, MongoDatabase database) {
        try {
            // Assume exportDoc returns a list and the user profile is the first document
            Document userProfile = userProfileExporter.exportDoc(username, database).get(0);
            List<Document> posts = postExporter.exportDoc(username, database);

            // Extract user type from the user profile document
            String userTypeString = userProfile.getString("userType");
            UserType userType = UserType.valueOf(userTypeString);

            List<File> filesToCompress = new ArrayList<>();

            if (userType == UserType.PREMIUM_USER) {
                // Export payment details for premium users
                List<Document> paymentInfo = paymentExporter.exportDoc(username, database);
                if (paymentInfo != null && !paymentInfo.isEmpty()) {
                    File paymentPdf = pdfConverter.convertToPdf(paymentInfo, "PaymentInfo_" + username + ".pdf");
                    filesToCompress.add(paymentPdf);
                }

                // Export premium user details
                List<Document> premiumDetails = new ArrayList<>();
                premiumDetails.add(userProfile);
                premiumDetails.addAll(posts);
                premiumDetails.addAll(activityExporter.exportDoc(username, database));
                File premiumDetailsPdf = pdfConverter.convertToPdf(premiumDetails, "PremiumDetails_" + username + ".pdf");
                filesToCompress.add(premiumDetailsPdf);

            } else {
                // Export regular or new user details
                List<Document> regularDetails = new ArrayList<>();
                regularDetails.add(userProfile);
                regularDetails.addAll(posts);
                if (userType == UserType.REGULAR_USER) {
                    regularDetails.addAll(activityExporter.exportDoc(username, database));
                }
                File regularDetailsPdf = pdfConverter.convertToPdf(regularDetails, "UserDetails_" + username + ".pdf");
                filesToCompress.add(regularDetailsPdf);
            }

            // Compress all generated PDF files
            File zipFile = fileCompressor.compressFiles(filesToCompress, "ExportedFiles_" + username + ".zip");

            // Perform the final action (download/upload)
            localDownload.downloadFile(zipFile.getAbsolutePath()); // for download
            // fileUploadStrategy.uploadFile(zipFile.getAbsolutePath());

        } catch (Exception e) {
            logger.error("Error during data export for user '{}': {}", username, e.getMessage());
        }
    }
    public void exportAndDownload(String username, MongoDatabase database) {
        exportData(username, database);
        localDownload.downloadFile("ExportedFiles.zip");
    }

    public void exportAndUpload(String username, MongoDatabase database) {
        exportData(username, database);
        fileUploadStrategy.uploadFile("ExportedFiles.zip");
    }
}
