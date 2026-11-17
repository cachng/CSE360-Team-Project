package hw2;

import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Title: HW2Main Class.
 * 
 * Description: The main JavaFX GUI application where students can post,
 * search, edit, and delete lessons learned with live input validation.
 * 
 * @author Samuel Joseph
 * @version 1.00 2026-10-05
 */
public class HW2Main extends Application {

    // Input fields for creating a post
    private TextField textTitle = new TextField();
    private TextArea textContent = new TextArea();
    private TextField textAuthor = new TextField();
    private Label labelError = new Label("");

    // Search bar widgets
    private TextField textSearch = new TextField();
    private Label labelSearchStatus = new Label("");
    private VBox feedBox = new VBox(10);

    @Override
    public void start(Stage primaryStage) {
        // Initialize our H2 database
        LessonDatabase.initialize();

        primaryStage.setTitle("CSE 360 HW2: Lessons Learned Forum — Samuel Joseph");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #f7fafc;");

        // Top Header
        VBox topHeader = new VBox(4);
        topHeader.setPadding(new Insets(0, 0, 10, 0));
        Label title = new Label("CSE 360 Lessons Learned Forum");
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1a365d"));
        Label subtitle = new Label("Persistent CRUD & Input Validation | Author: Samuel Joseph");
        subtitle.setFont(Font.font("Helvetica", 12));
        subtitle.setTextFill(Color.web("#4a5568"));
        topHeader.getChildren().addAll(title, subtitle);
        root.setTop(topHeader);

        // Main content: Left = Post Form, Right = Search & Feed
        HBox mainContent = new HBox(16);
        mainContent.setPadding(new Insets(10, 0, 0, 0));

        // Left Composer Card
        VBox composerCard = buildComposerCard();

        // Right Feed Card
        VBox feedCard = buildFeedCard();
        HBox.setHgrow(feedCard, Priority.ALWAYS);

        mainContent.getChildren().addAll(composerCard, feedCard);
        root.setCenter(mainContent);

        // Load all existing lessons on startup
        refreshFeed(LessonDatabase.getAllLessons());

        Scene scene = new Scene(root, 1020, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Builds the form where students create a new lesson
    private VBox buildComposerCard() {
        VBox card = new VBox(8);
        card.setMinWidth(360);
        card.setMaxWidth(360);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e0; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label heading = new Label("Share a Lesson Learned (Create)");
        heading.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
        heading.setTextFill(Color.web("#1a365d"));

        textTitle.setPromptText("Title (5-100 chars)");
        textContent.setPromptText("What did you learn? Describe the fix...");
        textContent.setPrefRowCount(6);
        textContent.setWrapText(true);
        textAuthor.setPromptText("Your Username (3-50 chars)");

        Button btnPost = new Button("Post Lesson");
        btnPost.setFont(Font.font("Helvetica", FontWeight.BOLD, 13));
        btnPost.setStyle("-fx-background-color: #2b6cb0; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
        btnPost.setOnAction(e -> handlePostSubmission());

        labelError.setFont(Font.font("Helvetica", 11));
        labelError.setTextFill(Color.web("#c53030"));
        labelError.setWrapText(true);

        card.getChildren().addAll(
            heading,
            new Label("Title:"), textTitle,
            new Label("Explanation / Content:"), textContent,
            new Label("Author:"), textAuthor,
            btnPost, labelError
        );
        return card;
    }

    // Handles clicking the "Post Lesson" button
    private void handlePostSubmission() {
        String t = textTitle.getText();
        String c = textContent.getText();
        String a = textAuthor.getText();

        // Step 1: Validate inputs first
        String validationError = LessonValidator.validateAll(t, c, a);
        if (!validationError.isEmpty()) {
            labelError.setTextFill(Color.web("#c53030"));
            labelError.setText(validationError);
            return;
        }

        // Step 2: Save to database
        int newId = LessonDatabase.createLesson(t, c, a);
        if (newId > 0) {
            labelError.setTextFill(Color.web("#22543d"));
            labelError.setText("Lesson successfully posted!");
            textTitle.clear();
            textContent.clear();
            refreshFeed(LessonDatabase.getAllLessons());
        } else {
            labelError.setTextFill(Color.web("#c53030"));
            labelError.setText("Error: Failed to save to database.");
        }
    }

    // Builds the feed area on the right with the search bar
    private VBox buildFeedCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e0; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Search Bar
        HBox searchRow = new HBox(8);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        textSearch.setPromptText("Search lessons by keyword (e.g. JavaFX)...");
        HBox.setHgrow(textSearch, Priority.ALWAYS);

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #1a365d; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        btnSearch.setOnAction(e -> handleSearchAction());

        Button btnClear = new Button("Clear");
        btnClear.setOnAction(e -> {
            textSearch.clear();
            labelSearchStatus.setText("");
            refreshFeed(LessonDatabase.getAllLessons());
        });

        searchRow.getChildren().addAll(textSearch, btnSearch, btnClear);

        labelSearchStatus.setFont(Font.font("Helvetica", 11));
        labelSearchStatus.setTextFill(Color.web("#4a5568"));

        // Scrollable list container
        ScrollPane scrollPane = new ScrollPane(feedBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        card.getChildren().addAll(searchRow, labelSearchStatus, scrollPane);
        return card;
    }

    // Handles searching
    private void handleSearchAction() {
        String query = textSearch.getText().trim();
        String err = LessonValidator.validateSearchQuery(query);
        if (!err.isEmpty()) {
            labelSearchStatus.setText(err);
            return;
        }

        List<LessonLearned> results = LessonDatabase.searchLessons(query);
        if (results.isEmpty()) {
            labelSearchStatus.setText("No lessons found matching '" + query + "'.");
        } else {
            labelSearchStatus.setText("Showing " + results.size() + " matching lesson(s) for '" + query + "'.");
        }
        refreshFeed(results);
    }

    // Refreshes the cards on screen
    private void refreshFeed(List<LessonLearned> list) {
        feedBox.getChildren().clear();
        if (list.isEmpty()) {
            Label empty = new Label("No lessons to show.");
            empty.setFont(Font.font("Helvetica", 13));
            empty.setTextFill(Color.web("#a0aec0"));
            feedBox.getChildren().add(empty);
            return;
        }
        for (LessonLearned lesson : list) {
            feedBox.getChildren().add(buildLessonCard(lesson));
        }
    }

    // Renders one lesson post card with Edit and Delete buttons
    private VBox buildLessonCard(LessonLearned lesson) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setStyle("-fx-background-color: #f7fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-radius: 6;");

        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("#" + lesson.getId() + " " + lesson.getTitle());
        title.setFont(Font.font("Helvetica", FontWeight.BOLD, 13.5));
        title.setTextFill(Color.web("#1a365d"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit button
        Button btnEdit = new Button("Edit");
        btnEdit.setFont(Font.font("Helvetica", 11));
        btnEdit.setOnAction(e -> openEditDialog(lesson));

        // Delete button
        Button btnDelete = new Button("Delete");
        btnDelete.setFont(Font.font("Helvetica", 11));
        btnDelete.setStyle("-fx-text-fill: #9b2c2c;");
        btnDelete.setOnAction(e -> handleDeleteAction(lesson));

        topRow.getChildren().addAll(title, spacer, btnEdit, btnDelete);

        Label meta = new Label("Posted by @" + lesson.getAuthor() + " on " + lesson.getFormattedDate());
        meta.setFont(Font.font("Helvetica", 10.5));
        meta.setTextFill(Color.web("#718096"));

        Label content = new Label(lesson.getContent());
        content.setFont(Font.font("Helvetica", 12));
        content.setTextFill(Color.web("#2d3748"));
        content.setWrapText(true);

        card.getChildren().addAll(topRow, meta, content);
        return card;
    }

    // Opens a popup dialog to edit a lesson (Update - U)
    private void openEditDialog(LessonLearned lesson) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Lesson #" + lesson.getId());
        dialog.setHeaderText("Update Title and Content");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));

        TextField editTitle = new TextField(lesson.getTitle());
        TextArea editContent = new TextArea(lesson.getContent());
        editContent.setPrefRowCount(4);
        editContent.setWrapText(true);
        Label errLabel = new Label("");
        errLabel.setTextFill(Color.web("#c53030"));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(editTitle, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(editContent, 1, 1);
        grid.add(errLabel, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String newT = editTitle.getText();
            String newC = editContent.getText();

            String tErr = LessonValidator.validateTitle(newT);
            if (!tErr.isEmpty()) {
                errLabel.setText(tErr);
                event.consume();
                return;
            }

            String cErr = LessonValidator.validateContent(newC);
            if (!cErr.isEmpty()) {
                errLabel.setText(cErr);
                event.consume();
                return;
            }

            LessonDatabase.updateLesson(lesson.getId(), newT, newC);
        });

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            refreshFeed(LessonDatabase.getAllLessons());
        }
    }

    // Confirmation alert before deleting a lesson (Delete - D)
    private void handleDeleteAction(LessonLearned lesson) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Lesson #" + lesson.getId() + "?");
        alert.setContentText("Are you sure you want to delete this lesson? This cannot be undone.");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            LessonDatabase.deleteLesson(lesson.getId());
            refreshFeed(LessonDatabase.getAllLessons());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}