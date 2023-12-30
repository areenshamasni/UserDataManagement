package edu.najah.cap.data.deleteservice;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataBackup;
import edu.najah.cap.data.deleteservice.exceptionhandler.IDataRestore;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(HardDelete.class);
    private final MongoDatabase database;
    private final IDataBackup dataBackup;
    private final IDataRestore dataRestore;

    public HardDelete(MongoDatabase database, IDataBackup dataBackup, IDataRestore dataRestore) {
        this.database = database;
        this.dataBackup = dataBackup;
        this.dataRestore = dataRestore;
    }

    @Override
    public void deleteUserData(String userName) {
        try {
            dataBackup.backupUserData(userName);

            logger.info("Performing hard delete for user: {}", userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            Thread usersThread = new Thread(() -> {
                try {
                    DeleteResult usersResult = usersCollection.deleteOne(new Document("userId", userName));
                    logger.info("Users deleted: {}", usersResult.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting users for user: {}", userName, e);
                }
            });

            Thread activitiesThread = new Thread(() -> {
                try {
                    DeleteResult activitiesResult = activitiesCollection.deleteMany(new Document("userId", userName));
                    logger.info("Activities deleted: {}", activitiesResult.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting activities for user: {}", userName, e);
                }
            });

            Thread paymentsThread = new Thread(() -> {
                try {
                    DeleteResult paymentsResult = paymentsCollection.deleteMany(new Document("userName", userName));
                    logger.info("Payments deleted: {}", paymentsResult.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting payments for user: {}", userName, e);
                }
            });

            Thread postsThread = new Thread(() -> {
                try {
                    DeleteResult postsResult = postsCollection.deleteMany(new Document("Author", userName));
                    logger.info("Posts deleted: {}", postsResult.getDeletedCount());
                } catch (Exception e) {
                    logger.error("Error deleting posts for user: {}", userName, e);
                }
            });

            usersThread.start();
            activitiesThread.start();
            paymentsThread.start();
            postsThread.start();

            try {
                usersThread.join();
                activitiesThread.join();
                paymentsThread.join();
                postsThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread '{}' interrupted while waiting for subthreads.", Thread.currentThread().getName(), e);
                dataRestore.restoreUserData(userName);
            }

            logger.info("Hard Delete for user: {} completed.", userName);
        } catch (Exception e) {
            dataRestore.restoreUserData(userName);
            logger.error("Exception occurred during hard delete for user: {}. Data restoration initiated.", userName, e);
        }
    }
}
