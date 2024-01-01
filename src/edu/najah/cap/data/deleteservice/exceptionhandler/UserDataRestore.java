package edu.najah.cap.data.deleteservice.exceptionhandler;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UserDataRestore implements IDataRestore {
    private static final Logger logger = LoggerFactory.getLogger(UserDataRestore.class);
    private final MongoDatabase database;
    private final List<String> duplicateDocumentWarnings = new ArrayList<>();
    public UserDataRestore(MongoDatabase database) {
        this.database = database;
    }
    @Override
    public void restoreUserData(String userName,  Map<String, List<Document>> userBackup ) {
        if (userBackup == null) {
            logger.warn("No backup found for user: {}", userName);
            return;
        }
        for (String collectionName : userBackup.keySet()) {
            List<Document> documents = userBackup.get(collectionName);
            if (documents != null) {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                restoreDocuments(collection, documents, userName);
            }
        }
        logger.info("User data for {} restored successfully", userName);
    }
    private void restoreDocuments(MongoCollection<Document> collection, List<Document> documents, String userName) {
        for (Document document : documents) {
            try {
                Document existingDocument = collection.find(new Document("_id", document.get("_id"))).first();
                if (existingDocument == null) {
                    insertDocument(collection, document, userName);
                } else {
                    logDocumentAlreadyExists(document, collection.getNamespace().getCollectionName(), userName);
                }
            } catch (Exception e) {
                logErrorDuringInsert(collection.getNamespace().getCollectionName(), userName, e);
            }
        }
    }
    private void insertDocument(MongoCollection<Document> collection, Document document, String userName) {
        collection.insertOne(document);
        logger.info("Inserted document in collection '{}', user: {}", collection.getNamespace().getCollectionName(), userName);
    }
    private void logDocumentAlreadyExists(Document document, String collectionName, String userName) {
        if (!duplicateDocumentWarnings.contains(collectionName)) {
            logger.warn("Document with _id '{}' already exists in collection '{}', user: {}", document.get("_id"), collectionName, userName);
            duplicateDocumentWarnings.add(collectionName);
        }
    }
    private void logErrorDuringInsert(String collectionName, String userName, Exception e) {
        logger.error("Error inserting document in collection '{}', user: {}", collectionName, userName, e);
    }
}