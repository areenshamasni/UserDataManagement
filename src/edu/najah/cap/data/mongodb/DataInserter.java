package edu.najah.cap.data.mongodb;

import edu.najah.cap.activity.IUserActivityService;
import edu.najah.cap.activity.UserActivity;
import edu.najah.cap.exceptions.Util;
import edu.najah.cap.iam.IUserService;
import edu.najah.cap.iam.UserProfile;
import edu.najah.cap.iam.UserType;
import edu.najah.cap.payment.IPayment;
import edu.najah.cap.payment.Transaction;
import edu.najah.cap.posts.IPostService;
import edu.najah.cap.posts.Post;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataInserter {
    private final MongoDataInserter mongoDataInserter;
    private static final Logger logger = LoggerFactory.getLogger(DataInserter.class);

    public DataInserter(MongoDataInserter mongoDataInserter) {
        this.mongoDataInserter = mongoDataInserter;
    }

    public void insertData(IUserActivityService userActivityService, IPayment paymentService,
                           IUserService userService, IPostService postService) {
        Util.setSkipValidation(true);
        for (int i = 0; i < 100; i++) {
            String userId = "user" + i;
            try {
                UserProfile userProfile = userService.getUser(userId);
                UserType userType = userProfile.getUserType();
                if (!UserType.NEW_USER.equals(userType)) {
                    List<UserActivity> userActivities = userActivityService.getUserActivity(userId);
                    for (UserActivity userActivity : userActivities) {
                        mongoDataInserter.insertDocument("activities", userActivity, this::mapUserActivityToDocument);
                    }
                }
                if (UserType.PREMIUM_USER.equals(userType)) {
                    List<Transaction> transactions = paymentService.getTransactions(userId);
                    if (transactions != null) {
                        for (Transaction transaction : transactions) {
                            mongoDataInserter.insertDocument("payments", transaction, this::mapTransactionToDocument);
                        }
                    }
                }
                mongoDataInserter.insertDocument("users", userProfile, this::mapUserProfileToDocument);

                List<Post> posts = postService.getPosts(userId);
                for (Post post : posts) {
                    mongoDataInserter.insertDocument("posts", post, this::mapPostToDocument);
                }
            } catch (Exception e) {
                logger.error("Error in insertData method", e);
            }
        }
        Util.setSkipValidation(false);
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