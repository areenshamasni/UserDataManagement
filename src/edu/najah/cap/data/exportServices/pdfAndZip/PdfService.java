package edu.najah.cap.data.exportServices.pdfAndZip;
import com.itextpdf.text.DocumentException;
import org.bson.Document;
import java.io.FileNotFoundException;
import java.util.List;
public class PdfService {
    public static String createPdf(List<Document> data, String directoryPath, String fileName)
            throws FileNotFoundException, DocumentException {
        String outputPath = directoryPath + fileName + ".pdf";
        PdfConverter.convertToPdf(data, outputPath);
        System.out.println("PDF Generated at: " + outputPath);
        return outputPath;
    }
}
