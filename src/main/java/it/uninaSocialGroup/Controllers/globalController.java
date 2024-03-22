package it.uninaSocialGroup.Controllers;

import it.uninaSocialGroup.DAO.UserDAO;
import it.uninaSocialGroup.DAO.GroupDAO;
import it.uninaSocialGroup.DAO.GroupMemberDAO;
import it.uninaSocialGroup.DAO.PostDAO;
import it.uninaSocialGroup.DAO.CommentDAO;
import it.uninaSocialGroup.DAO.LikeDAO;
import it.uninaSocialGroup.Oggetti.Group;
import it.uninaSocialGroup.Oggetti.User;
import it.uninaSocialGroup.Oggetti.Post;
import it.uninaSocialGroup.Oggetti.MonthlyReport;
import it.uninaSocialGroup.DAO.ReportDAO;
import it.uninaSocialGroup.Oggetti.Comment;
import it.uninaSocialGroup.Utils.DBUtil;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import  java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class globalController {
    private User currentUser;
    @FXML
    private VBox createPost;
    @FXML
    private VBox createGroup;
    @FXML
    private ChoiceBox<String> monthChoice;

    private GroupDAO groupDAO;
    private GroupMemberDAO groupMemberDAO;

    private PostDAO postDAO;
    private CommentDAO commentDAO;
    private LikeDAO likeDAO;
    private ReportDAO reportDAO;
    private UserDAO UserDAO;
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println(currentUser);
        connectToDatabase();
        UserDAO = new UserDAO(connection);
        groupDAO = new GroupDAO(connection);
        groupMemberDAO = new GroupMemberDAO(connection);
        postDAO = new PostDAO(connection);
        commentDAO = new CommentDAO(connection);
        reportDAO = new ReportDAO(connection);
        likeDAO = new LikeDAO(connection);
        showProfileOrGroupList(currentUser.getProfilePictureLink(), currentUser.getNomeUtente(), usernameAlto, proPic);
        getGroupsFromDatabaseAndDisplay();
        monthChoice.getItems().addAll("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
        createPost.setVisible(false);
        createGroup.setVisible(false);
        reportPanel.setVisible(false);
    }
    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        initializeSlidePanel();


        PauseTransition pause = new PauseTransition(Duration.millis(500));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Ogni volta che il testo cambia riavvia il PauseTransition. questo previene un enorme carico alla cpu (500% nei test)
            pause.setOnFinished(event -> {
                if (newValue.isEmpty()) {
                    getGroupsFromDatabaseAndDisplay();
                } else {
                    updateAvailableGroups(newValue.toLowerCase());
                }
            });
            pause.playFromStart();
        });
        monthChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleMonthSelection(newValue);
            System.out.println(newValue);
        });
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        mostLikedColumn.setCellValueFactory(new PropertyValueFactory<>("mostLikedPostContent"));
        lessLikedColumn.setCellValueFactory(new PropertyValueFactory<>("leastLikedPostContent"));
        mostCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("mostCommentedPostContent"));
        lessCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("leastCommentedPostContent"));
        averagePostNumber.setCellValueFactory(new PropertyValueFactory<>("averagePostsPerGroup"));


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

    private boolean isAGroupActive = false;
    private boolean isVBoxOpen = true;
    private Connection connection;
    private String link = "https://drive.google.com/uc?export=view&id=1DvpLvwgBZaKlSDmQdgCV1fCfIqhnRWU7";
    private Map<String, Image> imageCache = new HashMap<>();

    public void showProfileOrGroupList(String profilePictureLink, String username, Label nameLabel, Rectangle imagePlaceholder) {
        if (nameLabel != null){
            nameLabel.setText(username);
        }

        final String finalProfilePictureLink = profilePictureLink;

        Task<Image> loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                if (imageCache.containsKey(finalProfilePictureLink)) {
                    return imageCache.get(finalProfilePictureLink);
                } else {
                    String link = finalProfilePictureLink;
                    if (link == null || link.equals("")) {
                        link = getClass().getResource("/Immagini/user.png").toExternalForm();
                    }
                    Image image = new Image(link, false);
                    imageCache.put(finalProfilePictureLink, image);
                    return image;
                }
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
            rightVBox.setPrefWidth(35);
        } else {
            rightVBox.setPrefWidth(240);
        }
        isVBoxOpen = !isVBoxOpen;
    }


    private void getGroupsFromDatabaseAndDisplay() {
        leftVBox.getChildren().clear();
        Map<Integer, Group> groups = groupDAO.getGroupsByUser(currentUser.getNomeUtente());

        for (Group group : groups.values()) {
            Rectangle rectangle = new Rectangle();
            rectangle.setId("rectangle" + group.getGroupName());
            Label label = new Label();
            label.setId("label" + group.getGroupName());

            Node groupComponent = createGroupComponent(rectangle, String.valueOf(group.getId()), label, group.getGroupName());
            leftVBox.getChildren().add(groupComponent);
            showProfileOrGroupList(group.getGroupPictureLink(), group.getGroupName(), label, rectangle);
        }
    }
    private void updateAvailableGroups(String searchCriteria) {
        leftVBox.getChildren().clear();
        Map<Integer, Group> groups = groupDAO.searchGroups(searchCriteria);

        for (Group group : groups.values()) {
            Rectangle rectangle = new Rectangle();
            rectangle.setId("rectangle" + group.getGroupName());
            Label label = new Label();
            label.setId("label" + group.getGroupName());

            Node groupComponent = createGroupComponent(rectangle, String.valueOf(group.getId()), label, group.getGroupName());
            leftVBox.getChildren().add(groupComponent);
            showProfileOrGroupList(group.getGroupPictureLink(), group.getGroupName(), label, rectangle);
        }
    }

    private Node createGroupComponent(Rectangle rectangle, String buttonId, Label label, String labelText) {
        rectangle.setArcHeight(40.0);
        rectangle.setArcWidth(70.0);
        rectangle.setHeight(40.0);
        rectangle.setWidth(40.0);
        rectangle.setStyle("-fx-stroke: linear-gradient(from 0% 0% to 100% 100%, rgb(61, 237, 253) 44.2%, rgb(3, 136, 238) 95.6%); -fx-stroke-width: 2px;");

        label.setText(labelText);
        label.setStyle("-fx-background-color: transparent;");
        HBox.setMargin(label, new Insets(0, 0, 0, 10));

        Button button = new Button();
        button.setId(buttonId);
        button.setPrefHeight(50.0);
        button.setPrefWidth(212.0);
        button.setMaxWidth(212.0);

        button.setStyle("-fx-background-radius: 24px; -fx-border-color: rgba(224, 224, 224, 0.2); -fx-border-radius: 26px;");

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

    @FXML
    private VBox centerVBox;
    public int idActiveGroup = 0;

    @FXML
    private StackPane centerStackPane;

    //GRUPPI NELLA SIDEBAR E RICERCA

    private void handleGroupButtonClick(String buttonId) {
        isAGroupActive = true;
        reportPanel.setVisible(false);
        createGroup.setVisible(false);
        homePanel.setVisible(false);
        profileView.setVisible(false);
        idActiveGroup = Integer.parseInt(buttonId);
        loadCentralComponent(buttonId);
        if (isCurrentUserMemberOfGroup(Integer.valueOf(buttonId))) {
            postContent.clear();
            getPostsFromDatabase(idActiveGroup, currentUser.getNomeUtente());
            path.setText("");
            createPost.setVisible(false);
            newPostButton.setVisible(true);
        }else{
            Label label = new Label("Non sei membro di questo gruppo. per partecipare");
            VBox.setMargin(label, new Insets(120, 0, 0, 0));
            mainviewVbox.getChildren().add(label);

            Button button = new Button("Unisciti!");
            VBox.setMargin(button, new Insets(20, 0, 0, 0));
            button.getStyleClass().add("actionButton");
            mainviewVbox.getChildren().add(button);
            button.setOnAction(event -> joinGroup(buttonId));
        }
    }
    @FXML
    private void joinGroup(String buttonId){
        int groupId = Integer.parseInt(buttonId);
        groupMemberDAO.joinGroup(groupId, currentUser.getNomeUtente());
        handleGroupButtonClick(buttonId);
    }



    private boolean isCurrentUserMemberOfGroup(int groupId) {
        return groupMemberDAO.isUserMemberOfGroup(groupId, currentUser.getNomeUtente());
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


    private void getPostsFromDatabase(int groupId, String currentUser) {
        List<Post> posts = postDAO.getPostsByGroupId(groupId, currentUser);
        for (Post post : posts) {
            createAndLoadPostComponent(post);
        }
    }

    @FXML
    private Rectangle authorPic;
    @FXML
    private Label authorName;
    @FXML
    private Label postText;
    @FXML
    private Label postDate;
    @FXML
    private Rectangle postPic;
   @FXML
   private VBox postBox;
    @FXML
    private Label likes;

    //inserisco il nome del contenitore padre, così da impostare facilmente in bottone con l'id del post per non ricevere puntatori a null
    @FXML
    private HBox likesAndComments;


    private Button createLikeButton(int idPost, boolean liked) {
        FontIcon heartIcon = new FontIcon("fas-heart");
        heartIcon.getStyleClass().add("icon");
        heartIcon.setIconSize(23);
        heartIcon.setIconColor(Color.rgb( 224, 224, 224));
        Button likeButton = new Button();
        likeButton.setStyle("-fx-background-color: #131313;");
        likeButton.setGraphic(heartIcon);
        if (liked){
            heartIcon.getStyleClass().add("active");
        }
        likeButton.getStyleClass().add("button-like");
        likeButton.setId(String.valueOf(idPost));
        likeButton.setOnAction(event -> incrementLikeNumber(event));
        return likeButton;
    }
    @FXML
    private VBox commentSection;
    @FXML
    private VBox commentsView;
    private Button createCommentButton(int idPost) {
        FontIcon commentIcon = new FontIcon("fas-comment");
        commentIcon.getStyleClass().add("icon");
        commentIcon.setIconSize(23);
        commentIcon.setIconColor(Color.rgb(224, 224, 224));
        Button commentButton = new Button();
        commentButton.setStyle("-fx-background-color: #131313;");
        commentButton.setGraphic(commentIcon);
        commentButton.getStyleClass().add("button-comment");
        commentButton.setId("comm" + idPost);
        commentButton.setOnAction(event -> handleCommentButton(event));
        return commentButton;
    }
    private int idActivePost;
    @FXML
    private void handleCommentButton(javafx.event.ActionEvent e) {
        commentsView.getChildren().clear();
        commentSection.setVisible(true);
        idActivePost = Integer.parseInt(((Button) e.getSource()).getId().replace("comm", ""));
        String buttonId = ((Button) e.getSource()).getId();
        //faccio in questo modo perchè altrimenti il programma va in errore, siccome esiste già questo id.
        int postId = Integer.parseInt(buttonId.replace("comm", ""));
        getCommentsFromDatabase(postId);
    }

    private void getCommentsFromDatabase(int postId) {
        List<Comment> comments = commentDAO.getCommentsByPostId(postId);
        for (Comment comment : comments) {
            createAndLoadCommentComponent(comment);
        }
    }

    @FXML
    private Rectangle commentsPic;
    @FXML
    private Label commentUsername;
    @FXML
    private Label commentContent;

    private void createAndLoadCommentComponent(Comment comment) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/comment.fxml"));
            fxmlLoader.setController(this);
            Node commentComponent = fxmlLoader.load();
            commentsPic.setArcWidth(commentsPic.getWidth());
            commentsPic.setArcHeight(commentsPic.getHeight());
            commentsPic.setStyle("-fx-stroke: linear-gradient(from 0% 0% to 100% 100%, rgb(61, 237, 253) 44.2%, rgb(3, 136, 238) 95.6%); -fx-stroke-width: 2px;");
            commentContent.setText(comment.getContent());
            showProfileOrGroupList(comment.getUserProfilePicture(), comment.getUsername(), commentUsername, commentsPic);

            commentUsername.setOnMouseClicked(event -> showprofile(comment.getUsername()));

            commentsView.getChildren().add(commentComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Separator separator = new Separator();
        commentsView.getChildren().add(separator);
    }

    @FXML
    private void closeCommentSection() {
        commentSection.setVisible(false);
        newPostButton.setVisible(true);
        commentArea.clear();
    }
    @FXML
    private TextArea commentArea;

    @FXML
    private void postComment() {
        String commentText = commentArea.getText();
        if (commentText.isEmpty()){
            return;
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Comment newComment = new Comment(
                idActivePost,
                currentUser.getNomeUtente(),
                commentText,
                timestamp,
                currentUser.getProfilePictureLink()
        );
        commentDAO.addComment(newComment);
        commentsView.getChildren().clear();
        getCommentsFromDatabase(idActivePost);
        commentArea.clear();
    }

    @FXML
    private void createAndLoadPostComponent(Post post) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/post.fxml"));
            fxmlLoader.setController(this);

            Node postComponent = fxmlLoader.load();
            mainviewVbox.getChildren().add(0, postComponent);

            showProfileOrGroupList(post.getUserProfilePicture(), post.getAuthorUsername(), authorName, authorPic);
            authorName.setOnMouseClicked(event -> showprofile(post.getAuthorUsername()));

            if (post.getPostPicture() == null || post.getPostPicture().equals("")) {
                postBox.getChildren().remove(postPic);
            } else {
                showProfileOrGroupList(post.getPostPicture(), null, null, postPic);
            }
            Button likeButton = createLikeButton(post.getId(), post.isLiked());
            Button commentButton = createCommentButton(post.getId());
            likesAndComments.getChildren().add(0, likeButton);
            likesAndComments.getChildren().add(2, commentButton);
            likes.setText(String.valueOf(post.getLikeNumber()));
            likes.setId("likes" + post.getId());
            postText.setText(post.getContent());
            Separator separator = new Separator();
            mainviewVbox.getChildren().add(separator);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    //trova la label corrispondente, prende la classe del pulsante like e se è attiva decrementa, altrimenti incrementa. dopodiché aggiorna il db con il dao
    private void incrementLikeNumber(javafx.event.ActionEvent e) {
        Button likeButton = (Button) e.getSource();
        int postId = Integer.parseInt(likeButton.getId());

        Label likesLabel = (Label) centerVBox.lookup("#likes" + postId);
        FontIcon icon = (FontIcon) likeButton.getGraphic();
        boolean isLiked = !icon.getStyleClass().contains("active");
        int currentLikes = Integer.parseInt(likesLabel.getText());
        likesLabel.setText(String.valueOf(currentLikes + (isLiked ? 1 : -1)));

        if (isLiked) {
            icon.getStyleClass().add("active");
        } else {
            icon.getStyleClass().remove("active");
        }
        likeDAO.updateLike(postId, currentUser.getNomeUtente(), isLiked);
    }

    private Node groupComponent;
    @FXML
    private Label membersNumber;

    private void loadCentralComponent(String buttonId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/centralGrouPane.fxml"));
            fxmlLoader.setController(this);

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
            groupComponent.setId(buttonId);
            membersNumber.setText("partecipanti: " + String.valueOf(groupMemberDAO.getGroupMembers(Integer.parseInt(buttonId)).size()));
            groupComponent.setOnMouseClicked(event -> showGroupInfo());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private Rectangle ActiveGroupPic;
    @FXML
    private Label ActiveGroupName;

    private Group activeGroup;
    private void getGroupPicAndName(int id) {
        activeGroup = groupDAO.getGroupDetailsById(id);
        if (activeGroup != null) {
            showProfileOrGroupList(activeGroup.getGroupPictureLink(), activeGroup.getGroupName(), ActiveGroupName, ActiveGroupPic);
        }
    }

    @FXML
    private Label groupNameLabel;
    @FXML
    private Rectangle infoPic;
    @FXML
    private Label groupCategoryLabel;
    @FXML
    private Label groupDescriptionLabel;
    @FXML
    private Button esc;
    @FXML
    private VBox groupInfo;
    @FXML
    private void showGroupInfo() {
        mainviewVbox.getChildren().clear();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/groupInfo.fxml"));
            fxmlLoader.setController(this);
            Node groupInfoComponent = fxmlLoader.load();
            mainviewVbox.getChildren().clear();
            mainviewVbox.getChildren().add(groupInfoComponent);
            showProfileOrGroupList(activeGroup.getGroupPictureLink(), activeGroup.getGroupName(), groupNameLabel, infoPic);
            groupDescriptionLabel.setText(activeGroup.getDescription());
            groupCategoryLabel.setText("#"+activeGroup.getCategory());
            esc.setOnAction(event -> deleteGroupInfo());
            List<String> members = groupMemberDAO.getGroupMembers(activeGroup.getId());
            for (String member : members) {
                Label label = new Label(member);
                label.setPrefWidth(406);
                groupInfo.getChildren().add(label);
                label.setOnMouseClicked(event -> showprofile(label.getText()));
                Separator separator = new Separator();
                separator.setPrefWidth(300);
                groupInfo.getChildren().add(separator);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void deleteGroupInfo() {
        mainviewVbox.getChildren().clear();
        getPostsFromDatabase(idActiveGroup, currentUser.getNomeUtente());
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

    @FXML
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
        Post newPost = new Post(0, currentUser.getNomeUtente(), postText, postPictureLink, currentUser.getProfilePictureLink(), timestamp, groupId, 0, false);
        postDAO.addPost(newPost);
        mainviewVbox.getChildren().clear();
        getPostsFromDatabase(groupId, currentUser.getNomeUtente());
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
        profileView.setVisible(false);
        createGroup.setVisible(true);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
            mainviewVbox.getChildren().clear();
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
        String groupNameText = groupName.getText();
        String groupCategoryText = groupCategory.getText();
        String groupDescriptionText = groupDescription.getText();
        try {
            if (groupDAO.checkIfNameIsAlreadyUsed(groupNameText)) {
                errorMessage.setText("Nome del gruppo già utilizzato");
                return;
            }
        } catch (Exception e) {
            errorMessage.setText("Errore durante il controllo del nome. Riprova.");

        }
        if (groupNameText.isEmpty() || groupCategoryText.isEmpty() || groupDescriptionText.isEmpty()) {
            errorMessage.setText("I campi nome, categoria e descrizione non possono essere vuoti");
            return;
        }
        int groupId = 0;
        String groupPhotoLink = "";
        if (!groupPicPath.getText().isEmpty()){
            groupPhotoLink = groupPhotoUploader.uploadProfilePicture();
        } else {
            // Imposta l'immagine di default se non è stata selezionata alcuna immagine
            groupPhotoLink = getClass().getResource("/Immagini/groupDefaultImage.png").toExternalForm();
        }
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());

        Group newGroup = new Group(groupNameText, groupDescriptionText, groupCategoryText, groupId, currentUser.getNomeUtente(), creationDate, groupPhotoLink);

        try {
            groupDAO.createGroup(newGroup);
            clearGroupForm();
            leftVBox.getChildren().clear();
            getGroupsFromDatabaseAndDisplay();
            homePanel.setVisible(true);
        } catch (Exception e) {
            errorMessage.setText("Errore durante la creazione del gruppo. Per favore, riprova.");
            e.printStackTrace();

        }
    }

    private void clearGroupForm() {
        groupName.clear();
        groupCategory.clear();
        groupDescription.clear();
        groupPicPath.setText("");
        createGroup.setVisible(false);
    }


    //HOME SCREEN
    @FXML
    private VBox homePanel;

    @FXML
    private void showHomePanel() {
        reportPanel.setVisible(false);
        createGroup.setVisible(false);
        newPostButton.setVisible(false);
        profileView.setVisible(false);
        homePanel.setVisible(true);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
            mainviewVbox.getChildren().clear();
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
        profileView.setVisible(false);
        if (centerVBox.getChildren().get(0) instanceof Button) {
            centerVBox.getChildren().remove(0);
            mainviewVbox.getChildren().clear();
        }
    }

    @FXML
    private TableView<MonthlyReport> reportTable;
    @FXML
    private TableColumn<MonthlyReport, String> groupNameColumn;
    @FXML
    private TableColumn<MonthlyReport, String> mostLikedColumn;
    @FXML
    private TableColumn<MonthlyReport, String> lessLikedColumn;
    @FXML
    private TableColumn<MonthlyReport, String> mostCommentsColumn;
    @FXML
    private TableColumn<MonthlyReport, String> lessCommentsColumn;
    @FXML
    private TableColumn<MonthlyReport, Double> averagePostNumber;
    @FXML
    public void handleMonthSelection(String month) {
        String admin = currentUser.getNomeUtente();
        String monthNumber = monthToNumber(month);

        // Ottieni i dati per ogni categoria dalle query
        List<String> groupNames = reportDAO.getAdminGroups(admin, monthNumber);
        ObservableList<MonthlyReport> reportItems = FXCollections.observableArrayList();
        for (String groupName : groupNames) {
            MonthlyReport report = new MonthlyReport();
            report.setGroupName(groupName);
            report.setMostLikedPostContent(reportDAO.getMostLikedPost(groupName, monthNumber));
            report.setLeastLikedPostContent(reportDAO.getLeastLikedPost(groupName, monthNumber));
            report.setMostCommentedPostContent(reportDAO.getMostCommentedPost(groupName, monthNumber));
            report.setLeastCommentedPostContent(reportDAO.getLeastCommentedPost(groupName, monthNumber));
            String averagePostsPerGroup = String.valueOf(reportDAO.getAveragePostsPerGroup(groupName, monthNumber));
            report.setAveragePostsPerGroup(averagePostsPerGroup);
            reportItems.add(report);
        }

        reportTable.setItems(reportItems);
    }


    public String monthToNumber(String month) {
        String monthNumber;
        switch (month.toLowerCase()) {
            case "gennaio":
                monthNumber = "2024-01";
                break;
            case "febbraio":
                monthNumber = "2024-02";
                break;
            case "marzo":
                monthNumber = "2024-03";
                break;
            case "aprile":
                monthNumber = "2024-04";
                break;
            case "maggio":
                monthNumber = "2024-05";
                break;
            case "giugno":
                monthNumber = "2024-06";
                break;
            case "luglio":
                monthNumber = "2024-07";
                break;
            case "agosto":
                monthNumber = "2024-08";
                break;
            case "settembre":
                monthNumber = "2024-09";
                break;
            case "ottobre":
                monthNumber = "2024-10";
                break;
            case "novembre":
                monthNumber = "2024-11";
                break;
            case "dicembre":
                monthNumber = "2024-12";
                break;
            default:
                monthNumber = "00";
        }
        return monthNumber;
    }

    @FXML
    private VBox profileView;
    @FXML
    private Label profileName;
    @FXML
    private Label profileSurname;
    @FXML
    private Label profileEmail;
    @FXML
    private Label profileMatricola;
    @FXML
    private Label profileBirthDate;
    @FXML
    private Label profileUsername;
    @FXML
    private Rectangle profilePropic;

    @FXML
    private void showprofile(String name){
        User user = UserDAO.getUserByUsername(name);

        profileView.setVisible(true);
        profileName.setText(user.getNome());
        profileSurname.setText(user.getCognome());
        profileEmail.setText(user.getEmail());
        profileMatricola.setText(user.getMatricola());
        profileBirthDate.setText(String.valueOf(user.getDataDiNascita()));
        if (centerVBox.getChildren().get(0) instanceof Button){
            VBox.setMargin(profileView, new Insets(0, 0, 80, 0));
        }
        showProfileOrGroupList(user.getProfilePictureLink(), user.getNomeUtente(), profileUsername, profilePropic);

    }
    @FXML
    private void closeProfile(){
        profileView.setVisible(false);
    }


    public void showprofile(ActionEvent actionEvent) {
        showprofile(currentUser.getNomeUtente());
    }
}