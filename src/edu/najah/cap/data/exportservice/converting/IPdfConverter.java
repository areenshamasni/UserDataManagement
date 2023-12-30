package edu.najah.cap.data.exportservice.converting;
import org.bson.Document;

public interface IPdfConverter {
    void convertToPdf(Document document, String outputPath);
}
