package edu.najah.cap.data.deleteservice.exceptionhandler;
import org.bson.Document;
import java.util.List;
import java.util.Map;

public interface IDataBackup {
    void backupUserData(String userName);
    Map<String, List<Document>> getUserBackupData(String userName);

}
