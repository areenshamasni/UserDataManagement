package edu.najah.cap.data.exportServices.usersExporter;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;
public interface UserDataExport {
    List<Document> exportUserData(MongoDatabase database, String userId);
}