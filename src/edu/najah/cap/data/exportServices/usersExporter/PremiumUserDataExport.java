package edu.najah.cap.data.exportServices.usersExporter;
import com.mongodb.client.MongoDatabase;
import edu.najah.cap.data.exportServices.Factory.BaseUserDataExport;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;
public class PremiumUserDataExport extends BaseUserDataExport {
    public List<Document> exportUserData(MongoDatabase database, String userId) {
        List<Document> data = new ArrayList<>();
        //List<Document> paymentData = getDataFromCollectionPayment(database.getCollection("payments"), userId);
        data.addAll(getDataFromCollectionUsers(database.getCollection("users"), userId));
        data.addAll(getDataFromCollectionPost(database.getCollection("posts"), userId));
        data.addAll(getDataFromCollectionActivity(database.getCollection("activities"), userId));
        return data;
    }
}