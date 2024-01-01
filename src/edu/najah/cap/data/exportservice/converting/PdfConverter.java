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
import java.util.List;
import java.util.Map;

public class PdfConverter implements IPdfConverter {
    private static final Logger logger = LoggerFactory.getLogger(PdfConverter.class);
    private static final String HIDDEN_FIELD = "_id";
    private static final String MASKED_FIELD = "password";

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
                if (key.equals(HIDDEN_FIELD)) {
                    continue;
                }
                Object value = key.equals(MASKED_FIELD) ? "**********" : entry.getValue();
                stringBuilder.append(key).append(": ").append(value).append("\n");
            }
            pdfDocument.add(new Paragraph(stringBuilder.toString()));
            pdfDocument.add(new Paragraph("\n"));
        }
        pdfDocument.close();
    }
}