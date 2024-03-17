package it.uninaSocialGroup.Controllers;

import it.uninaSocialGroup.Oggetti.Group;
import it.uninaSocialGroup.Oggetti.User;
import it.uninaSocialGroup.Utils.DBUtil;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import it.uninaSocialGroup.Utils.FileUploadUtility;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class globalController {
    private User currentUser;
    @FXML
    private VBox createPost;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println(currentUser);
        connectToDatabase();
        initializeSlidePanel();
        createPost.setVisible(false);
        showProfileOrGroupList(currentUser.getProfilePictureLink(), currentUser.getNomeUtente(), usernameAlto, proPic, 70, 70);
        getGroupsFromDatabaseAndDisplay();
    }

    @FXML
    private Rectangle proPic;
    @FXML
    private BorderPane borderPane;
    @FXML
    private StackPane rightStackPane;
    @FXML
    private ScrollPane centerScrollPane;
    @FXML
    private Label usernameAlto;
    @FXML
    private VBox leftVBox;
    @FXML
    private Button newPostButton;
    @FXML
    private Label path;

    @FXML
    private TextArea postContent;

    private boolean isVBoxOpen = true; // supponiamo che il VBox sia aperto all'inizio
    private Connection connection;
    private String link = "https://drive.google.com/uc?export=view&id=1DvpLvwgBZaKlSDmQdgCV1fCfIqhnRWU7";
    public void showProfileOrGroupList(String profilePictureLink, String username, Label nameLabel, Rectangle imagePlaceholder, double width, double height) {
        if (nameLabel != null){
            nameLabel.setText(username);
        }

        final String finalProfilePictureLink = profilePictureLink;
        Task<Image> loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                String link = finalProfilePictureLink;
                if (link == null || link.equals("")) {
                    link = getClass().getResource("/Immagini/user.png").toExternalForm();
                }
                return new Image(link, width, height, true, true);
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            Image image = loadImageTask.getValue();
            ImagePattern pattern = new ImagePattern(image);
            imagePlaceholder.setFill(pattern);
            imagePlaceholder.setEffect(new DropShadow(20, Color.BLACK));
        });

        new Thread(loadImageTask).start();
    }

    public void connectToDatabase() {
        try {
            this.connection = DBUtil.getConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void initializeSlidePanel() {
        rightStackPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newStackPaneWidth = newValue.doubleValue();
            double newCenterWidth = borderPane.getWidth() - newStackPaneWidth;

            centerScrollPane.setPrefWidth(newCenterWidth);
        });
        newPostButton.setVisible(false);
    }

    @FXML
    public void toggleVBox() {
        if (isVBoxOpen) {
            rightStackPane.setPrefWidth(35); // chiude il VBox
        } else {
            rightStackPane.setPrefWidth(240); // apre il VBox, supponendo che la larghezza originale sia 200
        }
        isVBoxOpen = !isVBoxOpen; // aggiorna lo stato del VBox
    }


    private void getGroupsFromDatabaseAndDisplay() {
        String query = "SELECT groupDetails.id_gruppo, groupDetails.fotoGruppo, groupDetails.nome " +
                "FROM gruppi AS groupDetails " +
                "INNER JOIN group_members AS participantDetails ON groupDetails.id_gruppo = participantDetails.id_gruppo " +
                "WHERE participantDetails.nomeUtente = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currentUser.getNomeUtente());
            System.out.println("Debug: getGroupsFromDatabaseAndDisplay -> currentUser: " + currentUser);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id_gruppo");
                    String fotoGruppo = resultSet.getString("fotoGruppo");
                    String nome = resultSet.getString("nome");

                    // Creazione dei componenti
                    Rectangle rectangle = new Rectangle();
                    rectangle.setId("rectangle" + nome);
                    Label label = new Label();
                    label.setId("label" + nome);

                    Node groupComponent = createGroupComponent(rectangle, String.valueOf(id), label, nome);
                    leftVBox.getChildren().add(groupComponent);
                    showProfileOrGroupList(fotoGruppo, nome, label, rectangle, 70, 70);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Node createGroupComponent(Rectangle rectangle, String buttonId, Label label, String labelText) {
        // Impostazione del rettangolo
        rectangle.setArcHeight(40.0);
        rectangle.setArcWidth(70.0);
        rectangle.setHeight(40.0);
        rectangle.setWidth(40.0);
        rectangle.setStyle("-fx-stroke: linear-gradient(from 0% 0% to 100% 100%, rgb(61, 237, 253) 44.2%, rgb(3, 136, 238) 95.6%); -fx-stroke-width: 2px;");

        // Impostazione del label
        label.setText(labelText);
        label.setStyle("-fx-background-color: transparent;");
        HBox.setMargin(label, new Insets(0, 0, 0, 10));

        // Creazione del button
        Button button = new Button();
        button.setId(buttonId);
        button.setPrefHeight(50.0);
        button.setPrefWidth(212.0);
        button.setMaxWidth(212.0);

        button.setStyle("-fx-background-radius: 24px; -fx-border-color: rgba(224, 224, 224, 0.2); -fx-border-radius: 26px;");


        // Creazione dell'HBox e aggiunta dei componenti
        HBox hbox = new HBox();
        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hbox.setPrefHeight(42.0);
        hbox.setPrefWidth(198.0);

        hbox.setStyle("-fx-background-color: transparent; ");
        hbox.getChildren().addAll(rectangle, label);
        hbox.setPadding(new Insets(0, 0, 0, -5));

        // Impostazione dell'HBox come grafica del button
        button.setGraphic(hbox);
        button.setOnAction(actionEvent -> handleGroupButtonClick(buttonId));
        return button;
    }

    //il pulsante deve: caricare il titolo del gruppo nella ui, dove sta il vbox
    //caricare i post del gruppo nella ui, dove sta la scrollpane
    //settare su visibile il componente creapost
    @FXML
    private VBox centerVBox;
    public int idActiveGroup = 0;

    @FXML
    private StackPane centerStackPane;



    private void handleGroupButtonClick(String buttonId) {
        idActiveGroup = Integer.parseInt(buttonId);
        loadCentralComponent(buttonId);
        getPostsFromDatabase(idActiveGroup);
        postContent.clear();
        path.setText("");
        createPost.setVisible(false);
        //rende visibile il componente creaPost solo se non ce ne sono altri
        newPostButton.setVisible(true);
    }
    @FXML
    private void toggleCreatePost() {
        createPost.setVisible(false);
    }
    @FXML
    private void showCreatePost() {
        System.out.println("showCreatePost");
        System.out.println(this.currentUser);
        System.out.println(idActiveGroup);
        postContent.clear();
        path.setText("");
        createPost.setVisible(true);
    }

    @FXML VBox mainviewVbox;
    private  void getPostsFromDatabase(int id) {

        String query = "SELECT * FROM Posts WHERE group_id = ? ORDER BY timestamp";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String author = resultSet.getString("author_username");
                    String text = resultSet.getString("content");
                    String postPicture = resultSet.getString("postPicture");
                    String userProfilePicture = resultSet.getString("user_profile_picture");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");

                    createAndLoadPostComponent(author, text, postPicture, userProfilePicture, timestamp.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Rectangle authorPic;
    @FXML
    private Label authorName;
    @FXML
    private TextArea postText;
    @FXML
    private Label postDate;
    @FXML
    private Rectangle postPic;
   @FXML
   private VBox postBox;

    @FXML
    private void createAndLoadPostComponent(String author, String text, String postPicture, String userProfilePicture, String timestamp) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/post.fxml"));
            fxmlLoader.setController(this);

            // Caricamento del componente
            Node postComponent = fxmlLoader.load();
            mainviewVbox.getChildren().add(0, postComponent);


            showProfileOrGroupList(userProfilePicture, author, authorName, authorPic, 40, 40);

            if (postPicture == null || postPicture.equals("")) {
                postBox.getChildren().remove(postPic);
            } else {
                showProfileOrGroupList(postPicture, null, null, postPic, 457, 457);
            }
            postText.setText(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCentralComponent(String buttonId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/centralGrouPane.fxml"));
            fxmlLoader.setController(this);

            // Caricamento del componente
            Node groupComponent = fxmlLoader.load();

            // Rimozione del primo figlio se è un Button (se lo è significa che c'è un gruppo attivo)
            if (centerVBox.getChildren().get(0) instanceof Button) {
                centerVBox.getChildren().remove(0);
                mainviewVbox.getChildren().clear();
            }
            //utilizzato perché non ho altro modo di far combaciare i bordi nel componente caricato dinamicamente.
            if (groupComponent instanceof Button) {
                VBox.setMargin(groupComponent, new Insets(4, 0, 4, 0));
            }
            // Aggiunta del componente al VBox come primo figlio
            centerVBox.getChildren().add(0, groupComponent);
            getGroupPicAndName(Integer.parseInt(buttonId));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private Rectangle ActiveGroupPic;
    @FXML
    private Label ActiveGroupName;
    private void getGroupPicAndName(int id) {
        String query = "SELECT fotoGruppo, nome FROM gruppi WHERE id_gruppo = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String fotoGruppo = resultSet.getString("fotoGruppo");
                    String nome = resultSet.getString("nome");

                    showProfileOrGroupList(fotoGruppo, nome, ActiveGroupName, ActiveGroupPic, 70 ,70);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private FileUploadUtility fileUploadUtility = new FileUploadUtility();

    @FXML
    private void handleChoosePhoto() {
        fileUploadUtility.chooseProfilePicture();
        String selectedFilePath = fileUploadUtility.getSelectedProfilePicturePath();
        path.setText(selectedFilePath);
    }


    @FXML
    private void handleuploadPost() {
        System.out.println("uploading post");
        System.out.println(currentUser);
        System.out.println(idActiveGroup);
        uploadPost(idActiveGroup, currentUser);
        createPost.setVisible(false);

    }


    private void uploadPost(int groupId, User currentUser) {
        String postText = postContent.getText();
        if (postText.isEmpty()){
            createPost.setVisible(false);
            return;
        }
        String postPictureLink = "";
        if (!path.getText().isEmpty()){
            postPictureLink = fileUploadUtility.uploadProfilePicture();
        }

        // Genera un nuovo timestamp ogni volta che il metodo viene chiamato
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String sql = "INSERT INTO Posts (content, postpicture, author_username, user_profile_picture, timestamp, group_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, postText);
            stmt.setString(2, postPictureLink);
            stmt.setString(3, currentUser.getNomeUtente());
            stmt.setString(4, currentUser.getProfilePictureLink());
            stmt.setTimestamp(5, timestamp);
            stmt.setInt(6, groupId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //refresh dei post
        mainviewVbox.getChildren().clear();
        getPostsFromDatabase(groupId);
    }

}