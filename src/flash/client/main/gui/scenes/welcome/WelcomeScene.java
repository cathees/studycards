package flash.client.main.gui.scenes.welcome;


import flash.client.main.gui.SceneRouter;
import flash.client.main.gui.scenes.cardManagement.AdminLoginScene;
import flash.client.main.gui.scenes.quizzes.UserLoginScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WelcomeScene extends BorderPane {

    private static final String BRAND_GRADIENT =
            "linear-gradient(to bottom, #f4f6ff, #ffffff)";
    private static final String CARD_STYLE =
            "-fx-background-color: white;" +
                    "-fx-background-radius: 16;" +
                    "-fx-border-radius: 16;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.10), 12, 0, 0, 6);";
    private static final String SUBTLE_PANEL =
            "-fx-background-color: #f7f9fc; -fx-background-radius: 12;";

    public WelcomeScene() {
        setPadding(new Insets(32));
        setStyle("-fx-background-color: " + BRAND_GRADIENT + ";");

        VBox content = new VBox(36, header(), cards(), features());
        content.setAlignment(Pos.TOP_CENTER);
        setCenter(content);
    }

    private Parent header() {
        Label title = new Label("Lernkartensystem");
        title.setFont(Font.font(24));
        title.setTextFill(Color.web("#6C63FF"));

        Label subtitle = new Label("Verwalten Sie Ihre Lernkarten oder starten Sie ein Quiz");
        subtitle.setTextFill(Color.web("#616161"));

        VBox box = new VBox(8, title, subtitle);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Parent cards() {
        // “Lernkartenverwaltung” card
        VBox manage = card(
                "Lernkartenverwaltung",
                "Erstellen, bearbeiten und organisieren Sie Ihre Lernkarten",
                "Zur Verwaltung",
                () -> SceneRouter.setRoot(new AdminLoginScene())
        );

        // “Quiz-Client” card
        VBox quiz = card(
                "Quiz-Client",
                "Testen Sie Ihr Wissen mit interaktiven Quiz-Fragen",
                "Quiz starten",
                () -> SceneRouter.setRoot(new UserLoginScene())
        );

        // FlowPane ⇒ responsive wrap on resize
        FlowPane wrap = new FlowPane(24, 24, manage, quiz);
        wrap.setAlignment(Pos.CENTER);
        wrap.widthProperty().addListener((obs, w, h) -> {
            double cardW = Math.min(520, (h.doubleValue() - 24) / 2.0);
            manage.setPrefWidth(cardW);
            quiz.setPrefWidth(cardW);
        });
        return wrap;
    }

    private VBox card(String title, String desc, String cta, Runnable onClick) {
        Label t = new Label(title);
        t.setFont(Font.font(18));
        t.setTextFill(Color.web("#5b59ff"));

        Label d = new Label(desc);
        d.setWrapText(true);
        d.setTextFill(Color.web("#666"));

        Button b = new Button(cta + " →");
        b.setOnAction(e -> onClick.run());
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: #5b59ff; -fx-font-weight: bold;");

        VBox v = new VBox(10, t, d, b);
        v.setPadding(new Insets(24));
        v.setAlignment(Pos.TOP_LEFT);
        v.setMinWidth(320);
        v.setPrefHeight(180);
        v.setStyle(CARD_STYLE);
        return v;
    }

    private Parent features() {
        VBox f1 = feature("Einfache Verwaltung", "Lernkarten schnell erstellen und organisieren");
        VBox f2 = feature("Interaktive Quiz", "Sofortiges Feedback während des Lernens");
        VBox f3 = feature("Fortschritt tracken", "Behalten Sie den Überblick über Ihren Lernfortschritt");

        FlowPane wrap = new FlowPane(18, 18, f1, f2, f3);
        wrap.setAlignment(Pos.CENTER);
        wrap.widthProperty().addListener((obs, w, h) -> {
            double w0 = Math.min(320, (h.doubleValue() - 36) / 3.0);
            f1.setPrefWidth(w0); f2.setPrefWidth(w0); f3.setPrefWidth(w0);
        });
        return wrap;
    }

    private VBox feature(String title, String desc) {
        Label t = new Label(title);
        t.setFont(Font.font(16));
        t.setTextFill(Color.web("#444"));
        Label d = new Label(desc);
        d.setTextFill(Color.web("#6f6f6f"));
        VBox v = new VBox(6, t, d);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(18));
        v.setStyle(SUBTLE_PANEL);
        return v;
    }
}