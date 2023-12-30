package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.iam.UserProfile;
import org.bson.Document;

public interface UserProfileExporter {
    Document exportUserProfile(String username , MongoDatabase database);
}