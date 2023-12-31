package edu.najah.cap.data.exportservice.converting;

import edu.najah.cap.data.exportservice.converting.IPdfConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class PdfConverter implements IPdfConverter {

    private static final Logger logger = LoggerFactory.getLogger(PdfConverter.class);
    private static final Set<String> HIDDEN_FIELDS = new HashSet<>();
    static {
        HIDDEN_FIELDS.add("_id");
        HIDDEN_FIELDS.add("firstName");
        HIDDEN_FIELDS.add("lastName");
    }

    public File convertToPdf(List<Document> documents, String outputPath) {
        PDDocument pdfDocument = new PDDocument();
        try {
            PDPage page = new PDPage();
            pdfDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true, true);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            float y = 700; // Start from the top of the page minus some margin
            float margin = 50; // Set a margin for the page
            float leading = 14; // Line spacing

            for (Document bsonDoc : documents) {
                for (String key : bsonDoc.keySet()) {
                    if (!HIDDEN_FIELDS.contains(key)) {
                        if (y < margin) { // Margin check
                            contentStream.endText();
                            contentStream.close();
                            page = new PDPage();
                            pdfDocument.addPage(page);
                            contentStream = new PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true, true);
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                            y = 700; // Reset Y position to top of new page
                        }
                        Object value = bsonDoc.get(key);
                        String text = key + ": " + (value != null ? value.toString() : "null");
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText(text);
                        y -= leading; // Move to the next line
                    }
                }
                y -= leading * 2; // Double space before a new document
                if (y < margin) {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage();
                    pdfDocument.addPage(page);
                    contentStream = new PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    y = 700; // Reset Y position to top of new page
                }
            }

            contentStream.endText();
            contentStream.close();
            pdfDocument.save(outputPath);
            logger.info("PDF created at {}", outputPath);
        } catch (IOException e) {
            logger.error("Error while creating PDF: {}", e.getMessage());
            return null;
        } finally {
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
            } catch (IOException ex) {
                logger.error("Error while closing PDF document: {}", ex.getMessage());
            }
        }

        return new File(outputPath);
    }
}
