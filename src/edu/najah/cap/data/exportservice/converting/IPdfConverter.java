package edu.najah.cap.data.exportservice.converting;
import org.bson.Document;

import java.io.File;
import java.util.List;

public interface IPdfConverter {
    File convertToPdf(List<Document> document, String outputPath);
}
