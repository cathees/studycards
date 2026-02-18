package flash.client.main.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class SceneRouter {
    private static Stage STAGE;
    private static Scene SCENE;

    public static void init(Stage stage, Parent initialRoot, double w, double h) {
        STAGE = stage;
        SCENE = new Scene(initialRoot, w, h);
        STAGE.setScene(SCENE);
        STAGE.show();
    }

    public static void setRoot(Parent rootContent) {
        // Wrap any root in a StackPane so overlays can be shown on top
        StackPane wrapper = new StackPane(rootContent);
        wrapper.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Scene scene = new Scene(wrapper, 1280, 800);
        STAGE.setScene(scene);
        STAGE.show();
    }

    public static Stage getStage() { return STAGE; }

    private SceneRouter() {}
}
