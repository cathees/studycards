package flash.client.main.gui;

import flash.client.main.gui.scenes.welcome.WelcomeScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class FlashMainStage extends Application {

    @Override
    public void start(Stage stage) throws Exception {
            stage.setTitle("Lernkartensystem");
            SceneRouter.init(stage, new WelcomeScene(), 1100, 700);
    }


    public static void main(String[] args) { Application.launch(FlashMainStage.class); }
}
