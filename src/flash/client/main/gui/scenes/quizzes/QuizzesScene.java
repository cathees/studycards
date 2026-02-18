package flash.client.main.gui.scenes.quizzes;

import flash.client.main.ClientSession;
import flash.client.main.ServerGateway;
import flash.client.main.gui.AlertUtil;
import flash.client.main.gui.SceneRouter;
import flash.client.main.gui.scenes.welcome.WelcomeScene;
import flash.common.dto.QuizInfoDTO;
import flash.common.dto.QuizStartDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QuizzesScene extends BorderPane {

    private final FlowPane grid = new FlowPane();
    private List<QuizInfoDTO> quizzes = new ArrayList<>();

    public QuizzesScene() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #f8faff, #ffffff);");
        setPadding(new Insets(20));

        VBox layout = new VBox(25,
                topBar(),
                banner(),
                quizzesGrid(),
                tipsBox()
        );
        layout.setPadding(new Insets(20, 40, 40, 40));
        setCenter(layout);
    }

    // TOP BAR
    private Parent topBar() {
        Label title = new Label("Quiz-Client");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label welcome = new Label("Willkommen, " + ClientSession.name());
        welcome.setTextFill(Color.GRAY);

        VBox titleBox = new VBox(2, title, welcome);

        Hyperlink logout = new Hyperlink("Abmelden");
        logout.setStyle("-fx-text-fill: #1a56ff; -fx-font-weight: bold;");
        logout.setOnAction(_ -> SceneRouter.setRoot(new WelcomeScene()));

        HBox bar = new HBox(titleBox, new Region(), logout);
        HBox.setHgrow(bar.getChildren().get(1), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // PURPLE BANNER (dynamic numbers)
    private Parent banner() {
        Label heading = new Label("Bereit zum Lernen?");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setTextFill(Color.WHITE);

        Label text = new Label("Wählen Sie eines der verfügbaren Quiz aus und testen Sie Ihr Wissen. Sie erhalten sofortiges Feedback zu Ihren Antworten.");
        text.setTextFill(Color.web("#eee"));
        text.setWrapText(true);

        VBox textBox = new VBox(8, heading, text);

        Label totalQuiz = new Label("…");
        totalQuiz.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        totalQuiz.setTextFill(Color.WHITE);
        Label lblQuiz = new Label("Verfügbare Quiz");
        lblQuiz.setTextFill(Color.web("#ddd"));
        VBox col1 = new VBox(lblQuiz, totalQuiz);
        col1.setAlignment(Pos.CENTER_LEFT);

        Label totalQuestions = new Label("…");
        totalQuestions.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        totalQuestions.setTextFill(Color.WHITE);
        Label lblQuestions = new Label("Gesamt Fragen");
        lblQuestions.setTextFill(Color.web("#ddd"));
        VBox col2 = new VBox(lblQuestions, totalQuestions);
        col2.setAlignment(Pos.CENTER_LEFT);

        // update these dynamically after load
        loadQuizzesFromServer(totalQuiz, totalQuestions);

        HBox stats = new HBox(60, col1, col2);
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(20, textBox, stats);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #7a2cff; -fx-background-radius: 16;");
        return box;
    }

    // QUIZ CARDS
    private Parent quizzesGrid() {
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(0));
        grid.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(new Insets(0));
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        scrollPane.viewportBoundsProperty().addListener((_, _, newBounds) ->
                grid.setPrefWidth(newBounds.getWidth())
        );

        grid.getChildren().setAll(new Label("Quiz werden geladen..."));
        return scrollPane;
    }

    private void loadQuizzesFromServer(Label totalQuiz, Label totalQuestions) {
        String sessionId = ClientSession.sessionId();

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return ServerGateway.quiz().listQuizzes(sessionId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }, ServerGateway.EXECUTOR)
                .thenAccept(list -> Platform.runLater(() -> {
                    quizzes = new ArrayList<>(list);
                    renderQuizzes();

                    totalQuiz.setText(String.valueOf(quizzes.size()));
                    int qCount = quizzes.stream().mapToInt(QuizInfoDTO::questions).sum();
                    totalQuestions.setText(String.valueOf(qCount));
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        AlertUtil.error("Fehler", cause.getMessage() != null ? cause.getMessage() : cause.toString());
                        grid.getChildren().setAll(new Label("Quiz konnten nicht geladen werden."));
                        totalQuiz.setText("0");
                        totalQuestions.setText("0");
                    });
                    return null;
                });
    }

    private void renderQuizzes() {
        grid.getChildren().clear();

        if (quizzes.isEmpty()) {
            grid.getChildren().add(new Label("Keine Quiz verfügbar."));
            return;
        }

        for (QuizInfoDTO quiz : quizzes) {
            grid.getChildren().add(quizCard(quiz));
        }
    }

    private VBox quizCard(QuizInfoDTO q) {
        // Kategorie-Label
        Label category = new Label(q.category());
        category.setStyle("-fx-background-color: #e8edff; -fx-text-fill: #1a56ff; -fx-background-radius: 10; -fx-padding: 2 10 2 10;");

        // Schwierigkeits-Label
        Label level = new Label(q.level());
        String levelColor = switch (q.level()) {
            case "Einfach" -> "#22c55e";
            case "Mittel" -> "#facc15";
            case "Schwer" -> "#f87171";
            default -> "#d1d5db";
        };
        level.setStyle("-fx-background-color:" + levelColor + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 10 2 10;");

        HBox header = new HBox(category, new Region(), level);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        Label title = new Label(q.title());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Label desc = new Label(q.description());
        desc.setTextFill(Color.GRAY);
        desc.setWrapText(true);

        Label meta = new Label(q.questions() + " Fragen   ·   " + q.duration());
        meta.setTextFill(Color.web("#666"));

        Button start = new Button("Quiz starten");
        start.setPrefWidth(Double.MAX_VALUE);
        start.setStyle("-fx-background-color:" + q.color() + "; -fx-text-fill: white; -fx-font-weight: bold;");

        start.setOnAction(_ -> {
            start.setDisable(true);
            start.setText("Startet...");

            CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            // NOTE: if your DTO field is named "id", change q.quizId() -> q.id()
                            return ServerGateway.quiz().startQuiz(ClientSession.sessionId(), q.quizId());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }, ServerGateway.EXECUTOR)
                    .thenAccept((QuizStartDTO started) -> Platform.runLater(() -> {
                        start.setDisable(false);
                        start.setText("Quiz starten");

                        SceneRouter.setRoot(new QuizQuestionScene(started.quizSessionId()));
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            start.setDisable(false);
                            start.setText("Quiz starten");

                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            AlertUtil.error("Quiz starten fehlgeschlagen",
                                    cause.getMessage() != null ? cause.getMessage() : cause.toString());
                        });
                        return null;
                    });
        });

        VBox box = new VBox(8, header, title, desc, meta, start);
        box.setPadding(new Insets(20));
        box.setPrefWidth(420);
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );
        return box;
    }

    // TIPS BOX
    private Parent tipsBox() {
        Label heading = new Label("Tipps für bessere Ergebnisse");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        heading.setTextFill(Color.web("#1a56ff"));

        Label t1 = new Label("• Nehmen Sie sich Zeit und lesen Sie die Fragen sorgfältig");
        Label t2 = new Label("• Sie erhalten sofortiges Feedback nach jeder Antwort");
        Label t3 = new Label("• Am Ende sehen Sie eine detaillierte Auswertung");

        VBox box = new VBox(6, heading, t1, t2, t3);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #f3f6ff; -fx-background-radius: 12;");
        return box;
    }
}
