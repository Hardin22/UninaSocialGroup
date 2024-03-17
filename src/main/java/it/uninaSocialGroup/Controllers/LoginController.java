package it.uninaSocialGroup.Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.uninaSocialGroup.Oggetti.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import it.uninaSocialGroup.Utils.DBUtil;
import javafx.stage.Stage;
import javafx.scene.Node;
import it.uninaSocialGroup.Controllers.globalController;
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private User currentUser;

    @FXML
    protected void handleLogin(ActionEvent event) {

        String username = usernameField.getText();
        String password = passwordField.getText();

        String errorMessage = getErrorType(username, password);

        if (errorMessage != null) {
            showError(errorMessage);
        } else {
            getCurrentUser(username);
            loadMainScene();
        }
    }

    private void showError(String error){
        errorLabel.setText(error);
        errorLabel.setVisible(true);
    }

    private String getErrorType(String username, String password){
        if (username.isEmpty()) {
            return "Il campo email non può essere vuoto";
        }

        if (password.isEmpty()) {
            return "Il campo password non può essere vuoto";
        }

        int loginResult = isLoginValid(username, password);

        switch (loginResult) {
            case 1:
                return "Username errato o inesistente";
            case 2:
                return "Password errata";
            case -1:
                return "Errore del database";
            default:
                return null;
        }
    }


    private int isLoginValid(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sqlCheckUsername = "SELECT * FROM users WHERE nomeUtente = ?";
            stmt1 = conn.prepareStatement(sqlCheckUsername);
            stmt1.setString(1, username);

            rs = stmt1.executeQuery();

            if (!rs.next()) {
                return 1; // username non trovato
            }

            String sqlCheckPassword = "SELECT * FROM users WHERE nomeUtente = ? AND password = ?";
            stmt2 = conn.prepareStatement(sqlCheckPassword);
            stmt2.setString(1, username);
            stmt2.setString(2, password);

            rs = stmt2.executeQuery();

            if (!rs.next()) {
                return 2; // Password errata
            }

            return 0; // Login valido
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // errore database
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public User getCurrentUser(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT * FROM users WHERE nomeUtente = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String nomeUtente = rs.getString("nomeUtente");

                String nome = rs.getString("nome");

                String cognome = rs.getString("cognome");

                String email = rs.getString("email");

                String password = rs.getString("password");

                String matricola = rs.getString("matricola");

                String fotoprofilo = rs.getString("fotoprofilo");

                currentUser = new User(nomeUtente, nome, cognome, email, password, rs.getDate("dataDiNascita").toLocalDate(), matricola, fotoprofilo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return currentUser;
    }

    private void loadMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/principale3.fxml"));
            Parent root = loader.load();

            globalController globalController = (globalController) loader.getController();
            globalController.setCurrentUser(currentUser);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setTitle("Unina Social Group");
            stage.show();

            ((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void switchToCreateAccount(ActionEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage primaryStage = (Stage) node.getScene().getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/ui/registrati.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            // Imposta la dimensione minima e massima della finestra
            primaryStage.setMinWidth(742.0);
            primaryStage.setMinHeight(604.0);
            primaryStage.setMaxWidth(742.0);
            primaryStage.setMaxHeight(604.0);

            primaryStage.setTitle("Crea un account");
            primaryStage.setResizable(false);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}