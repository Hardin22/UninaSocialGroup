package it.uninaSocialGroup.Utils;

import it.uninaSocialGroup.Utils.GoogleDriveService;
import javafx.stage.FileChooser;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

public class FileUploadUtility {

    private File selectedProfilePicture;

    public String uploadProfilePicture() {
        if (selectedProfilePicture == null) {
            return "";
        }

        GoogleDriveService googleDriveService;
        try {
            googleDriveService = new GoogleDriveService();
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante la creazione del servizio Google Drive";
        }

        String mimeType;
        try {
            Tika tika = new Tika();
            mimeType = tika.detect(selectedProfilePicture);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        String result;
        try {
            result = googleDriveService.uploadFile(selectedProfilePicture, mimeType);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        return result;
    }

    public void chooseProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        selectedProfilePicture = fileChooser.showOpenDialog(null);
    }
    public String getSelectedProfilePicturePath() {
        if (selectedProfilePicture != null) {
            return selectedProfilePicture.getAbsolutePath();
        } else {
            return "";
        }
    }
}