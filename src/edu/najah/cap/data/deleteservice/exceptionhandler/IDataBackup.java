package edu.najah.cap.data.deleteservice.exceptionhandler;

import org.bson.Document;
import java.util.List;
import java.util.Map;

public interface IDataBackup {
    Map<String, List<Document>> backupUserData(String userName);
}
