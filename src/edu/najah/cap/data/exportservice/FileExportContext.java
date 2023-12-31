package edu.najah.cap.data.exportservice;

import com.itextpdf.text.DocumentException;
import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportservice.converting.IFileCompressor;
import edu.najah.cap.data.exportservice.converting.IPdfConverter;
import edu.najah.cap.data.exportservice.exportprocess.IDocExporter;
import edu.najah.cap.data.exportservice.todownload.ILocalStorage;
import edu.najah.cap.data.exportservice.toupload.IFileUploadStrategy;
import edu.najah.cap.iam.UserType;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
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
        logger.info("FileExportContext initialized");
    }
    public void processData(String username, MongoDatabase database) {
        try {
            try {
                logger.info("Processing data for username: {}", username);
                Document userProfile = userProfileExporter.exportDoc(database, username).get(0);
                String userTypeString = userProfile.getString("userType");
                UserType userType = UserType.valueOf(userTypeString);

                List<Document> posts = postExporter.exportDoc(database, username);
                List<Document> activities = activityExporter.exportDoc(database, username);
                List<Document> payments = paymentExporter.exportDoc(database, username);

                String userActivityPdfPath =  username +"_details" + ".pdf";
                String paymentDetailsPdfPath =  username +"_payment" + ".pdf";

                List<File> generatedPdfFiles = new ArrayList<>();

                List<Document> userData = new ArrayList<>();
                userData.add(userProfile);
                userData.addAll(posts);

                if(userType!=UserType.NEW_USER) {
                    userData.addAll(activities);
                }
                if (userType==UserType.PREMIUM_USER){
                    File premiumDetailsPdf = pdfConverter.convertToPdf(payments, paymentDetailsPdfPath);
                    generatedPdfFiles.add(premiumDetailsPdf);
                }
                File UserInfoPdf = pdfConverter.convertToPdf(userData, userActivityPdfPath);
                generatedPdfFiles.add(UserInfoPdf);

                File zipFile = fileCompressor.compressFiles(generatedPdfFiles,  username + ".zip");
                localDownload.downloadFile(zipFile.getAbsolutePath());

            } catch (IllegalArgumentException | FileNotFoundException | DocumentException e) {
                logger.error("Exception occurred: ", e);
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            logger.error("Error during data export for user '{}': {}", username, e.getMessage());
        }
    }
    public void exportAndDownload(String username, MongoDatabase database) {
        logger.info("Exporting and downloading data for username: {}", username);
        processData(username, database);
        localDownload.downloadFile(username + ".zip");
    }
    public void exportAndUpload(String username, MongoDatabase database) {
        logger.info("Exporting and uploading data for username: {}", username);
        processData(username, database);
        fileUploadStrategy.uploadFile(username + ".zip");
    }
}