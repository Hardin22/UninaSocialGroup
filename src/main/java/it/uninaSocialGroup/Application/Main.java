package it.uninaSocialGroup.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FontIcon icon = new FontIcon(FontAwesomeSolid.SEARCH);
        Font.loadFont(getClass().getResource("/fonts/Poppins/Poppins-Regular.ttf").toExternalForm(), 14);
        Image applicationIcon = new Image(getClass().getResourceAsStream("/Immagini/copialogo.png"));
        primaryStage.getIcons().add(applicationIcon);
        Parent root = FXMLLoader.load(getClass().getResource("/ui/login.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/styles/root.css").toExternalForm());
        primaryStage.setTitle("Login");

        primaryStage.setMinWidth(654.0);
        primaryStage.setMinHeight(400.0);
        primaryStage.setMaxWidth(654.0);
        primaryStage.setMaxHeight(400.0);

        primaryStage.setResizable(false);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}