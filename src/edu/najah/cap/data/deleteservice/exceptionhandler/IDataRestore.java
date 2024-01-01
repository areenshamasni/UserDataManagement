package edu.najah.cap.data.deleteservice.exceptionhandler;

import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface IDataRestore {
        void restoreUserData(String userName,  Map<String, List<Document>> userBackup );
    }

