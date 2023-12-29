package edu.najah.cap.data.deleteservice;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import edu.najah.cap.data.exceptions.DeleteOperationException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftDelete implements IDeleteService {
    private static final Logger logger = LoggerFactory.getLogger(SoftDelete.class);
    private final MongoDatabase database;

    public SoftDelete(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public void deleteUserData(String userName) {
        try {
            logger.info("Performing Soft Delete for user: {}", userName);
            MongoCollection<Document> usersCollection = database.getCollection("users");
            MongoCollection<Document> activitiesCollection = database.getCollection("activities");
            MongoCollection<Document> paymentsCollection = database.getCollection("payments");
            MongoCollection<Document> postsCollection = database.getCollection("posts");

            Thread activitiesThread = new Thread(() -> {
                DeleteResult result = activitiesCollection.deleteMany(new Document("userId", userName));
                logger.info("Activities deleted: {}", result.getDeletedCount());
            });

            Thread paymentsThread = new Thread(() -> {
                DeleteResult result = paymentsCollection.deleteMany(new Document("userName", userName));
                logger.info("Payments deleted: {}", result.getDeletedCount());
            });

            Thread postsThread = new Thread(() -> {
                DeleteResult result = postsCollection.deleteMany(new Document("Author", userName));
                logger.info("Posts deleted: {}", result.getDeletedCount());
            });

            activitiesThread.start();
            paymentsThread.start();
            postsThread.start();

            try {
                activitiesThread.join();
                paymentsThread.join();
                postsThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread '" + Thread.currentThread().getName() + "' interrupted while waiting for subthreads.", e);
            }

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

            logger.info("Soft Delete for user: {} completed.", userName);

        } catch (Exception e) {
            throw new DeleteOperationException("Error during soft delete for user: " + userName, e);        }
    }
}
