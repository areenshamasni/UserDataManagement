package edu.najah.cap.data.exportServices.Factory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.najah.cap.data.exportServices.usersExporter.UserDataExport;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public abstract class BaseUserDataExport implements UserDataExport {
    protected List<Document> getDataFromCollectionUsers(MongoCollection<Document> collection, String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return collection.find(Filters.eq("userId", userId)).into(new ArrayList<>());
    }
    protected List<Document> getDataFromCollectionPost(MongoCollection<Document> collection, String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return collection.find(Filters.eq("Author", userId)).into(new ArrayList<>());
    }
    protected List<Document> getDataFromCollectionActivity(MongoCollection<Document> collection, String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return collection.find(Filters.eq("userId", userId)).into(new ArrayList<>());
    }
    public List<Document> getDataFromCollectionPayment(MongoCollection<Document> collection, String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return collection.find(Filters.eq("userName", userId)).into(new ArrayList<>());
    }
}