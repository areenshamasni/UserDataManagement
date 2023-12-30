package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface IDocExporter {
    Document exportDoc(String username, MongoDatabase database);
}
