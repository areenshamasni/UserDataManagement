package edu.najah.cap.data.deleteservice;

import edu.najah.cap.data.deleteservice.exceptionhandler.IDataBackup;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import edu.najah.cap.exceptions.SystemBusyException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(SoftDelete.class);
    private final MongoDatabase database;
    private final IDataBackup dataBackup;
    private final IDataRestore dataRestore;

    public SoftDelete(MongoDatabase database, IDataBackup dataBackup, IDataRestore dataRestore) {
        this.database = database;
        this.dataBackup = dataBackup;
        this.dataRestore = dataRestore;
    }
    @Override
    public void deleteUserData(String userName) {
        try {
            dataBackup.backupUserData(userName);

            logger.info("Performing Soft Delete for user: {}", userName);
            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            Thread activitiesThread = new Thread(() -> {
                try {
                    DeleteResult result = activitiesCollection.deleteMany(new Document("userId", userName));
                    logger.info("Activities deleted: {}", result.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting activities for user: {}", userName, e);
                }
            });

            Thread paymentsThread = new Thread(() -> {
                try {
                    DeleteResult result = paymentsCollection.deleteMany(new Document("userName", userName));
                    logger.info("Payments deleted: {}", result.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting payments for user: {}", userName, e);
                }
            });

            Thread postsThread = new Thread(() -> {
                try {
                    DeleteResult result = postsCollection.deleteMany(new Document("Author", userName));
                    logger.info("Posts deleted: {}", result.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting posts for user: {}", userName, e);
                }
            });
            Bson updates = Updates.combine(
                    Updates.unset("firstName"),
                    Updates.unset("lastName"),
                    Updates.unset("phoneNumber"),
                    Updates.unset("password"),
                    Updates.unset("role"),
                    Updates.unset("department"),
                    Updates.unset("organization"),
                    Updates.unset("country"),
                    Updates.unset("city"),
                    Updates.unset("street"),
                    Updates.unset("postalCode"),
                    Updates.unset("building"),
                    Updates.unset("userType")
            );

            UpdateResult userUpdateResult = usersCollection.updateOne(new Document("userId", userName), updates);
            if (userUpdateResult.getModifiedCount() > 0) {
                logger.info("User data updated successfully.");
            } else {
                logger.info("User data was not updated.");
            }
            activitiesThread.start();
            paymentsThread.start();
            //this line for testing
            /*if (userName.equals("user7")) {
                throw new InterruptedException("Simulated System Interrupted Exception for testing.");
            }*/
            postsThread.start();
            try {
                activitiesThread.join();
                paymentsThread.join();
                postsThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread '{}' interrupted while waiting for subthreads.", Thread.currentThread().getName(), e);
                dataRestore.restoreUserData(userName);
            }

            logger.info("Soft Delete for user: {} completed.", userName);
        }
        catch (Exception e) {

            logger.error("Exception occurred during soft delete for user: {}. Data restoration initiated.", userName, e);
            dataRestore.restoreUserData(userName);
        }
    }
}
