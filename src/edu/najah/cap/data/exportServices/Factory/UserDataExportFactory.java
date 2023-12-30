package edu.najah.cap.data.exportServices.Factory;
import edu.najah.cap.data.exportServices.usersExporter.UserDataExport;
import edu.najah.cap.data.exportServices.usersExporter.NewUserDataExport;
import edu.najah.cap.data.exportServices.usersExporter.PremiumUserDataExport;
import edu.najah.cap.data.exportServices.usersExporter.RegularUserDataExport;
public class UserDataExportFactory {
    public static UserDataExport getExportStrategy(String userType) {
        return switch (userType) {
            case "PREMIUM_USER" -> new PremiumUserDataExport();
            case "REGULAR_USER" -> new RegularUserDataExport();
            case "NEW_USER" -> new NewUserDataExport();
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };
    }
}