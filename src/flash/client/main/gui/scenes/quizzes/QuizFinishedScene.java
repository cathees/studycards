package flash.client.main.gui.scenes.quizzes;

import flash.client.main.ClientSession;
import flash.client.main.ServerGateway;
import flash.client.main.gui.AlertUtil;
import flash.client.main.gui.SceneRouter;
import flash.common.dto.QuizQuestionResultDTO;
import flash.common.dto.QuizResultDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.concurrent.CompletableFuture;

public class QuizFinishedScene extends BorderPane {

    private final String quizSessionId;

    public QuizFinishedScene(String quizSessionId) {
        this.quizSessionId = quizSessionId;

        setPadding(new Insets(40));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #faf8ff, #ffffff);");

        setCenter(new Label("Lädt Auswertung..."));
        loadResultFromServer();
    }

    private void loadResultFromServer() {
        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return ServerGateway.quiz().getResult(ClientSession.sessionId(), quizSessionId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }, ServerGateway.EXECUTOR)
                .thenAccept(result -> Platform.runLater(() -> render(result)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        AlertUtil.error("Fehler", cause.getMessage() != null ? cause.getMessage() : cause.toString());
                        SceneRouter.setRoot(new QuizzesScene());
                    });
                    return null;
                });
    }

    private void render(QuizResultDTO result) {
        int correct = result.correctCount();
        int total = result.totalCount();
        double percent = total == 0 ? 0 : (double) correct / total * 100;

        // HEADER SUCCESS BANNER
        Label title = new Label(percent >= 50 ? "Glückwunsch!" : "Vielleicht beim nächsten Mal?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        Label desc = new Label("Sie haben " + correct + " von " + total + " Fragen richtig beantwortet");
        desc.setFont(Font.font(15));
        desc.setTextFill(Color.WHITE);

        Label percentLabel = new Label(String.format("%.0f%%", percent));
        percentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        percentLabel.setTextFill(Color.WHITE);

        Label praise = new Label(percent >= 50 ? "Sehr gute Leistung!" : "Vielleicht noch einmal versuchen?");
        praise.setFont(Font.font(15));
        praise.setTextFill(Color.WHITE);

        VBox bannerContent = new VBox(8, title, desc, percentLabel, praise);
        bannerContent.setAlignment(Pos.CENTER);

        VBox banner = new VBox(bannerContent);
        banner.setAlignment(Pos.CENTER);
        banner.setPadding(new Insets(40));
        banner.setStyle(percent >= 50 ? """
            -fx-background-color: #22c55e;
            -fx-background-radius: 16;
        """ : """
            -fx-background-color: #fa683f;
            -fx-background-radius: 16;
        """);

        // DETAIL LIST
        VBox detailBox = new VBox(12);
        detailBox.setPadding(new Insets(20));
        detailBox.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 16;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.07), 12, 0, 0, 4);
        """);

        Label detailHeader = new Label("Detaillierte Auswertung");
        detailHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        detailHeader.setTextFill(Color.web("#444"));
        detailBox.getChildren().add(detailHeader);

        for (QuizQuestionResultDTO q : result.details()) {
            boolean isCorrect = q.correct();

            Label icon = new Label(isCorrect ? "✔" : "✘");
            icon.setTextFill(isCorrect ? Color.web("#16a34a") : Color.web("#dc2626"));
            icon.setFont(Font.font(18));

            Label questionText = new Label("Frage " + q.index() + ": " + q.questionText());
            questionText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            questionText.setWrapText(true);

            String given = (q.givenAnswer() == null || q.givenAnswer().isBlank()) ? "(keine Antwort)" : q.givenAnswer();

            Label userAnswer = new Label("Ihre Antwort: " + given);
            userAnswer.setFont(Font.font("Arial", 13));
            userAnswer.setWrapText(true);

            Label correctInfo = new Label("Richtige Antwort: " + q.correctAnswer());
            correctInfo.setFont(Font.font("Arial", 12));
            correctInfo.setTextFill(Color.web("#6b7280"));
            correctInfo.setWrapText(true);

            VBox qaBox = new VBox(4, questionText, userAnswer, correctInfo);
            qaBox.setPadding(new Insets(10));
            qaBox.setStyle(isCorrect
                    ? """
                       -fx-background-color: #f1fdf4;
                       -fx-background-radius: 8;
                       -fx-border-color: #bbf7d0;
                       -fx-border-radius: 8;
                       """
                    : """
                       -fx-background-color: #fff1f2;
                       -fx-background-radius: 8;
                       -fx-border-color: #fecaca;
                       -fx-border-radius: 8;
                       """
            );

            HBox entry = new HBox(12, icon, qaBox);
            entry.setAlignment(Pos.TOP_LEFT);

            detailBox.getChildren().add(entry);
        }

        // BUTTON
        Button restart = new Button("Neues Quiz starten");
        restart.setPrefWidth(400);
        restart.setStyle("""
            -fx-background-color: #7a2cff;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
        """);
        restart.setOnAction(e -> SceneRouter.setRoot(new QuizzesScene()));

        VBox bottom = new VBox(restart);
        bottom.setAlignment(Pos.CENTER);

        VBox content = new VBox(30, banner, detailBox, bottom);
        content.setAlignment(Pos.TOP_CENTER);

        setCenter(content);
    }
}
