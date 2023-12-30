package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public interface IUserProfileExporter {
    Document exportUserProfile(String username , MongoDatabase database);
}