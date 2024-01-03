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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataInserter {
    private static final Logger logger = LoggerFactory.getLogger(DataInserter.class);
    private final MongoDataInserter mongoDataInserter;
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
                           IUserService userService, IPostService postService) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            final String userId = "user" + i;
            executorService.execute(() -> insertUserActivities(userActivityService, userId));
            executorService.execute(() -> insertUserTransactions(paymentService, userId));
            executorService.execute(() -> insertUserPosts(postService, userId));
            executorService.execute(() -> insertUserProfile(userService, userId));
        }
        executorService.shutdown();
        try {
            while (!executorService.isTerminated()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted during insertData", e);
            Thread.currentThread().interrupt();
        }
    }
    private void insertUserActivities(IUserActivityService userActivityService, String userId) {
        try {
            Thread.sleep(1000);
            List<UserActivity> userActivities = userActivityService.getUserActivity(userId);
            for (UserActivity userActivity : userActivities) {
                mongoDataInserter.insertDocument("activities", userActivity, userActivityMapper::mapToDocument);
            }
        } catch (SystemBusyException e) {
            logger.error("System is busy while inserting user activities for userId: {}", userId);
            insertUserActivities(userActivityService, userId);
        } catch (NotFoundException e) {
            logger.error("User not found while inserting user activities for userId: {}", userId);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while inserting user activities for userId: {}", userId);
            insertUserActivities(userActivityService, userId);
        } catch (BadRequestException e) {
            logger.error("Bad request while inserting user activities for userId: {}", userId);
        }
    }
    private void insertUserTransactions(IPayment paymentService, String userId) {
        try {
            Thread.sleep(1000);
            List<Transaction> transactions = paymentService.getTransactions(userId);
            for (Transaction transaction : transactions) {
                mongoDataInserter.insertDocument("payments", transaction, transactionMapper::mapToDocument);
            }
        } catch (SystemBusyException e) {
            logger.error("System is busy while inserting user transactions for userId: {}", userId);
            insertUserTransactions(paymentService, userId);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while inserting user transactions for userId: {}", userId);
            insertUserTransactions(paymentService, userId);
        } catch (BadRequestException e) {
            logger.error("Bad request while inserting user transactions for userId: {}", userId);
        } catch (NotFoundException e) {
            logger.error("User not found while inserting user transactions for userId: {}", userId);
        }
    }
    private void insertUserPosts(IPostService postService, String userId) {
        try {
            Thread.sleep(1000);
            List<Post> posts = postService.getPosts(userId);
            for (Post post : posts) {
                mongoDataInserter.insertDocument("posts", post, postMapper::mapToDocument);
            }
        } catch (SystemBusyException e) {
            logger.error("System is busy while inserting user posts for userId: {}", userId);
            insertUserPosts(postService, userId);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while inserting user posts for userId: {}", userId);
            insertUserPosts(postService, userId);
        } catch (BadRequestException e) {
            logger.error("Bad request while inserting user posts for userId: {}", userId);
        } catch (NotFoundException e) {
            logger.error("User not found while inserting user posts for userId: {}", userId);
        }
    }
    private void insertUserProfile(IUserService userService, String userId) {
        try {
            Thread.sleep(1000);
            UserProfile userProfile = userService.getUser(userId);
            mongoDataInserter.insertDocument("users", userProfile, userMapper::mapToDocument);
        } catch (SystemBusyException e) {
            logger.error("System is busy while inserting user profile for userId: {}", userId);
            insertUserProfile(userService, userId);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while inserting user profile for userId: {}", userId);
            insertUserProfile(userService, userId);
        } catch (BadRequestException e) {
            logger.error("Bad request while inserting user profile for userId: {}", userId);
        } catch (NotFoundException e) {
            logger.error("User not found while inserting user profile for userId: {}", userId);
        }
    }
}