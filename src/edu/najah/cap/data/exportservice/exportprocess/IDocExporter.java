package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public interface IDocExporter {
    List <Document> exportDoc(String username, MongoDatabase database);
}
