package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import edu.najah.cap.exceptions.SystemBusyException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoftDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(SoftDelete.class);
    private final MongoDatabase database;
    private final IDataRestore dataRestore;
    private final ExecutorService executorService;
    public SoftDelete(MongoDatabase database,  IDataRestore dataRestore) {
        this.database = database;
        this.dataRestore = dataRestore;
        this.executorService = Executors.newFixedThreadPool(10);
    }
    @Override
    public void deleteUserData(String userName) {
        try {

            logger.info("Performing soft delete for user: {}", userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            try {
                executorService.submit(() -> softDeletePayments(userName, paymentsCollection));
            } catch (Exception e) {
                throw new SystemBusyException("Payment service is busy.");
            }
            //this line for testing exception handling
            if (userName.equals("user40")) {
                throw new SystemBusyException("Payment service is busy.");
            }
            try {
                executorService.submit(() -> softDeleteActivity(userName, activitiesCollection));
            } catch (Exception e) {
                throw new SystemBusyException("Activity service is busy.");
            }
            try {
                executorService.submit(() -> softDeleteUser(userName, usersCollection));
            } catch (Exception e) {
                throw new SystemBusyException("User service is busy.");
            }
            try {
                executorService.submit(() -> softDeletePosts(userName, postsCollection));
            } catch (Exception e) {
                throw new SystemBusyException("Post service is busy.");
            }

            executorService.shutdown();
            while (!executorService.isTerminated()) {
                Thread.sleep(100);
            }

            logger.info("Soft Delete for user: {} completed.", userName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread '{}' interrupted during soft delete for user: {}", Thread.currentThread().getName(), userName);
        } catch (SystemBusyException e) {
            logger.error("Exception occurred during soft delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName);
        }
    }

    private void softDeleteUser(String userName, MongoCollection<Document> usersCollection) {
        try {
            Document updates = new Document();
            updates.append("$unset", new Document("firstName","")
                    .append("lastName", "")
                    .append("phoneNumber", "")
                    .append("password", "")
                    .append("role", "")
                    .append("department", "")
                    .append("organization", "")
                    .append("country", "")
                    .append("city", "")
                    .append("street", "")
                    .append("postalCode", "")
                    .append("building", "")
                    .append("userType", "")
            );
            UpdateResult userUpdateResult = usersCollection.updateOne(new Document("userId", userName), updates);
            if (userUpdateResult.getModifiedCount() > 0) {
                logger.info("User data updated successfully.");
            } else {
                logger.info("User data was not updated.");
            }
        } catch (Exception e) {
            logger.error("Error updating user data for user: {}", userName, e);
        }
    }
    private void softDeleteActivity(String userName, MongoCollection<Document> activitiesCollection) {
        try {
            DeleteResult result = activitiesCollection.deleteMany(new Document("userId", userName));
            logger.info("Activities deleted: {}", result.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting activities for user: {}", userName, e);
        }
    }
    private void softDeletePayments(String userName,MongoCollection<Document> paymentsCollection){
        try {
            DeleteResult result = paymentsCollection.deleteMany(new Document("userName", userName));
            logger.info("Payments deleted: {}", result.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting payments for user: {}", userName, e);
        }
    }
    private void softDeletePosts(String userName,MongoCollection<Document> postsCollection){
        try {
            DeleteResult result = postsCollection.deleteMany(new Document("Author", userName));
            logger.info("Posts deleted: {}", result.getDeletedCount());
        } catch (Exception e) {
            logger.error("Error deleting posts for user: {}", userName, e);
        }
    }
}
