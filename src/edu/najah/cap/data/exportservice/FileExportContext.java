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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileExportContext {
    private static final Logger logger = LoggerFactory.getLogger(FileExportContext.class);
    private final IDocExporter userProfileExporter;
    private final IDocExporter postExporter;
    private final IDocExporter activityExporter;
    private final IDocExporter paymentExporter;
    private final IPdfConverter pdfConverter;
    private final IFileCompressor fileCompressor;
    private ILocalStorage localDownload;
    private IFileUploadStrategy fileUploadStrategy;

    public FileExportContext(
            IDocExporter userProfileExporter,
            IDocExporter postExporter,
            IDocExporter activityExporter,
            IDocExporter paymentExporter,
            IPdfConverter pdfConverter,
            IFileCompressor fileCompressor,
            ILocalStorage localDownload) {
        this.userProfileExporter = userProfileExporter;
        this.postExporter = postExporter;
        this.activityExporter = activityExporter;
        this.paymentExporter = paymentExporter;
        this.pdfConverter = pdfConverter;
        this.fileCompressor = fileCompressor;
        this.localDownload = localDownload;
        logger.info("File Export Context initialized");
    }

    public FileExportContext(
            IDocExporter userProfileExporter,
            IDocExporter postExporter,
            IDocExporter activityExporter,
            IDocExporter paymentExporter,
            IPdfConverter pdfConverter,
            IFileCompressor fileCompressor,
            IFileUploadStrategy fileUploadStrategy) {
        this.userProfileExporter = userProfileExporter;
        this.postExporter = postExporter;
        this.activityExporter = activityExporter;
        this.paymentExporter = paymentExporter;
        this.pdfConverter = pdfConverter;
        this.fileCompressor = fileCompressor;
        this.fileUploadStrategy = fileUploadStrategy;
        logger.info("File Export Context initialized");
    }

    public File processData(String username, MongoDatabase database) {
        try {
            logger.info("Processing data for username: {}", username);
            Document userProfile = userProfileExporter.exportDoc(database, username).get(0);
            List<File> generatedPdfFiles = new ArrayList<>();
            List<Document> userData = new ArrayList<>();
            String userDetailsPdfPath = username + "_details" + ".pdf";
            String paymentDetailsPdfPath = username + "_payment" + ".pdf";

            if (userProfile.getString("firstName") == null && userProfile.getString("lastName") == null) {
                userData.add(userProfile);
                File basicInfoPdf = pdfConverter.convertToPdf(userData, userDetailsPdfPath);
                generatedPdfFiles.add(basicInfoPdf);

            } else {
                String userTypeString = userProfile.getString("userType");
                UserType userType = UserType.valueOf(userTypeString);
                List<Document> posts = postExporter.exportDoc(database, username);
                List<Document> activities = activityExporter.exportDoc(database, username);
                List<Document> payments = paymentExporter.exportDoc(database, username);
                userData.add(userProfile);
                userData.addAll(posts);

                if (userType != UserType.NEW_USER) {
                    userData.addAll(activities);
                }
                if (userType == UserType.PREMIUM_USER) {
                    File premiumDetailsPdf = pdfConverter.convertToPdf(payments, paymentDetailsPdfPath);
                    generatedPdfFiles.add(premiumDetailsPdf);
                }
                File UserInfoPdf = pdfConverter.convertToPdf(userData, userDetailsPdfPath);
                generatedPdfFiles.add(UserInfoPdf);
            }
            return fileCompressor.compressFiles(generatedPdfFiles, username + ".zip");

        } catch (IllegalArgumentException | FileNotFoundException | DocumentException e) {
            logger.error("Exception occurred: ", e);
        } catch (Exception e) {
            logger.error("Error during data export for user '{}': {}", username, e.getMessage());
        }
        return null;
    }

    public void exportAndDownload(String username, MongoDatabase database) {
        logger.info("Exporting and downloading data for username: {}", username);
        File zipFile = processData(username, database);
        localDownload.downloadFile(zipFile.getAbsolutePath());
    }

    public void exportAndUpload(String username, MongoDatabase database, String outputPath) throws IOException {
        logger.info("Exporting and uploading data for username: {}", username);
        File zipFile = processData(username, database);
        fileUploadStrategy.uploadFile(zipFile.getAbsolutePath(), outputPath);
    }
}