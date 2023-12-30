package edu.najah.cap.data.exportServices;
import com.itextpdf.text.DocumentException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.najah.cap.data.exportServices.Factory.UserDataExportFactory;
import edu.najah.cap.data.exportServices.pdfAndZip.PdfService;
import edu.najah.cap.data.exportServices.pdfAndZip.ZipFileService;
import edu.najah.cap.data.exportServices.usersExporter.UserDataExport;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.Collections;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataExporter {
    private final MongoDatabase database;
    public DataExporter(MongoDatabase  database) {
        this.database = database;
    }
    public void exportUserData(String userId, String outputDirectory) throws DocumentException, IOException {
        ObjectId userObjectId = getUserById(userId);

        if (userObjectId == null) {
            System.out.println("No user found with userId: " + userId);
            return;
        }
        String userType = getUserType(userId);
        System.out.println("Exporting data for user type: " + userType);

        UserDataExport strategy = UserDataExportFactory.getExportStrategy(userType);

        List<Document> userData = strategy.exportUserData(database, userId);
        List<String> generatedPdfPaths = new ArrayList<>();
        switch (userType) {
            case "PREMIUM_USER" -> {
                List<Document> paymentData = getDataFromCollectionPayment(database.getCollection("payments"), userId); // This should fetch payment data

                String paymentPdfPath = PdfService.createPdf(paymentData, outputDirectory, userId + "Premium_info_payment");
                generatedPdfPaths.add(paymentPdfPath);

                String paymentPdfPath2 = PdfService.createPdf(userData, outputDirectory, userId + "Premium_info");
                generatedPdfPaths.add(paymentPdfPath2);
            }
            case "REGULAR_USER" -> {
                String paymentPdfPath = PdfService.createPdf(userData, outputDirectory, userId + "Regular_info");
                generatedPdfPaths.add(paymentPdfPath);

            }
            case "NEW_USER" -> {
                String paymentPdfPath = PdfService.createPdf(userData, outputDirectory, userId + "New_info");
                generatedPdfPaths.add(paymentPdfPath);
            }
        }

        String zipFilePath = outputDirectory + userType + ".zip";
        ZipFileService.createZipFile(generatedPdfPaths, zipFilePath);

        System.out.println("PDFs zipped successfully at: " + zipFilePath);
    }

    public ObjectId getUserById(String userId) {
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Document userDoc = usersCollection.find(Filters.eq("userId", userId)).first();
        return userDoc != null ? userDoc.getObjectId("_id") : null;
    }
    public String getUserType(String userId) {
        MongoCollection<Document> usersCollection = database.getCollection("users");
        Document user = usersCollection.find(Filters.eq("userId", userId)).first();
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user.getString("userType");
    }
    private List<Document> getDataFromCollectionPayment(MongoCollection<Document> collection, String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return collection.find(Filters.eq("userName", userId)).into(new ArrayList<>());
    }
}