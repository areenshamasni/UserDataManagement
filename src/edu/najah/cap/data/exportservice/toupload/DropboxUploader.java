package edu.najah.cap.data.exportservice.toupload;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedFolderMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dropbox.core.v2.sharing.ListFoldersResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//this class does not work correctly , the issue in the shared folder relation with the used API
public class DropboxUploader implements IFileUploadStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DropboxUploader.class);
    private static final String ACCESS_TOKEN = "sl.Bs0OuJuE8vlhAKE4L1b_REa2JklvJLe4WwDJ_YOkkZsvmtQ0igSHSkZjXWIWgwWUw_-PQSWeJEvmRbzAT33DggBWaEZMBQUCat3H6XThsg3ZCQe6zauJrgtVhKBs_t0Bf3eITgKcRf1iU2-vOs9PmBM";

    public void uploadFile(String filePath, String sharedLink) throws IOException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Ar-UserData").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        String sharedFolderId = getSharedFolderId(client, sharedLink);

        if (sharedFolderId == null) {
            logger.error("Error: Shared folder ID not found for the provided shared link.");
            return;
        }
        String destinationPath = "/UserData.zip";

        try (InputStream in = new FileInputStream(filePath)) {
            FileMetadata metadata = client.files().uploadBuilder(sharedFolderId + destinationPath)
                    .withMode(WriteMode.ADD)
                    .withClientModified(new java.util.Date())
                    .uploadAndFinish(in);
        } catch (DbxException | IOException e) {
            logger.error("Error uploading file to Dropbox: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        logger.info("Uploading file to Dropbox shared folder completed successfully");
    }

    private String getSharedFolderId(DbxClientV2 client, String sharedLinkPath) {
        try {
            ListFoldersResult listFoldersResult = client.sharing().listFolders();

            for (SharedFolderMetadata folderMetadata : listFoldersResult.getEntries()) {
                logger.info("Shared Folder Details - Path: {}, ID: {}", folderMetadata.getPathDisplay(), folderMetadata.getSharedFolderId());

                String constructedPath = folderMetadata.getPathDisplay() + "/UserData";
                String sharedLinkBasePath = sharedLinkPath.split("\\?")[0];

                if (constructedPath.equals(sharedLinkBasePath)) {
                    return folderMetadata.getSharedFolderId();
                }
            }
        } catch (DbxException e) {
            logger.error("Error listing shared folders: {}", e.getMessage());
        }
        return null;
    }
}
