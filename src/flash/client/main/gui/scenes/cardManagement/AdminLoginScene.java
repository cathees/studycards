package flash.client.main.gui.scenes.cardManagement;

import flash.client.main.ClientSession;
import flash.client.main.ServerGateway;
import flash.client.main.gui.AlertUtil;
import flash.client.main.gui.SceneRouter;
import flash.client.main.gui.scenes.welcome.WelcomeScene;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.concurrent.CompletableFuture;

import static flash.client.main.ServerGateway.EXECUTOR;

public class AdminLoginScene extends BorderPane {

    public AdminLoginScene() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #f7faff, #ffffff);");
        setPadding(new Insets(40));

        VBox wrapper = new VBox(30, backButton(), loginCard());
        wrapper.setAlignment(Pos.TOP_CENTER);
        setCenter(wrapper);
    }

    // Back link to WelcomeScene
    private Parent backButton() {
        Hyperlink back = new Hyperlink("← Zurück zur Startseite");
        back.setTextFill(Color.web("#333"));
        back.setOnAction(e -> SceneRouter.setRoot(new WelcomeScene()));

        VBox box = new VBox(back);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    // Main white card container with form and demo credentials
    private Parent loginCard() {
        Label heading = new Label("Lernkartenverwaltung");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label subtitle = new Label("Melden Sie sich an, um Lernkarten zu verwalten");
        subtitle.setTextFill(Color.web("#555"));

        VBox demoBox = demoCredentialsBox();

        // Email field
        Label lblEmail = new Label("E-Mail");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("ihre.email@beispiel.de");
        txtEmail.setPrefWidth(320);

        // Password field
        Label lblPassword = new Label("Passwort");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");

        // Login button
        Button btnLogin = new Button("Anmelden");
        btnLogin.setPrefWidth(320);
        btnLogin.setStyle("-fx-background-color: #1a56ff; -fx-text-fill: white; -fx-font-weight: bold;");
        btnLogin.setOnAction(e -> {
            String email = txtEmail.getText();
            String pw = txtPassword.getText();


            if (email.isEmpty() || pw.isEmpty()) {
                AlertUtil.error("Leere Zugangsdaten","Bitte geben Sie Ihre E-Mail-Adresse und Ihr Passwort ein.");
                return;
            }

            btnLogin.setDisable(true);
            btnLogin.setText("Anmelden...");
            CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return ServerGateway.auth().login(email, pw);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }, EXECUTOR)
                    .thenAccept(session -> Platform.runLater(() ->  {
                        btnLogin.setDisable(false);
                        btnLogin.setText("Anmelden");

                        ClientSession.set(session);
                        SceneRouter.setRoot(new CardManagementScene());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            btnLogin.setDisable(false);
                            btnLogin.setText("Anmelden");
                            AlertUtil.error("Server Error!" ,ex.getMessage());
                        });
                        return null;
                    });
        });

        // Registration link
        Hyperlink registerLink = new Hyperlink("Noch kein Konto? Jetzt registrieren");
        registerLink.setTextFill(Color.web("#1a56ff"));
        registerLink.setOnAction(e -> SceneRouter.setRoot(new AdminRegistrationScene()));

        VBox form = new VBox(10,
                lblEmail, txtEmail,
                lblPassword, txtPassword,
                btnLogin,
                registerLink
        );
        form.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(20, heading, subtitle, demoBox, form);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 12, 0, 0, 6);"
        );

        HBox layout = new HBox(card);
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    // Demo credentials info box
    private VBox demoCredentialsBox() {
        Label title = new Label("Demo-Zugangsdaten:");
        title.setFont(Font.font(null, FontWeight.BOLD, 12));
        Text email = new Text("E-Mail: student@lernsystem.de");
        Text pass = new Text("Passwort: student123");

        VBox box = new VBox(3, title, email, pass);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12));
        box.setStyle(
                "-fx-background-color: #f8faff;" +
                        "-fx-border-color: #d9e0f0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
        return box;
    }
}