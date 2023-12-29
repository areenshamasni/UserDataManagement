package edu.najah.cap.data;

import com.mongodb.MongoException;
import edu.najah.cap.activity.IUserActivityService;
import edu.najah.cap.activity.UserActivity;
import edu.najah.cap.activity.UserActivityService;
import edu.najah.cap.data.deleteservice.DeleteFactory;
import edu.najah.cap.data.deleteservice.DeleteType;
import edu.najah.cap.data.deleteservice.IDeleteService;
import edu.najah.cap.data.mongodb.DataInserter;
import edu.najah.cap.data.mongodb.MongoConnection;
import edu.najah.cap.data.mongodb.MongoDataInserter;
import edu.najah.cap.exceptions.Util;
import edu.najah.cap.iam.IUserService;
import edu.najah.cap.iam.UserProfile;
import edu.najah.cap.iam.UserService;
import edu.najah.cap.iam.UserType;
import edu.najah.cap.payment.IPayment;
import edu.najah.cap.payment.PaymentService;
import edu.najah.cap.payment.Transaction;
import edu.najah.cap.posts.IPostService;
import edu.najah.cap.posts.Post;
import edu.najah.cap.posts.PostService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;
import java.util.Scanner;

public class Application {

    private static final IUserActivityService userActivityService = new UserActivityService();
    private static final IPayment paymentService = new PaymentService();
    private static final IUserService userService = new UserService();
    private static final IPostService postService = new PostService();
    private static String loginUserName;

    public static void main(String[] args) {

        //generateRandomData();
        Instant start = Instant.now();
        System.out.println("Application Started: " + start);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        System.out.println("Note: You can use any of the following usernames: user0, user1, user2, user3, .... user99");
        String userName = scanner.nextLine();
        setLoginUserName(userName);
        //TODO Your application starts here. Do not Change the existing code
        Logger logger = LoggerFactory.getLogger(Application.class);
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/resources/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            logger.info("Error loading properties");
        }
        String connectionString = properties.getProperty("mongo.connection.string");
        MongoConnection mongoConnection = MongoConnection.getInstance(connectionString, "UserData");

        try {
            MongoDataInserter mongoDataInserter = new MongoDataInserter(mongoConnection.getDatabase());
            DataInserter dataInserter = new DataInserter(mongoDataInserter);
            //dataInserter.insertData(userActivityService, paymentService, userService, postService);
        } catch (MongoException e) {
            logger.error(e.getMessage(), e);
        }

        Document query = new Document("userId", userName);
        boolean userExists = mongoConnection.getDatabase().getCollection("users").find(query).limit(1).iterator().hasNext();

        if (userExists) {
            boolean validInput = false;
            int choice = 0;

            try {
                do {
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println("Hi, " + userName + ", what's your request?");
                    System.out.println("1: Export data & download it directly");
                    System.out.println("2: Export data & upload to file storage");
                    System.out.println("3: Delete request");
                    System.out.println("4: Exit");
                    System.out.print("Your choice: ");

                    try {
                        choice = scanner.nextInt();
                        validInput = true;
                    } catch (java.util.InputMismatchException e) {
                        scanner.nextLine();
                        logger.warn("Invalid input. Please enter a valid integer.");
                    }

                    if (validInput) {
                        switch (choice) {
                            case 1:
                                // Exporting data and downloading
                                break;
                            case 2:
                                // Exporting data and uploading to file storage
                                break;
                            case 3:
                                System.out.println("Choose delete type (hard/soft): ");
                                scanner.nextLine();

                                String deleteChoice = scanner.nextLine().trim().toUpperCase();

                                try {
                                    DeleteType deleteType = DeleteType.valueOf(deleteChoice);
                                    IDeleteService deleteService = DeleteFactory.createInstance(deleteType, connectionString, "UserData");

                                    long startTime = System.currentTimeMillis();
                                    deleteService.deleteUserData(userName);
                                    System.out.println(deleteChoice + " delete operation completed for user: " + userName);
                                    if (DeleteType.HARD.equals(deleteType)) {
                                        userExists = false;
                                    }
                                    long endTime = System.currentTimeMillis();
                                    long elapsedTime = endTime - startTime;
                                    System.out.println("Deleting data process took " + elapsedTime + " milliseconds.");
                                } catch (IllegalArgumentException e) {
                                    logger.error("Invalid delete type. Please choose 'hard' or 'soft'.");
                                }
                                break;
                            case 4:
                                System.out.println("Goodbye!");
                                break;
                            default:
                                logger.warn("Invalid choice. Please enter a valid option.");
                        }
                    }
                } while (userExists && (!validInput || choice != 4));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            logger.warn("You are not an existing user in our system.");
        }

        mongoConnection.closeMongoClient();

        //TODO Your application ends here. Do not Change the existing code
        Instant end = Instant.now();
        System.out.println("Application Ended: " + end);

    }

    private static void generateRandomData() {
        Util.setSkipValidation(true);
        for (int i = 0; i < 100; i++) {
            generateUser(i);
            generatePost(i);
            generatePayment(i);
            generateActivity(i);
        }
        System.out.println("Data Generation Completed");
        Util.setSkipValidation(false);
    }


    private static void generateActivity(int i) {
        for (int j = 0; j < 100; j++) {
            try {
                if (UserType.NEW_USER.equals(userService.getUser("user" + i).getUserType())) {
                    continue;
                }
            } catch (Exception e) {
                System.err.println("Error while generating activity for user" + i);
            }
            userActivityService.addUserActivity(new UserActivity("user" + i, "activity" + i + "." + j, Instant.now().toString()));
        }
    }

    private static void generatePayment(int i) {
        for (int j = 0; j < 100; j++) {
            try {
                if (userService.getUser("user" + i).getUserType() == UserType.PREMIUM_USER) {
                    paymentService.pay(new Transaction("user" + i, i * j, "description" + i + "." + j));
                }
            } catch (Exception e) {
                System.err.println("Error while generating payment for user" + i);
            }
        }
    }

    private static void generatePost(int i) {
        for (int j = 0; j < 100; j++) {
            postService.addPost(new Post("title" + i + "." + j, "body" + i + "." + j, "user" + i, Instant.now().toString()));
        }
    }

    private static void generateUser(int i) {

        UserProfile user = new UserProfile();
        user.setUserName("user" + i);
        user.setFirstName("first" + i);
        user.setLastName("last" + i);
        user.setPhoneNumber("phone" + i);
        user.setEmail("email" + i);
        user.setPassword("pass" + i);
        user.setRole("role" + i);
        user.setDepartment("department" + i);
        user.setOrganization("organization" + i);
        user.setCountry("country" + i);
        user.setCity("city" + i);
        user.setStreet("street" + i);
        user.setPostalCode("postal" + i);
        user.setBuilding("building" + i);
        user.setUserType(getRandomUserType(i));
        userService.addUser(user);
    }

    private static UserType getRandomUserType(int i) {
        if (i > 0 && i < 3) {
            return UserType.NEW_USER;
        } else if (i > 3 && i < 7) {
            return UserType.REGULAR_USER;
        } else {
            return UserType.PREMIUM_USER;
        }
    }

    public static String getLoginUserName() {
        return loginUserName;
    }

    private static void setLoginUserName(String loginUserName) {
        Application.loginUserName = loginUserName;
    }
}