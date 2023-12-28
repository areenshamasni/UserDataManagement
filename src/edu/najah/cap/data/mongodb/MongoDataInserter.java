package edu.najah.cap.data.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.function.Function;

public class MongoDataInserter {
    private final MongoDatabase database;

    public MongoDataInserter(MongoDatabase database) {
        this.database = database;
    }

    public <T> void insertDocument(String collectionName, T item, Function<T, Document> documentMapper) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = documentMapper.apply(item);
        collection.insertOne(document);
    }
}