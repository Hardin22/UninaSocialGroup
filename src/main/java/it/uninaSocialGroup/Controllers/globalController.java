package it.uninaSocialGroup.Controllers;

import it.uninaSocialGroup.Oggetti.User;
import it.uninaSocialGroup.Utils.DBUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import it.uninaSocialGroup.Utils.FileUploadUtility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class globalController {
    private User currentUser;
    @FXML
    private VBox createPost;
    @FXML
    private VBox createGroup;
    @FXML
    private ChoiceBox<String> monthChoice;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println(currentUser);
        connectToDatabase();
        initializeSlidePanel();
        createPost.setVisible(false);
        createGroup.setVisible(false);
        reportPanel.setVisible(false);
        showProfileOrGroupList(currentUser.getProfilePictureLink(), currentUser.getNomeUtente(), usernameAlto, proPic, 70, 70);
        getGroupsFromDatabaseAndDisplay();
        monthChoice.getItems().addAll("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
    }

    @FXML
    private Rectangle proPic;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox rightVBox;
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
    private VBox reportPanel;

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
        rightVBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newStackPaneWidth = newValue.doubleValue();
            double newCenterWidth = borderPane.getWidth() - newStackPaneWidth;

            centerScrollPane.setPrefWidth(newCenterWidth);
        });
        newPostButton.setVisible(false);
    }

    @FXML
    public void toggleVBox() {
        if (isVBoxOpen) {
            rightVBox.setPrefWidth(35); // chiude il VBox
        } else {
            rightVBox.setPrefWidth(240); // apre il VBox, supponendo che la larghezza originale sia 200
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
        reportPanel.setVisible(false);
        createGroup.setVisible(false);
        homePanel.setVisible(false);
        idActiveGroup = Integer.parseInt(buttonId);
        loadCentralComponent(buttonId);
        getPostsFromDatabase(idActiveGroup);
        postContent.clear();
        path.setText("");
        createPost.setVisible(false);
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
                    int idPost = resultSet.getInt("id");
                    String author = resultSet.getString("author_username");
                    String text = resultSet.getString("content");
                    String postPicture = resultSet.getString("postPicture");
                    String userProfilePicture = resultSet.getString("user_profile_picture");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");
                    int likeNumber = resultSet.getInt("numero_like");

                    createAndLoadPostComponent(author, text, postPicture, userProfilePicture, timestamp.toString(), idPost, likeNumber);
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
    private void createAndLoadPostComponent(String author, String text, String postPicture, String userProfilePicture, String timestamp, int idPost, int likeNumber) {
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
            this.LikeNumber.setText(String.valueOf(likeNumber));
            postComponent.setId(String.valueOf(idPost));
            postText.setText(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Label LikeNumber;
    @FXML
    private void incrementLikeNumber(ActionEvent e) {
        Node postComponent = (Node) e.getSource();
        String postId = postComponent.getId();
        handleLikeButton(postId);
        this.LikeNumber.setText(String.valueOf(Integer.parseInt(LikeNumber.getText()) + 1));
    }
    private void handleLikeButton(String postId) {
        String query = "UPDATE Posts SET numero_like = numero_like + 1 WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Integer.parseInt(postId));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private Node groupComponent;

    private void loadCentralComponent(String buttonId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/centralGrouPane.fxml"));
            fxmlLoader.setController(this);

            // Caricamento del componente
            groupComponent = fxmlLoader.load();

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
    @FXML
    private void deletePath(){
        path.setText("");
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


    //LOGICA DI CREAZIONE GRUPPO
    @FXML
    private TextField groupName;
    @FXML
    private TextField groupCategory;
    @FXML
    private TextArea groupDescription;
    @FXML
    private Label errorMessage;
    @FXML
    private Label groupPicPath;
    @FXML
    private void goback(){
        groupName.clear();
        groupCategory.clear();
        groupDescription.clear();
        groupPicPath.setText("");
        createGroup.setVisible(false);
        showHomePanel();
    }
    @FXML
    private void showCreateGroupPanel() {
        reportPanel.setVisible(false);
        homePanel.setVisible(false);
        newPostButton.setVisible(false);
        createGroup.setVisible(true);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
        }
    }

    @FXML
    private void deleteFilePath(){
        groupPicPath.setText("");
    }
    private FileUploadUtility groupPhotoUploader = new FileUploadUtility();
    private String selectedGroupPhotoPath;

    @FXML
    private void selectGroupImage() {
        groupPhotoUploader.chooseProfilePicture();
        selectedGroupPhotoPath = groupPhotoUploader.getSelectedProfilePicturePath();
        groupPicPath.setText(selectedGroupPhotoPath);
    }
    @FXML
    private void createGroup() {
        String groupName = this.groupName.getText();
        String groupCategory = this.groupCategory.getText();
        String groupDescription = this.groupDescription.getText();

        if (groupName.isEmpty() || groupCategory.isEmpty() || groupDescription.isEmpty()){
            errorMessage.setText("I campi nome, categoria e descrizione non possono essere vuoti");
            return;
        }

        String groupPhotoLink = "";
        if (!groupPicPath.getText().isEmpty()){
            groupPhotoLink = groupPhotoUploader.uploadProfilePicture();
        } else {
            // Imposta l'immagine di default se non è stata selezionata alcuna immagine
            groupPhotoLink = getClass().getResource("/Immagini/groupDefaultImage.png").toExternalForm();
        }

        String sql = "INSERT INTO gruppi (nome, nomeCreatore, descrizione, categoria, dataCreazione, fotoGruppo) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, groupName);
            stmt.setString(2, currentUser.getNomeUtente());
            stmt.setString(3, groupDescription);
            stmt.setString(4, groupCategory);
            stmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(6, groupPhotoLink);
            stmt.executeUpdate();

            // Recupera l'ID del gruppo appena creato
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int groupId = generatedKeys.getInt(1);

                String sqlGroupMembers = "INSERT INTO group_members (id_gruppo, nomeUtente) VALUES (?, ?)";
                try (PreparedStatement stmtGroupMembers = connection.prepareStatement(sqlGroupMembers)) {
                    stmtGroupMembers.setInt(1, groupId);
                    stmtGroupMembers.setString(2, currentUser.getNomeUtente());
                    stmtGroupMembers.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.groupName.clear();
        this.groupCategory.clear();
        this.groupDescription.clear();
        groupPicPath.setText("");
        createGroup.setVisible(false);
        leftVBox.getChildren().clear();
        getGroupsFromDatabaseAndDisplay();

        homePanel.setVisible(true);
    }


    //HOME SCREEN
    @FXML
    private VBox homePanel;

    @FXML
    private void showHomePanel() {
        reportPanel.setVisible(false);
        createGroup.setVisible(false);
        newPostButton.setVisible(false);
        homePanel.setVisible(true);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
        }
    }

    @FXML
    private void segrepass(){
        try {
            Desktop.getDesktop().browse(new URI("https://www.segrepass1.unina.it"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void docentiUnina(){
        try {
            Desktop.getDesktop().browse(new URI("https://www.docenti.unina.it"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showReportPanel() {
        homePanel.setVisible(false);
        createGroup.setVisible(false);
        reportPanel.setVisible(true);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
        }
    }


}