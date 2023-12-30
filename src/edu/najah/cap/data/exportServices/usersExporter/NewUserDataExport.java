package edu.najah.cap.data.exportServices.usersExporter;
import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportServices.Factory.BaseUserDataExport;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
public class NewUserDataExport extends BaseUserDataExport {
    public List<Document> exportUserData(MongoDatabase database, String userId) {
        List<Document> data = new ArrayList<>();
        data.addAll(getDataFromCollectionUsers(database.getCollection("users"), userId));
        data.addAll(getDataFromCollectionPost(database.getCollection("posts"), userId));
        return data;
    }
}