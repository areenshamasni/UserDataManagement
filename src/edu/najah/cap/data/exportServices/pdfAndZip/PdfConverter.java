package edu.najah.cap.data.exportServices.pdfAndZip;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
public class PdfConverter {
    private static final Set<String> HIDDEN_FIELDS = new HashSet<>();
    static {
        HIDDEN_FIELDS.add("_id");
        HIDDEN_FIELDS.add("firstName");
        HIDDEN_FIELDS.add("lastName");
        HIDDEN_FIELDS.add("email");
        HIDDEN_FIELDS.add("password");
        HIDDEN_FIELDS.add("phoneNumber");
//        HIDDEN_FIELDS.add("userId");
//        HIDDEN_FIELDS.add("userName");
//        HIDDEN_FIELDS.add("Author");
    }
    public static void convertToPdf(List<org.bson.Document> data, String directoryPath) throws FileNotFoundException, DocumentException {
        Document pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(directoryPath));
        pdfDocument.open();
        for (org.bson.Document bsonDoc : data) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : bsonDoc.entrySet()) {
                String key = entry.getKey();
                if (!HIDDEN_FIELDS.contains(key)) {
                    Object value = entry.getValue();
                    stringBuilder.append(key).append(": ").append(value.toString()).append("\n");
                }
            }

            pdfDocument.add(new Paragraph(stringBuilder.toString()));
            pdfDocument.add(new Paragraph("\n"));
        }
        pdfDocument.close();
        System.out.println("PDF created at: " + directoryPath);
    }
}