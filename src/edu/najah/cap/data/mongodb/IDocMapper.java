package edu.najah.cap.data.mongodb;

import org.bson.Document;

public interface IDocMapper<T> {
    Document mapToDocument(T service);
}
