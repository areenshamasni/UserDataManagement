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
    private final UserMapper userMapper;
    private final UserActivityMapper userActivityMapper;
    private final TransactionMapper transactionMapper;
    private final PostMapper postMapper;
    public DataInserter(MongoDataInserter mongoDataInserter,
                        UserMapper userMapper, UserActivityMapper userActivityMapper,
                        TransactionMapper transactionMapper,
                        PostMapper postMapper) {
        this.mongoDataInserter = mongoDataInserter;
        this.userMapper = userMapper;
        this.userActivityMapper = userActivityMapper;
        this.transactionMapper = transactionMapper;
        this.postMapper = postMapper;
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
                        mongoDataInserter.insertDocument("activities", userActivity, userActivityMapper::mapToDocument);
                    }
                }
                if (UserType.PREMIUM_USER.equals(userType)) {
                    List<Transaction> transactions = paymentService.getTransactions(userId);
                    if (transactions != null) {
                        for (Transaction transaction : transactions) {
                            mongoDataInserter.insertDocument("payments", transaction, transactionMapper::mapToDocument);
                        }
                    }
                }
                mongoDataInserter.insertDocument("users", userProfile, userMapper::mapToDocument);

                List<Post> posts = postService.getPosts(userId);
                for (Post post : posts) {
                    mongoDataInserter.insertDocument("posts", post, postMapper::mapToDocument);
                }
            } catch (Exception e) {
                logger.error("Error in insertData method", e);
            }
        }
        Util.setSkipValidation(false);
    }
}