package edu.najah.cap.data.deleteservice;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
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

public class HardDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(HardDelete.class);
    private final MongoDatabase database;
    private final IDataRestore dataRestore;
    private final IDataBackup dataBackup;
    private final ExecutorService executorService;

    public HardDelete(MongoDatabase database, IDataRestore dataRestore, IDataBackup dataBackup) {
        this.database = database;
        this.dataRestore = dataRestore;
        this.dataBackup = dataBackup;

        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void deleteUserData(String userName) throws SystemBusyException {
        logger.info("Performing hard delete for user: {}", userName);
        Map<String, List<Document>> userBackup = dataBackup.backupUserData(userName);
        try {
            Util.validateUserName(userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            executorService.submit(() -> hardDeletePayments(userName, paymentsCollection));
            executorService.submit(() -> hardDeleteActivities(userName, activitiesCollection));
            executorService.submit(() -> hardDeleteUsers(userName, usersCollection));
            executorService.submit(() -> hardDeletePosts(userName, postsCollection));

            executorService.shutdown();

            while (!executorService.isTerminated()) {
                Thread.sleep(100);
            }
            logger.info("Hard Delete for user: {} completed.", userName);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread '{}' interrupted during hard delete for user: {}", Thread.currentThread().getName(), userName);
            dataRestore.restoreUserData(userName, userBackup);
        } catch (SystemBusyException e) {
            logger.error("SystemBusyException occurred during hard delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName, userBackup);
        } catch (BadRequestException e) {
            logger.error("BadRequestException occurred during hard delete for user: {}. Data restoration initiated.", userName);
            dataRestore.restoreUserData(userName, userBackup);
        }
    }

    private void hardDeleteUsers(String userName, MongoCollection<Document> usersCollection) {
        try {
            DeleteResult usersResult = usersCollection.deleteOne(new Document("userId", userName));
            logger.info("Users deleted: {}", usersResult.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting users for user: {}", userName, e);
        }
    }

    private void hardDeleteActivities(String userName, MongoCollection<Document> activitiesCollection) {
        try {
            DeleteResult activitiesResult = activitiesCollection.deleteMany(new Document("userId", userName));
            logger.info("Activities deleted: {}", activitiesResult.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting activities for user: {}", userName, e);
        }
    }

    private void hardDeletePayments(String userName, MongoCollection<Document> paymentsCollection) {
        try {
            DeleteResult paymentsResult = paymentsCollection.deleteMany(new Document("userName", userName));
            logger.info("Payments deleted: {}", paymentsResult.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting payments for user: {}", userName, e);
        }
    }

    private void hardDeletePosts(String userName, MongoCollection<Document> postsCollection) {
        try {
            DeleteResult postsResult = postsCollection.deleteMany(new Document("Author", userName));
            logger.info("Posts deleted: {}", postsResult.getDeletedCount());
        } catch (MongoException e) {
            logger.error("Error deleting posts for user: {}", userName, e);
        }
    }
}
