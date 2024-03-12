package it.uninaSocialGroup.Utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.ByteArrayContent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
public class GoogleDriveService {
    private static final String APPLICATION_NAME = "UninaSocialGroup";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String SERVICE_ACCOUNT_KEY_FILE = "/Users/Francesco/Desktop/UninaSocialGroup/src/main/resources/google/serviceWorker.json";

    private Drive service;
    private static final Logger LOGGER = Logger.getLogger(GoogleDriveService.class.getName());

    public GoogleDriveService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        LOGGER.info("Creating Drive service...");
        service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        LOGGER.info("Drive service created.");
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        LOGGER.info("Getting credentials...");
        Credential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_FILE))
                .createScoped(SCOPES);
        LOGGER.info("Credentials obtained.");
        return credential;
    }


    public String uploadFile(java.io.File filePath, String mimeType) throws IOException {
        LOGGER.info("Uploading file...");
        if (!filePath.exists() || !filePath.isFile()) {
            throw new IOException("File does not exist: " + filePath);
        }

        BufferedImage originalImage = ImageIO.read(filePath);
        if (originalImage == null) {
            throw new IOException("File is not a valid image or format not supported: " + filePath);
        }

        File fileMetadata = new File();
        fileMetadata.setName(filePath.getName() + ".png");
        fileMetadata.setParents(Collections.singletonList("1ggR2dkODSBi6H2wTaJloimFxmzll8Gm4")); // ID della cartella

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        ByteArrayContent mediaContent = new ByteArrayContent("image/png", bytes);
        File file = service.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink")
                .execute();

        LOGGER.info("File uploaded.");
        String downloadLink = file.getWebContentLink();
        return convertDriveLink(downloadLink);
    }
    private String convertDriveLink(String downloadLink) {
        String fileId = downloadLink.split("id=")[1].split("&")[0];
        return "https://drive.google.com/uc?export=view&id=" + fileId;
    }
}