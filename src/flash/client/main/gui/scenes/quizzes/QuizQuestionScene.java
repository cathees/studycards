package flash.client.main.gui.scenes.quizzes;

import flash.client.main.ClientSession;
import flash.client.main.ServerGateway;
import flash.client.main.gui.AlertUtil;
import flash.client.main.gui.SceneRouter;
import flash.common.dto.AnswerResultDTO;
import flash.common.dto.QuizQuestionDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.concurrent.CompletableFuture;

public class QuizQuestionScene extends BorderPane {

    private final String quizSessionId;

    private ProgressBar progressBar;
    private Label subtitle;
    private Label questionLabel;
    private TextField answerField;
    private Label badge;
    private Button confirmButton;

    private QuizQuestionDTO current;

    public QuizQuestionScene(String quizSessionId) {
        this.quizSessionId = quizSessionId;

        getStylesheets().add(
                getClass().getResource("../../../css/progress.css").toExternalForm()
        );

        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #faf8ff, #ffffff);");

        setCenter(buildLayout());

        loadCurrentQuestion();
    }

    private Parent buildLayout() {
        VBox layout = new VBox(30, buildTopBar(), buildQuestionCard());
        layout.setPadding(new Insets(40, 0, 0, 0));
        layout.setAlignment(Pos.TOP_CENTER);
        return layout;
    }

    private Parent buildTopBar() {
        Label title = new Label("Quiz");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#7a2cff"));

        subtitle = new Label("Lädt...");
        subtitle.setFont(Font.font(13));
        subtitle.setTextFill(Color.web("#666"));

        VBox info = new VBox(2, title, subtitle);

        Button backBtn = new Button("← Zurück");
        backBtn.setFont(Font.font("Arial", 13));
        backBtn.setTextFill(Color.web("#7a2cff"));
        backBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-font-weight: bold;
        """);
        backBtn.setOnAction(e -> SceneRouter.setRoot(new QuizzesScene()));

        HBox bar = new HBox(info, new Region(), backBtn);
        HBox.setHgrow(bar.getChildren().get(1), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);

        progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(10);
        progressBar.setPrefWidth(Double.MAX_VALUE);

        VBox top = new VBox(bar, progressBar);
        VBox.setMargin(progressBar, new Insets(6, 0, 0, 0));
        return top;
    }

    private Parent buildQuestionCard() {
        badge = new Label();
        badge.setStyle("""
            -fx-background-color: #ede5ff;
            -fx-text-fill: #7a2cff;
            -fx-background-radius: 12;
            -fx-padding: 4 12;
        """);

        questionLabel = new Label("...");
        questionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(400);

        answerField = new TextField();
        answerField.setPromptText("Ihre Antwort eingeben ...");
        answerField.setPrefWidth(400);
        answerField.setFont(Font.font("Arial", 13));
        answerField.setStyle("""
            -fx-background-radius: 8;
            -fx-border-color: #d0d0d0;
            -fx-border-radius: 8;
            -fx-padding: 8;
        """);

        confirmButton = new Button("Frage beantworten");
        confirmButton.setPrefWidth(400);
        confirmButton.setStyle("""
            -fx-background-color: #7a2cff;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
        """);
        confirmButton.setOnAction(e -> handleAnswer());

        VBox card = new VBox(20, badge, questionLabel, answerField, confirmButton);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(30));
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 16;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 12, 0, 0, 4);
        """);

        HBox wrapper = new HBox(card);
        wrapper.setAlignment(Pos.TOP_CENTER);
        return wrapper;
    }

    private void loadCurrentQuestion() {
        confirmButton.setDisable(true);
        subtitle.setText("Lädt...");

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return ServerGateway.quiz().getCurrentQuestion(ClientSession.sessionId(), quizSessionId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }, ServerGateway.EXECUTOR)
                .thenAccept(q -> Platform.runLater(() -> {
                    current = q;
                    updateUI();
                    confirmButton.setDisable(false);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        AlertUtil.error("Fehler", cause.getMessage() != null ? cause.getMessage() : cause.toString());
                        SceneRouter.setRoot(new QuizzesScene());
                    });
                    return null;
                });
    }

    private void updateUI() {
        int index = current.index();
        int total = current.total();

        subtitle.setText("Frage " + index + " von " + total);
        badge.setText("Frage " + index);
        questionLabel.setText(current.questionText());

        double progress = (double) (index - 1) / total;
        progressBar.setProgress(progress);

        answerField.clear();
    }

    private void handleAnswer() {
        String userAnswer = answerField.getText().trim();
        if (userAnswer.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Bitte geben Sie eine Antwort ein.").showAndWait();
            return;
        }

        confirmButton.setDisable(true);

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return ServerGateway.quiz().submitAnswer(ClientSession.sessionId(), quizSessionId, userAnswer);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }, ServerGateway.EXECUTOR)
                .thenAccept((AnswerResultDTO res) -> Platform.runLater(() -> {
                    confirmButton.setDisable(false);

                    if (!res.finished()) {
                        loadCurrentQuestion();
                    } else {
                        SceneRouter.setRoot(new QuizFinishedScene(quizSessionId));
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        confirmButton.setDisable(false);
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        AlertUtil.error("Fehler", cause.getMessage() != null ? cause.getMessage() : cause.toString());
                    });
                    return null;
                });
    }
}
