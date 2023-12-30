package edu.najah.cap.data.exportservice.converting;
import org.bson.Document;

import java.io.File;

public interface IPdfConverter {
    File convertToPdf(Document document, String outputPath);
}
