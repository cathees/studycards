package flash.client.main.gui.scenes.cardManagement;

import flash.common.dto.CardCreateDTO;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.function.Consumer;

public class CardCreationDialog {

    public static void showCardDialog(StackPane root, Consumer<CardCreateDTO> onCardCreated) {
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.35);");
        overlay.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox dialog = new CardCreationDialogContent(onCardCreated, root);
        dialog.setPrefWidth(550);
        dialog.setMaxWidth(550);
        dialog.setPrefHeight(620);
        dialog.setMaxHeight(620);
        dialog.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-padding: 28;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 25, 0, 0, 5);
    """);

        StackPane.setAlignment(dialog, Pos.CENTER);
        StackPane modalLayer = new StackPane(overlay, dialog);
        root.getChildren().add(modalLayer);

        overlay.setOnMouseClicked(_ -> root.getChildren().remove(modalLayer));
    }

    static class CardCreationDialogContent extends VBox {
        private String selectedCategory = null;

        public CardCreationDialogContent(Consumer<CardCreateDTO> onCardCreated, StackPane root) {
            setSpacing(16);
            setPrefWidth(500);

            Label title = new Label("Neue Lernkarte erstellen");
            title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            Label subtitle = new Label("Fügen Sie eine neue Frage hinzu");
            subtitle.setTextFill(Color.web("#6b7280"));

            HBox header = new HBox(title, new Region(), createCloseButton());
            HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

            FlowPane catFlow = new FlowPane(8, 8);
            List<String> categories = List.of(
                    "Geographie", "Geschichte", "Mathematik", "Physik",
                    "Chemie", "Biologie", "Literatur",
                    "Sprachen", "Informatik", "Kunst"
            );

            for (String cat : categories) {
                Button tag = new Button(cat);
                tag.setStyle(chipStyle(false));
                tag.setOnAction(_ -> {
                    selectedCategory = cat;
                    catFlow.getChildren().forEach(node -> node.setStyle(chipStyle(false)));
                    tag.setStyle(chipStyle(true));
                });
                catFlow.getChildren().add(tag);
            }

            TextField customCategory = new TextField();
            customCategory.setPromptText("Eigene Kategorie eingeben…");

            TextArea question = new TextArea();
            question.setPromptText("Was möchten Sie fragen?");
            question.setPrefRowCount(3);

            TextArea answer = new TextArea();
            answer.setPromptText("Geben Sie die Antwortmöglichkeiten durch Komma separiert ein…");
            answer.setPrefRowCount(3);

            Button cancel = new Button("Abbrechen");
            cancel.setPrefWidth(160);
            cancel.setStyle("""
                -fx-background-color: #f9fafb;
                -fx-border-color: #e5e7eb;
                -fx-text-fill: #374151;
                -fx-background-radius: 8;
                -fx-border-radius: 8;
            """);

            Button create = new Button("Karte erstellen");
            create.setPrefWidth(160);
            create.setStyle("""
                -fx-background-color: #1d4ed8;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
            """);

            create.setOnAction(_ -> {
                String custom = customCategory.getText() == null ? "" : customCategory.getText().trim();
                String category = !custom.isEmpty() ? custom : selectedCategory;

                String questionText = question.getText().trim();
                String answerText = answer.getText().trim();

                if (category == null || category.isBlank() || questionText.isEmpty() || answerText.isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "Bitte alle Felder ausfüllen.").show();
                    return;
                }

                CardCreateDTO dto = new CardCreateDTO(category, questionText, answerText);
                onCardCreated.accept(dto);

                root.getChildren().remove(getParent());
            });

            cancel.setOnAction(_ -> root.getChildren().remove(getParent()));

            HBox buttonBar = new HBox(12, cancel, create);
            buttonBar.setAlignment(Pos.CENTER_RIGHT);

            getChildren().addAll(
                    header,
                    new Separator(),
                    catFlow,
                    customCategory,
                    new Label("Frage *"), question,
                    new Label("Antwort *"), answer,
                    buttonBar
            );
        }

        private Button createCloseButton() {
            Button close = new Button("✕");
            close.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #9ca3af;
                -fx-font-size: 14;
            """);
            close.setOnAction(_ -> {
                StackPane root = (StackPane) getParent().getParent();
                root.getChildren().remove(getParent());
            });
            return close;
        }

        private String chipStyle(boolean active) {
            return active
                    ? "-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-background-radius: 16; -fx-padding: 4 10;"
                    : "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-background-radius: 16; -fx-padding: 4 10;";
        }
    }
}
