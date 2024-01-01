package edu.najah.cap.data.deleteservice;

import edu.najah.cap.exceptions.SystemBusyException;

public interface IDeleteService {
    void deleteUserData(String userName) throws SystemBusyException;
}
