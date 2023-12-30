package edu.najah.cap.data.mongodb;

import edu.najah.cap.iam.UserProfile;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMapper implements IDocMapper<UserProfile> {
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    @Override
    public Document mapToDocument(UserProfile userProfile) {
        Document document = new Document();
        try {
            document.append("userId", userProfile.getUserName())
                    .append("firstName", userProfile.getFirstName())
                    .append("lastName", userProfile.getLastName())
                    .append("phoneNumber", userProfile.getPhoneNumber())
                    .append("email", userProfile.getEmail())
                    .append("password", userProfile.getPassword())
                    .append("role", userProfile.getRole())
                    .append("department", userProfile.getDepartment())
                    .append("organization", userProfile.getOrganization())
                    .append("country", userProfile.getCountry())
                    .append("city", userProfile.getCity())
                    .append("street", userProfile.getStreet())
                    .append("postalCode", userProfile.getPostalCode())
                    .append("building", userProfile.getBuilding())
                    .append("userType", userProfile.getUserType().toString());
            logger.info("User profile mapped to Document");
        } catch (Exception e) {
            logger.error("error in mapping user profile to document");
        }
        return document;
    }
}
