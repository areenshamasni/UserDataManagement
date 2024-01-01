package edu.najah.cap.data.exportservice.converting;

import com.itextpdf.text.DocumentException;
import org.bson.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface IPdfConverter {
    File convertToPdf(List<Document> data, String directoryPath) throws FileNotFoundException, DocumentException;
}