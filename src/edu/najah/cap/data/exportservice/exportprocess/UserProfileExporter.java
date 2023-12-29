package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.iam.UserProfile;

public interface UserProfileExporter {
    UserProfile exportUserProfile(String username , MongoDatabase database);
}