package it.uninaSocialGroup.Controllers;

import it.uninaSocialGroup.Oggetti.User;
import it.uninaSocialGroup.Utils.DBUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import it.uninaSocialGroup.Utils.GoogleDriveService;
import javafx.stage.FileChooser;
import it.uninaSocialGroup.Utils.userValidator;
import org.apache.tika.Tika;
import it.uninaSocialGroup.Utils.FileUploadUtility;
public class RegistratiController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField matricolaField;
    @FXML
    private DatePicker birthdateField;
    @FXML
    private Label errorLabel;
    private userValidator validator = new userValidator();
    private File selectedProfilePicture;


    private FileUploadUtility fileUploadUtility = new FileUploadUtility();

    @FXML
    public void handleCreateAccount(ActionEvent event) {
        String profilePictureLink = "";
        if (allControlsPassed(profilePictureLink)) {
            profilePictureLink = fileUploadUtility.uploadProfilePicture();
            User newUser = createNewUser(profilePictureLink);
            addUserToDatabase(newUser);
            loadMainScene();
        }
    }

    private User createNewUser(String profilePictureLink) {
        String username = usernameField.getText();
        String nome = nameField.getText();
        String cognome = surnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        LocalDate dataDiNascita = birthdateField.getValue();
        String matricola = matricolaField.getText();

        User newUser = new User(username, nome, cognome, email, password, dataDiNascita, matricola, profilePictureLink);
        return newUser;
    }

    private boolean allControlsPassed(String profilePictureLink) {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String matricola = matricolaField.getText();

        String[] validationErrors = new String[8];
        validationErrors[0] = validator.validateName(name);
        validationErrors[1] = validator.validateSurname(surname);
        validationErrors[2] = validator.validateUsername(username);
        validationErrors[3] = validator.validateEmail(email);
        validationErrors[4] = validator.validatePassword(password);
        validationErrors[5] = validator.validatePasswordConfirmation(password, confirmPassword);
        validationErrors[6] = validator.validateMatricola(matricola);
        validationErrors[7] = profilePictureLink.isEmpty() ? "ok" : validator.validateProfilePicture(profilePictureLink);

        boolean allValid = true;
        StringBuilder errorMessage = new StringBuilder();

        for (String error : validationErrors) {
            if (!error.equals("ok")) {
                allValid = false;
                errorMessage.append(error).append("\n");
            }
        }

        if (!allValid) {
            errorLabel.setText(errorMessage.toString());
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
        }

        return allValid;
    }

    @FXML
    public void handleChooseProfilePicture(ActionEvent event) {
        fileUploadUtility.chooseProfilePicture();
    }

    public void addUserToDatabase(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "INSERT INTO users (nomeUtente, nome, cognome, email, password, dataDiNascita, matricola, fotoprofilo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getNomeUtente());
            stmt.setString(2, user.getNome());
            stmt.setString(3, user.getCognome());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setDate(6, java.sql.Date.valueOf(user.getDataDiNascita()));
            stmt.setString(7, user.getMatricola());
            stmt.setString(8, user.getProfilePictureLink());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(stmt, conn);
        }
    }

    @FXML
    protected void switchToLogin(ActionEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage primaryStage = (Stage) node.getScene().getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/ui/login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Imposta la dimensione minima e massima della finestra
            primaryStage.setMinWidth(654.0);
            primaryStage.setMinHeight(400.0);
            primaryStage.setMaxWidth(654.0);
            primaryStage.setMaxHeight(400.0);

            primaryStage.setTitle("Login");
            primaryStage.setResizable(false);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadMainScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui/principale2.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setTitle("Unina Social Group");
            stage.show();

            // Chiudi la schermata di login
            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}