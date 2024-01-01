package edu.najah.cap.data.deleteservice;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import edu.najah.cap.data.exceptionhandler.IDataBackup;
import edu.najah.cap.data.exceptionhandler.IDataRestore;
import edu.najah.cap.exceptions.BadRequestException;
import edu.najah.cap.exceptions.SystemBusyException;
import edu.najah.cap.exceptions.Util;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoftDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(SoftDelete.class);
    private final MongoDatabase database;
    private final IDataRestore dataRestore;
    private final IDataBackup dataBackup;
    private final ExecutorService executorService;

    public SoftDelete(MongoDatabase database, IDataRestore dataRestore, IDataBackup dataBackup) {
        this.database = database;
        this.dataRestore = dataRestore;
        this.dataBackup = dataBackup;

        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void deleteUserData(String userName) throws SystemBusyException {
        logger.info("Performing soft delete for user: {}", userName);

        Map<String, List<Document>> userBackup = dataBackup.backupUserData(userName);
        try {
            Util.validateUserName(userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            executorService.submit(() -> softDeletePayments(userName, paymentsCollection));
            executorService.submit(() -> softDeleteActivity(userName, activitiesCollection));
            executorService.submit(() -> softDeleteUser(userName, usersCollection));
            executorService.submit(() -> softDeletePosts(userName, postsCollection));
            executorService.shutdown();

            while (!executorService.isTerminated()) {
                Thread.sleep(100);
            }
            logger.info("Soft Delete for user: {} completed.", userName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread '{}' interrupted during soft delete for user: {}", Thread.currentThread().getName(), userName);
            dataRestore.restoreUserData(userName, userBackup);
        } catch (SystemBusyException e) {
            logger.error("SystemBusyException occurred during soft delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName, userBackup);
        } catch (BadRequestException e) {
            logger.error("BadRequestException occurred during soft delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName, userBackup);
        }
    }

    private void softDeleteUser(String userName, MongoCollection<Document> usersCollection) {
        try {
            Document updates = new Document();
            updates.append("$unset", new Document("firstName", "")
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
        } catch (MongoException e) {
            logger.error("Error updating user data for user: {}", userName, e);
        }
    }

    private void softDeleteActivity(String userName, MongoCollection<Document> activitiesCollection) {
        try {
            DeleteResult result = activitiesCollection.deleteMany(new Document("userId", userName));
            logger.info("Activities deleted: {}", result.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting activities for user: {}", userName, e);
        }
    }

    private void softDeletePayments(String userName, MongoCollection<Document> paymentsCollection) {
        try {
            DeleteResult result = paymentsCollection.deleteMany(new Document("userName", userName));
            logger.info("Payments deleted: {}", result.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting payments for user: {}", userName, e);
        }
    }

    private void softDeletePosts(String userName, MongoCollection<Document> postsCollection) {
        try {
            DeleteResult result = postsCollection.deleteMany(new Document("Author", userName));
            logger.info("Posts deleted: {}", result.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting posts for user: {}", userName, e);
        }
    }
}
