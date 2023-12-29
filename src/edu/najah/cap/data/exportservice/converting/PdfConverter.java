package edu.najah.cap.data.exportservice.converting;
import org.bson.Document;

public interface PdfConverter {
    void convertToPdf(Document document, String outputPath);
}
