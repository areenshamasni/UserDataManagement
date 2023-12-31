package edu.najah.cap.data.mongodb;
import edu.najah.cap.activity.IUserActivityService;
import edu.najah.cap.activity.UserActivity;
import edu.najah.cap.exceptions.Util;
import edu.najah.cap.iam.IUserService;
import edu.najah.cap.iam.UserProfile;
import edu.najah.cap.payment.IPayment;
import edu.najah.cap.payment.Transaction;
import edu.najah.cap.posts.IPostService;
import edu.najah.cap.posts.Post;
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
                        TransactionMapper transactionMapper, PostMapper postMapper) {
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
            final String userId = "user" + i;

            Thread activitiesThread = new Thread(() -> insertUserActivities(userActivityService, userId));
            Thread transactionsThread = new Thread(() -> insertUserTransactions(paymentService, userId));
            Thread postsThread = new Thread(() -> insertUserPosts(postService, userId));
            Thread userProfileThread = new Thread(() -> insertUserProfile(userService, userId));

            activitiesThread.start();
            transactionsThread.start();
            postsThread.start();
            userProfileThread.start();

            try {
                activitiesThread.join();
                transactionsThread.join();
                postsThread.join();
                userProfileThread.join();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during insertData for userId: {}", userId, e);
                Thread.currentThread().interrupt();
            }
        }

        Util.setSkipValidation(false);
    }

    private void insertUserActivities(IUserActivityService userActivityService, String userId) {
        try {
            List<UserActivity> userActivities = userActivityService.getUserActivity(userId);
            for (UserActivity userActivity : userActivities) {
                mongoDataInserter.insertDocument("activities", userActivity, userActivityMapper::mapToDocument);
            }
        } catch (Exception e) {
            logger.error("Error inserting user activities for userId: {}", userId, e);
        }
    }

    private void insertUserTransactions(IPayment paymentService, String userId) {
        try {
            List<Transaction> transactions = paymentService.getTransactions(userId);
            for (Transaction transaction : transactions) {
                mongoDataInserter.insertDocument("payments", transaction, transactionMapper::mapToDocument);
            }
        } catch (Exception e) {
            logger.error("Error inserting user transactions for userId: {}", userId, e);
        }
    }

    private void insertUserPosts(IPostService postService, String userId) {
        try {
            List<Post> posts = postService.getPosts(userId);
            for (Post post : posts) {
                mongoDataInserter.insertDocument("posts", post, postMapper::mapToDocument);
            }
        } catch (Exception e) {
            logger.error("Error inserting user posts for userId: {}", userId, e);
        }
    }

    private void insertUserProfile(IUserService userService, String userId) {
        try {
            UserProfile userProfile = userService.getUser(userId);
            mongoDataInserter.insertDocument("users", userProfile, userMapper::mapToDocument);
        } catch (Exception e) {
            logger.error("Error inserting user profile for userId: {}", userId, e);
        }
    }
}
