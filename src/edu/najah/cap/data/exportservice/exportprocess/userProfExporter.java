package edu.najah.cap.data.exportservice.exportprocess;

import com.mongodb.client.MongoDatabase;
import edu.najah.cap.iam.UserProfile;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class userProfExporter implements UserProfileExporter {
    private static final Logger logger = LoggerFactory.getLogger(userProfExporter.class);
    @Override
    public Document exportUserProfile(String username, MongoDatabase database) {
        logger.info("export UserProfile for {}", username);
        return null;
    }
}
