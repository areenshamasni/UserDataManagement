package edu.najah.cap.data.exportservice;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportservice.converting.FileCompressor;
import edu.najah.cap.data.exportservice.converting.PdfConverter;
import edu.najah.cap.data.exportservice.exportprocess.PaymentExporter;
import edu.najah.cap.data.exportservice.exportprocess.UserProfileExporter;
import edu.najah.cap.data.exportservice.todownload.localStorage;
import edu.najah.cap.data.exportservice.toupload.FileUploadStrategy;
import edu.najah.cap.iam.UserProfile;
import edu.najah.cap.iam.UserType;

import java.util.Arrays;
import java.util.List;

public class FileHandlingExportContext {
    private final UserProfileExporter userProfileExporter;
    private final PaymentExporter paymentExporter;
    private final PdfConverter pdfConverter;
    private final FileCompressor fileCompressor;
    private final localStorage localStorage;
    private final FileUploadStrategy fileUploadStrategy;
    public FileHandlingExportContext(
            UserProfileExporter userProfileExporter,
            PaymentExporter paymentExporter,
            PdfConverter pdfConverter,
            FileCompressor fileCompressor,
            localStorage localStorage, FileUploadStrategy fileUploadStrategy) {
        this.userProfileExporter = userProfileExporter;
        this.paymentExporter = paymentExporter;
        this.pdfConverter = pdfConverter;
        this.fileCompressor = fileCompressor;
        this.localStorage = localStorage;
        this.fileUploadStrategy = fileUploadStrategy;
    }

    public void exportData(String username, MongoDatabase database) {
        UserProfile userProfile = userProfileExporter.exportUserProfile(username, database);
        pdfConverter.convertToPdf(userProfile, "UserProfile.pdf");

        if (userProfile.getUserType() == UserType.PREMIUM_USER) {
            paymentExporter.exportPaymentInformation(username, database);
            pdfConverter.convertToPdf(userProfile, "PaymentInfo.pdf");
            fileCompressor.compressFiles(List.of("UserProfile.pdf", "PaymentInfo.pdf"), "ExportedFiles.zip");
        } else {
            fileCompressor.compressFiles(List.of("UserProfile.pdf"), "ExportedFiles.zip");
        }
    }
    public void exportAndDownload(String username, MongoDatabase database) {
        exportData(username, database);
        localStorage.downloadFile("ExportedFiles.zip");
    }

    public void exportAndUpload(String username, MongoDatabase database) {
        exportData(username, database);
        fileUploadStrategy.uploadFile("ExportedFiles.zip");
    }
}
