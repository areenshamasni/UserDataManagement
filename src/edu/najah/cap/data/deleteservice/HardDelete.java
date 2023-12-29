package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.najah.cap.data.exceptions.DeleteOperationException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(HardDelete.class);
    private final MongoDatabase database;

    public HardDelete(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void deleteUserData(String userName) {
        try {
            logger.info("Performing hard delete for user: {}", userName);

            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            Thread usersThread = new Thread(() -> {
                DeleteResult usersResult = usersCollection.deleteOne(new Document("userId", userName));
                logger.info("Users deleted: {}", usersResult.getDeletedCount());
            });

            Thread activitiesThread = new Thread(() -> {
                DeleteResult activitiesResult = activitiesCollection.deleteMany(new Document("userId", userName));
                logger.info("Activities deleted: {}", activitiesResult.getDeletedCount());
            });

            Thread paymentsThread = new Thread(() -> {
                DeleteResult paymentsResult = paymentsCollection.deleteMany(new Document("userName", userName));
                logger.info("Payments deleted: {}", paymentsResult.getDeletedCount());
            });

            Thread postsThread = new Thread(() -> {
                DeleteResult postsResult = postsCollection.deleteMany(new Document("Author", userName));
                logger.info("Posts deleted: {}", postsResult.getDeletedCount());
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
                logger.error("Thread '" + Thread.currentThread().getName() + "' interrupted while waiting for subthreads.", e);
            }


            logger.info("Hard Delete for user: {} completed.", userName);
        } catch (Exception e) {
            throw new DeleteOperationException("Error during soft delete for user: " + userName, e);
        }
    }
}
