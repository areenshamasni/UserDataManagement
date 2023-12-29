package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.iam.UserProfile;

public class userProfExporter implements UserProfileExporter {
    @Override
    public UserProfile exportUserProfile(String username, MongoDatabase database) {
        return null;
    }
}
