# UserDataManagement

## Project Description
The objective of this project is to develop a new backend feature Named "user data" this feature empowers users to manage and access information collected about themselves. Users can export their data and delete it from the system.

## [The Class diagram ](https://lucid.app/lucidchart/0473112a-2473-432b-89c6-30ff3f2db69a/edit?viewport_loc=-2143%2C-1365%2C7768%2C3251%2CHWEp-vi-RSFO&invitationId=inv_be34bf7c-ec46-4db6-a8e7-0b07101d51e2) , [The Concrete architecture ](https://lucid.app/lucidchart/c2ded94b-15e5-4247-b190-63e51d8700ec/edit?viewport_loc=2043%2C-1921%2C8166%2C2855%2C0_0&invitationId=inv_5583f1b5-400d-46d8-8332-ef93df744ab1) ,  [The Report ](https://docs.google.com/document/d/1y7KK2PUVonjY5515e0uE8kI9WHnO2bQy1oip2dOvHkg/edit?usp=sharing)

## Code Structure Overview
The generated code encompasses several files and classes. Below is a brief overview of each new interface and its purpose:

- **Interfaces:**
    - IDeleteService.java: Interface for the delete service.
    - IDataBackup.java: Interface for data backup operations.
    - IDataRestore.java: Interface for data restoration operations.
    - IFileCompressor.java: Interface for file compression.
    - IPdfConverter.java: Interface for PDF conversion.
    - ILocalStorage.java: Interface for local storage operations.
    - IFileUploadStrategy.java: Interface for file upload strategies.
    - IDocExporter.java: Interface for document exporting.
    - IDocMapper.java: Interface for mapping documents.

- **Configuration Files:**
    - application.properties: Configuration file that will contain the base sensitive configuration links.
    - credentials.json: JSON file containing credentials that will use Google Drive API.
    - logback.xml: Logging configuration file.

## Code Explanation

### Data Deletion
The code for data deletion employs the factory design pattern. `DeleteFactory` creates instances of `IDeleteService` based on the provided `DeleteType`. `HardDelete` and `SoftDelete` classes implement the `IDeleteService` interface, focusing on deleting user data. Exception handling is managed by the `SystemBusyException` class, ensuring data integrity through backup and restoration.

### Exception Handler
The exception handler code adheres to good software design practices. The `UserDataBackup` class handles user data backup, while `UserDataRestore` manages data restoration. The use of interfaces (`IDataBackup` and `IDataRestore`) ensures loose coupling and facilitates easy modification or extension of the backup and restore mechanisms.

### Export User Data
The code for exporting user data doesn't explicitly follow a design pattern but adheres to SOLID principles. It separates exporting functionality into distinct classes, each responsible for a specific data type. Logging is employed to record the export process for monitoring, debugging, and accountability.

### Upload to File Storage and Download Directly
The code for file upload and download utilizes the strategy design pattern. It defines an interface for file upload strategies and provides two implementations. Each implementation class has a single responsibility, and the code is open for extension to support additional file storage types or upload techniques without modifying existing code. Logging is implemented to ensure accountability and aid in problem tracking and application monitoring.

### MongoDB
The code for MongoDB processing employs the singleton design pattern to ensure a single instance of the `MongoConnection` class for managing the MongoDB connection. `DataInserter` utilizes an ExecutorService with a fixed thread pool of 4 threads to concurrently insert data for multiple users. This improves performance by parallelize the insertion process. Each class has a single responsibility, adhering to SOLID principles. Logging is used to record MongoDB operations, handle exceptions, and aid in debugging, monitoring, and accountability.

## Conclusion
This is an overview of the code generated for the data management system project. Each file and class's purpose and functionality have been explained. The code adheres to good software design practices and SOLID principles, utilizing design patterns for flexibility, extensibility, and maintainability. Exception handling and logging contribute to system accountability, supporting debugging and monitoring. Overall, the code meets the project requirements, providing a robust and efficient data management system.
