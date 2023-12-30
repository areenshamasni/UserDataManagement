package edu.najah.cap.data.mongodb;

import org.bson.Document;

public interface IDocMapper<T> {
    public Document mapToDocument(T service);
}
