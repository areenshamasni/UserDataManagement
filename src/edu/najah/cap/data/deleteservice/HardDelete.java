package edu.najah.cap.data.deleteservice;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import edu.najah.cap.exceptions.SystemBusyException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HardDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(HardDelete.class);
    private final MongoDatabase database;
    private final IDataRestore dataRestore;
    private final ExecutorService executorService;

    public HardDelete(MongoDatabase database, IDataRestore dataRestore) {
        this.database = database;
        this.dataRestore = dataRestore;
        this.executorService = Executors.newFixedThreadPool(10);
    }
    @Override
    public void deleteUserData(String userName) {
        try {

            logger.info("Performing hard delete for user: {}", userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            try {
                executorService.submit(() -> hardDeletePayments(userName, paymentsCollection));
            } catch (Exception e) {
                logger.error("Error deleting payments for user: {}. Data restoration initiated.", userName, e);
                dataRestore.restoreUserData(userName);
                throw new SystemBusyException("Payment service is busy.");
            }
            //this line for testing exception handling
           /* if (userName.equals("user70")) {
                throw new SystemBusyException("Payment service is busy.");
            }*/
            try {
                executorService.submit(() -> hardDeleteActivities(userName, activitiesCollection));
            } catch (Exception e) {
                logger.error("Error deleting activities for user: {}. Data restoration initiated.", userName, e);
                dataRestore.restoreUserData(userName);
                throw new SystemBusyException("Activity service is busy.");
            }

            try {
                executorService.submit(() -> hardDeleteUsers(userName, usersCollection));
            } catch (Exception e) {
                logger.error("Error deleting users for user: {}. Data restoration initiated.", userName, e);
                dataRestore.restoreUserData(userName);
                throw new SystemBusyException("User service is busy.");
            }

            try {
                executorService.submit(() -> hardDeletePosts(userName, postsCollection));
            } catch (Exception e) {
                logger.error("Error deleting posts for user: {}. Data restoration initiated.", userName, e);
                dataRestore.restoreUserData(userName);
                throw new SystemBusyException("Post service is busy.");
            }

            executorService.shutdown();

            while (!executorService.isTerminated()) {
                Thread.sleep(100);
            }
            logger.info("Hard Delete for user: {} completed.", userName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread '{}' interrupted during hard delete for user: {}", Thread.currentThread().getName(), userName);
            dataRestore.restoreUserData(userName);
        } catch (SystemBusyException   e) {
            logger.error("Exception occurred during hard delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName);

        }
    }

    private void hardDeleteUsers(String userName,MongoCollection<Document> usersCollection){
        try {
            DeleteResult usersResult = usersCollection.deleteOne(new Document("userId", userName));
            logger.info("Users deleted: {}", usersResult.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting users for user: {}", userName, e);
        }
    }
    private void hardDeleteActivities(String userName,MongoCollection<Document> activitiesCollection){
        try {
            DeleteResult activitiesResult = activitiesCollection.deleteMany(new Document("userId", userName));
            logger.info("Activities deleted: {}", activitiesResult.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting activities for user: {}", userName, e);
        }
    }
    private void hardDeletePayments(String userName,MongoCollection<Document> paymentsCollection){
        try {
            DeleteResult paymentsResult = paymentsCollection.deleteMany(new Document("userName", userName));
            logger.info("Payments deleted: {}", paymentsResult.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting payments for user: {}", userName, e);
        }
    }
    private void hardDeletePosts(String userName,MongoCollection<Document> postsCollection){
        try {
            DeleteResult postsResult = postsCollection.deleteMany(new Document("Author", userName));
            logger.info("Posts deleted: {}", postsResult.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting posts for user: {}", userName, e);
        }
    }
}
