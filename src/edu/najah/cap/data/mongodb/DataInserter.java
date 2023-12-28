package edu.najah.cap.data.mongodb;

import edu.najah.cap.activity.IUserActivityService;
import edu.najah.cap.activity.UserActivity;
import edu.najah.cap.exceptions.BadRequestException;
import edu.najah.cap.exceptions.NotFoundException;
import edu.najah.cap.exceptions.SystemBusyException;
import edu.najah.cap.iam.IUserService;
import edu.najah.cap.iam.UserProfile;
import edu.najah.cap.payment.IPayment;
import edu.najah.cap.payment.Transaction;
import edu.najah.cap.posts.IPostService;
import edu.najah.cap.posts.Post;
import org.bson.Document;

import java.util.List;

public class DataInserter {
    private final MongoDataInserter mongoDataInserter;

    public DataInserter(MongoDataInserter mongoDataInserter) {
        this.mongoDataInserter = mongoDataInserter;
    }

    public void insertData(IUserActivityService userActivityService, IPayment paymentService,
                           IUserService userService, IPostService postService) {
        String userId = null;
        try {
            for (int i = 0; i < 100; i++) {
                userId = "user" + i;
                Util.setSkipValidation(true);
                List<UserActivity> userActivities = userActivityService.getUserActivity(userId);
                if (userActivities != null){
                    for (UserActivity userActivity : userActivities) {
                        try {
                            mongoDataInserter.insertDocument("activities", userActivity, this::mapUserActivityToDocument);
                        } catch (Exception e) {
                            logger.error("Error inserting user activity document for userId: " + userId, e);
                        }
                    }}

                List<Transaction> transactions = paymentService.getTransactions(userId);
                if (transactions != null) {
                    for (Transaction transaction : transactions) {
                        try {
                            mongoDataInserter.insertDocument("payments", transaction, this::mapTransactionToDocument);
                        } catch (Exception e) {
                            logger.error("Error inserting payment document for userId: " + userId, e);
                        }
                    }
                }

                UserProfile userProfile = userService.getUser(userId);
                if (userProfile != null) {
                    try {
                        mongoDataInserter.insertDocument("users", userProfile, this::mapUserProfileToDocument);
                    } catch (Exception e) {
                        logger.error("Error inserting user profile document for userId: " + userId, e);
                    }}

                List<Post> posts = postService.getPosts(userId);
                if (posts != null) {
                    for (Post post : posts) {
                        try {
                            mongoDataInserter.insertDocument("posts", post, this::mapPostToDocument);
                        } catch (Exception e) {
                            logger.error("Error inserting post document for userId: " + userId, e);
                        }
                    }}
            }
        } catch (SystemBusyException | BadRequestException | NotFoundException e) {
            logger.error("Error in insertData method", e);
        } finally {
            Util.setSkipValidation(false);
        }
    }

    private Document mapUserActivityToDocument(UserActivity userActivity) {
        Document document = new Document();
        document.append("userId", userActivity.getUserId())
                .append("activityType", userActivity.getActivityType())
                .append("activityDate", userActivity.getActivityDate());
        return document;
    }

    private Document mapTransactionToDocument(Transaction transaction) {
        Document document = new Document();
        document.append("userName", transaction.getUserName())
                .append("amount", transaction.getAmount())
                .append("description", transaction.getDescription());
        return document;
    }

    private Document mapUserProfileToDocument(UserProfile userProfile) {
        Document document = new Document();
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
        return document;
    }

    private Document mapPostToDocument(Post post) {
        Document document = new Document();
        document.append("title", post.getTitle())
                .append("body", post.getBody())
                .append("Author", post.getAuthor())
                .append("postDate", post.getDate());
        return document;
    }
}