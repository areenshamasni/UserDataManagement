package edu.najah.cap.data.exportservice;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportservice.converting.IFileCompressor;
import edu.najah.cap.data.exportservice.converting.IPdfConverter;
import edu.najah.cap.data.exportservice.exportprocess.IPaymentExporter;
import edu.najah.cap.data.exportservice.exportprocess.IUserProfileExporter;
import edu.najah.cap.data.exportservice.todownload.ILocalStorage;
import edu.najah.cap.data.exportservice.toupload.IFileUploadStrategy;
import edu.najah.cap.iam.UserType;
import org.bson.Document;

import java.util.List;

public class FileHandlingExportContext {
    private final IUserProfileExporter IUserProfileExporter;
    private final IPaymentExporter IPaymentExporter;
    private final IPdfConverter IPdfConverter;
    private final IFileCompressor IFileCompressor;
    private final ILocalStorage ILocalStorage;
    private final IFileUploadStrategy IFileUploadStrategy;
    public FileHandlingExportContext(
            IUserProfileExporter IUserProfileExporter,
            IPaymentExporter IPaymentExporter,
            IPdfConverter IPdfConverter,
            IFileCompressor IFileCompressor,
            ILocalStorage ILocalStorage, IFileUploadStrategy IFileUploadStrategy) {
        this.IUserProfileExporter = IUserProfileExporter;
        this.IPaymentExporter = IPaymentExporter;
        this.IPdfConverter = IPdfConverter;
        this.IFileCompressor = IFileCompressor;
        this.ILocalStorage = ILocalStorage;
        this.IFileUploadStrategy = IFileUploadStrategy;
    }

    public void exportData(String username, MongoDatabase database) {
        Document userProfile = IUserProfileExporter.exportUserProfile(username, database);
        String userTypeString = userProfile.getString("userType");
        UserType userType = UserType.valueOf(userTypeString);
        IPdfConverter.convertToPdf(userProfile, "UserProfile.pdf");

        if (userType == UserType.PREMIUM_USER) {
            IPaymentExporter.exportPaymentInformation(username, database);
            IPdfConverter.convertToPdf(userProfile, "PaymentInfo.pdf");
            IFileCompressor.compressFiles(List.of("UserProfile.pdf", "PaymentInfo.pdf"), "ExportedFiles.zip");
        } else {
            IFileCompressor.compressFiles(List.of("UserProfile.pdf"), "ExportedFiles.zip");
        }
    }
    public void exportAndDownload(String username, MongoDatabase database) {
        exportData(username, database);
        ILocalStorage.downloadFile("ExportedFiles.zip");
    }

    public void exportAndUpload(String username, MongoDatabase database) {
        exportData(username, database);
        IFileUploadStrategy.uploadFile("ExportedFiles.zip");
    }
}
