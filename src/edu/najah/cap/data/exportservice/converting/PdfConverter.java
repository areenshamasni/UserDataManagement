package edu.najah.cap.data.exportservice.converting;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfConverter implements IPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(PdfConverter.class);
    private static final Set<String> HIDDEN_FIELDS = new HashSet<>();
    static {
        HIDDEN_FIELDS.add("_id");
        HIDDEN_FIELDS.add("firstName");
        HIDDEN_FIELDS.add("lastName");
        HIDDEN_FIELDS.add("password");
        HIDDEN_FIELDS.add("phoneNumber");
    }
    public File convertToPdf(List<org.bson.Document> data, String outputPath)
            throws FileNotFoundException, DocumentException {
        createPdf(data, outputPath);
        logger.info("PDF created at: {}", outputPath);
        return new File(outputPath);
    }
    private static void createPdf(List<org.bson.Document> data, String outputPath)
            throws FileNotFoundException, DocumentException {
        Document pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(outputPath));
        pdfDocument.open();

        for (org.bson.Document bsonDoc : data) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : bsonDoc.entrySet()) {
                String key = entry.getKey();
                Object value = HIDDEN_FIELDS.contains(key) ? "*****" : entry.getValue();
                stringBuilder.append(key).append(": ").append(value.toString()).append("\n");
            }
            pdfDocument.add(new Paragraph(stringBuilder.toString()));
            pdfDocument.add(new Paragraph("\n"));
        }
        pdfDocument.close();
    }
}