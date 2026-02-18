package flash.client.main.gui.scenes.quizzes;


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

import java.util.concurrent.CompletableFuture;

public class UserRegistrationScene extends BorderPane {

    public UserRegistrationScene() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #f7faff, #ffffff);");
        setPadding(new Insets(40));

        VBox wrapper = new VBox(30, backButton(), registrationCard());
        wrapper.setAlignment(Pos.TOP_CENTER);
        setCenter(wrapper);
    }

    private Parent backButton() {
        Hyperlink back = new Hyperlink("← Zurück zur Startseite");
        back.setTextFill(Color.web("#333"));
        back.setOnAction(e -> SceneRouter.setRoot(new WelcomeScene()));

        VBox box = new VBox(back);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    private Parent registrationCard() {
        Label heading = new Label("Quiz-Client Registrierung");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label subtitle = new Label("Erstellen Sie ein Konto, um Quiz zu absolvieren");
        subtitle.setTextFill(Color.web("#555"));

        // Name field
        Label lblName = new Label("Name");
        TextField txtName = new TextField();
        txtName.setPromptText("Ihr Name");

        // Email field
        Label lblEmail = new Label("E-Mail");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("ihre.email@beispiel.de");

        // Password field
        Label lblPassword = new Label("Passwort");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");

        // Password field
        Label lblPassword2 = new Label("Passwort");
        PasswordField txtPassword2 = new PasswordField();
        txtPassword2.setPromptText("••••••••");

        // Button
        Button btnRegister = new Button("Registrieren");
        btnRegister.setPrefWidth(320);
        btnRegister.setStyle("-fx-background-color: #cd1aff; -fx-text-fill: white; -fx-font-weight: bold;");
        btnRegister.setOnAction(e -> {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String pw = txtPassword.getText();
            String pw2 = txtPassword2.getText();

            if (name.isEmpty() || email.isEmpty() || pw.isEmpty() || pw2.isEmpty()) {
                AlertUtil.warning("Eingabefehler", "Bitte alle Felder ausfüllen.");
                return;
            }
            if (!pw.equals(pw2)) {
                AlertUtil.warning("Eingabefehler", "Passwörter stimmen nicht überein.");
                return;
            }

            btnRegister.setDisable(true);
            btnRegister.setText("Registrieren...");

            CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return ServerGateway.auth().register(name, email, pw);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }, ServerGateway.EXECUTOR)
                    .thenAccept(session -> Platform.runLater(() -> {
                        btnRegister.setDisable(false);
                        btnRegister.setText("Registrieren");

                        ClientSession.set(session);
                        SceneRouter.setRoot(new QuizzesScene());
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            btnRegister.setDisable(false);
                            btnRegister.setText("Registrieren");

                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            String msg = (cause.getMessage() != null && !cause.getMessage().isBlank())
                                    ? cause.getMessage()
                                    : cause.toString();

                            AlertUtil.error("Registrierung fehlgeschlagen", msg);
                        });
                        return null;
                    });
        });


        // Login link
        Hyperlink loginLink = new Hyperlink("Bereits registriert? Jetzt anmelden");
        loginLink.setTextFill(Color.web("#7a2cff"));
        loginLink.setOnAction(e -> SceneRouter.setRoot(new UserLoginScene()));

        VBox form = new VBox(10,
                lblName, txtName,
                lblEmail, txtEmail,
                lblPassword, txtPassword,
                lblPassword2, txtPassword2,
                btnRegister,
                loginLink
        );
        form.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(20, heading, subtitle, form);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 12, 0, 0, 6);
        """);

        HBox layout = new HBox(card);
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
