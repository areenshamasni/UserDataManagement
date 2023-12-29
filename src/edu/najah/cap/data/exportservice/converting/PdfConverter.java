package edu.najah.cap.data.exportservice.converting;

import edu.najah.cap.iam.UserProfile;

public interface PdfConverter {
    void convertToPdf(UserProfile userProfile, String outputPath);
}
