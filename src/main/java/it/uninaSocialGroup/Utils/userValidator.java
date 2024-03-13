package it.uninaSocialGroup.Utils;
import it.uninaSocialGroup.Utils.DBUtil;
import it.uninaSocialGroup.Utils.GoogleDriveService;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class userValidator {
    Connection conn;

    {
        try {
            conn = DBUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //LoginController




    //RegistratiController

    public String validateUsername(String username) {
        if (username.isEmpty()) {
            return "Il campo username non può essere vuoto";
        }
        if (username.length() < 3) {
            return "Lo username deve essere lungo almeno 3 caratteri";
        }
        if (username.length() > 14) {
            return "La lunghezza massima dello username è 14 caratteri";
        }
        if (!isFieldUnique(username, "nomeUtente", conn)) {
            return "Questo username esiste già. Se sei tu, passa a login";
        }
        return "ok";
    }

    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Il campo email non può essere vuoto";
        }
        // Controllo se l'email è nel formato corretto, permettendo solo email studenti unina, con caratteri speciali
        // e numeri (dato che se non sbaglio alcune mail unina contengono effettivamente numeri), in caso di omonimi.
        String regex = "^[0-9a-zA-Zàèìòùáéíóúçñ]+\\.[0-1a-zA-Zàèìòùáéíóúçñ]+@studenti\\.unina\\.it$";
        if (!Pattern.matches(regex, email)) {
            return "L'email deve avere il formato nome.cognome@studenti.unina.it";
        }
        if (!isFieldUnique(email, "email", conn)) {
            return "Questa email è già stata usata. Se sei tu, passa a login";
        }
        if (email.length() > 50) {
            return "L'email deve essere lunga al massimo 50 caratteri";
        }

        return "ok";
    }

    public String validatePassword(String password) {
        if (password.isEmpty()) {
            return "Il campo password non può essere vuoto";
        }
        if (password.length() < 8) {
            return "La password deve essere lunga almeno 8 caratteri";
        }
        // Controllo presenza di almeno una lettera maiuscola, minuscola e numero
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";
        if (!Pattern.matches(regex, password)) {
            return "La password deve contenere almeno una lettera maiuscola, una minuscola e un numero";
        }

        return "ok";
    }

    public String validatePasswordConfirmation(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Le password non corrispondono";
        }
        return "ok";
    }

    public String validateName(String name) {
        if (name.isEmpty()) {
            return "Il campo nome non può essere vuoto";
        }
        if (name.length() > 30) {
            return "Il nome deve essere lungo al massimo 30 caratteri";
        }
        if (name.matches(".*\\d.*")) {
            return "il nome non può contenere numeri";
        }
        if (name.matches(".*[.,_:;|/()!\"£$%].*")) {
            return "il nome non può contenere caratteri speciali";
        }
        return "ok";
    }

    public String validateSurname(String surname) {
        if (surname.isEmpty()) {
            return "Il campo cognome non può essere vuoto";
        }
        if (surname.length() > 30) {
            return "Il cognome deve essere lungo al massimo 30 caratteri";
        }
        if (surname.matches(".*\\d.*")) {
            return "il cognome non può contenere numeri";
        }
        if (surname.matches(".*[.,_:;|/()!\"£$%].*")) {
            return "il cognome non può contenere caratteri speciali";
        }
        return "ok";
    }

    public String validateMatricola(String matricola) {
        if (matricola.isEmpty()) {
            return "Il campo matricola non può essere vuoto";
        }
        if (matricola.length() != 9) {
            return "La matricola deve essere lunga 9 caratteri";
        }
        if (!isFieldUnique(matricola, "matricola", conn)) {
            return "Questa matricola è già stata usata. Se sei tu, passa a login";
        }
        return "ok";
    }
    public String validateProfilePicture(String profilePictureLink) {
        if (profilePictureLink.isEmpty()) {
            return "";
        } else if (profilePictureLink.startsWith("Errore")) {
            return profilePictureLink;
        } else {
            return "ok";
        }
    }

    public boolean isFieldUnique(String fieldValue, String fieldName, Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sqlCheckField = "SELECT * FROM users WHERE " + fieldName + " = ?";
            stmt = conn.prepareStatement(sqlCheckField);
            stmt.setString(1, fieldValue);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // errore database
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}