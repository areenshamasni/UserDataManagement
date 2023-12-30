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
import java.util.List;

public class FileExportContext {
    private final IDocExporter userProfileExporter;
    private final IDocExporter paymentExporter;
    private final IPdfConverter pdfConverter;
    private final IFileCompressor fileCompressor;
    private final ILocalStorage localStorage;
    private final IFileUploadStrategy fileUploadStrategy;
    public FileExportContext(
            IDocExporter userProfileExporter,
            IDocExporter paymentExporter,
            IPdfConverter pdfConverter,
            IFileCompressor fileCompressor,
            ILocalStorage localStorage, IFileUploadStrategy fileUploadStrategy) {
        this.userProfileExporter = userProfileExporter;
        this.paymentExporter = paymentExporter;
        this.pdfConverter = pdfConverter;
        this.fileCompressor = fileCompressor;
        this.localStorage = localStorage;
        this.fileUploadStrategy = fileUploadStrategy;
    }

    public void exportData(String username, MongoDatabase database) {
        Document userProfile = userProfileExporter.exportDoc(username, database);
        String userTypeString = userProfile.getString("userType");
        UserType userType = UserType.valueOf(userTypeString);
        pdfConverter.convertToPdf(userProfile, "UserProfile.pdf");

        if (userType == UserType.PREMIUM_USER) {
            paymentExporter.exportDoc(username, database);
            File paymentFile= pdfConverter.convertToPdf(userProfile, "PaymentInfo.pdf");
            fileCompressor.compressFiles((List<File>) paymentFile, "ExportedFiles.zip");
        } else {
            fileCompressor.compressFiles(List.of(new File("UserProfile.pdf")), "ExportedFiles.zip");
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
