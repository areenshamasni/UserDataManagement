package edu.najah.cap.data.mongodb;

import edu.najah.cap.activity.UserActivity;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserActivityMapper implements IDocMapper<UserActivity> {
    private static final Logger logger = LoggerFactory.getLogger(UserActivityMapper.class);

    @Override
    public Document mapToDocument(UserActivity userActivity) {
        Document document = new Document();
        try {
            document.append("userId", userActivity.getUserId())
                    .append("activityType", userActivity.getActivityType())
                    .append("activityDate", userActivity.getActivityDate());
            logger.info("User Activity mapped to Document");
        } catch (Exception e) {
            logger.error("error in mapping user activity to document");
        }
        return document;
    }
}
