package edu.najah.cap.data.exportservice.converting;
import org.bson.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import com.itextpdf.text.DocumentException;
public interface IPdfConverter {
    File convertToPdf(List<Document> data, String directoryPath) throws FileNotFoundException, DocumentException;
}