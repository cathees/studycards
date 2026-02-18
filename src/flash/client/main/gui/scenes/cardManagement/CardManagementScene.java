package flash.client.main.gui.scenes.cardManagement;

import flash.client.main.ClientSession;
import flash.client.main.ServerGateway;
import flash.client.main.gui.AlertUtil;
import flash.client.main.gui.SceneRouter;
import flash.client.main.gui.scenes.welcome.WelcomeScene;
import flash.common.dto.CardCreateDTO;
import flash.common.dto.CardDTO;
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

public class CardManagementScene extends BorderPane {

    private final FlowPane grid;
    private List<CardDTO> allCards;
    private HBox statsRow;
    private int totalCount = 5;
    private int filteredCount = totalCount;

    public CardManagementScene() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #f8faff, #ffffff);");
        setPadding(new Insets(20));

        allCards = new ArrayList<>();

        grid = new FlowPane();
        grid.setHgap(25);
        grid.setVgap(25);
        grid.setPadding(new Insets(15));
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setPrefWrapLength(1000);

        Parent top = topBar();
        Parent stats = statsRow();
        Parent filter = filterBar();
        Parent gridScroll = cardsGrid();

        VBox layout = new VBox(25, top, stats, filter, gridScroll);
        layout.setPadding(new Insets(20, 40, 40, 40));
        setCenter(layout);

        updateGrid(allCards);
        updateStats(allCards);
        loadCardsFromServer();
    }

    private void loadCardsFromServer() {
        String sessionId = ClientSession.sessionId();

        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return ServerGateway.cards().listCards(sessionId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }, ServerGateway.EXECUTOR)
                .thenAccept(cards -> Platform.runLater(() -> {
                    allCards = new ArrayList<>(cards);
                    updateGrid(allCards);
                    updateStats(allCards);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        AlertUtil.error("Fehler", cause.getMessage() != null ? cause.getMessage() : cause.toString());
                    });
                    return null;
                });
    }

    private Parent topBar() {
        Label title = new Label("Lernkartenverwaltung");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label welcome = new Label("Willkommen, " + ClientSession.name());
        welcome.setTextFill(Color.GRAY);

        VBox titleBox = new VBox(2, title, welcome);

        Hyperlink logout = new Hyperlink("Abmelden");
        logout.setOnAction(_ -> SceneRouter.setRoot(new WelcomeScene()));
        logout.setStyle("-fx-text-fill: #1a56ff; -fx-font-weight: bold;");

        HBox bar = new HBox(titleBox, new Region(), logout);
        HBox.setHgrow(bar.getChildren().get(1), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private Parent statsRow() {
        HBox row = new HBox(40);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(20, 0, 10, 0));
        this.statsRow = row;
        return row;
    }

    private void updateStats(List<CardDTO> cards) {
        if (cards == null || statsRow == null) return;

        totalCount = allCards.size();
        filteredCount = cards.size();
        int categoryCount = (int) allCards.stream().map(CardDTO::category).distinct().count();

        statsRow.getChildren().setAll(
                statCard("Gesamt", String.valueOf(totalCount), "\uD83D\uDCD6"),
                statCard("Kategorien", String.valueOf(categoryCount), "\uD83D\uDD0D"),
                statCard("Gefiltert", String.valueOf(filteredCount), "\uD83D\uDD0E")
        );
    }

    private VBox statCard(String label, String value, String icon) {
        Label lblIcon = new Label(icon);
        lblIcon.setFont(Font.font(20));
        lblIcon.setTextFill(Color.web("#333"));

        Label lblLabel = new Label(label);
        lblLabel.setTextFill(Color.web("#555"));
        lblLabel.setFont(Font.font(13));

        HBox header = new HBox(8, lblIcon, lblLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblValue.setTextFill(Color.web("#1a56ff"));

        VBox card = new VBox(8, header, lblValue);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);
        card.setPrefHeight(90);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 3);"
        );

        return card;
    }

    private Parent filterBar() {
        TextField search = new TextField();
        search.setPromptText("Suche nach Frage oder Antwort...");
        search.setPrefWidth(400);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(
                "Alle Kategorien", "Geographie", "Geschichte", "Mathematik", "Physik",
                "Chemie", "Biologie", "Literatur",
                "Sprachen", "Informatik", "Kunst", "Benutzerdefinierte Kategorien");
        categoryBox.getSelectionModel().selectFirst();

        Button newCard = new Button("+ Neue Karte");
        newCard.setStyle("-fx-background-color: #1a56ff; -fx-text-fill: white; -fx-font-weight: bold;");

        newCard.setOnAction(_ -> {
            StackPane rootStack = (StackPane) getScene().getRoot();

            CardCreationDialog.showCardDialog(rootStack, newCardData -> {
                newCard.setDisable(true);

                CompletableFuture
                        .supplyAsync(() -> {
                            try {
                                CardCreateDTO createDTO = new CardCreateDTO(
                                        newCardData.category(),
                                        newCardData.question(),
                                        newCardData.answer()
                                );
                                return ServerGateway.cards().createCard(ClientSession.sessionId(), createDTO);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }, ServerGateway.EXECUTOR)
                        .thenAccept((CardDTO _) -> Platform.runLater(() -> {
                            newCard.setDisable(false);
                            loadCardsFromServer();
                        }))
                        .exceptionally(ex2 -> {
                            Platform.runLater(() -> {
                                newCard.setDisable(false);
                                Throwable cause = ex2.getCause() != null ? ex2.getCause() : ex2;
                                AlertUtil.error("Speichern fehlgeschlagen",
                                        cause.getMessage() != null ? cause.getMessage() : cause.toString());
                            });
                            return null;
                        });
            });
        });

        Runnable applyFilter = () -> {
            String searchText = search.getText().toLowerCase().trim();
            String selectedCategory = categoryBox.getValue();
            boolean all = "Alle Kategorien".equals(selectedCategory);
            boolean custom = "Benutzerdefinierte Kategorien".equals(selectedCategory);

            List<CardDTO> filtered = allCards.stream()
                    .filter(c -> {
                        if (all) return true;
                        if (custom) return c.isCustomCategory();
                        return c.category() != null && c.category().equalsIgnoreCase(selectedCategory);
                    })
                    .filter(c -> {
                        String q = c.question() == null ? "" : c.question().toLowerCase();
                        String a = c.answer() == null ? "" : c.answer().toLowerCase();
                        return q.contains(searchText) || a.contains(searchText);
                    })
                    .toList();


            updateGrid(filtered);
            updateStats(filtered);
        };

        search.textProperty().addListener((_, _, _) -> applyFilter.run());
        categoryBox.valueProperty().addListener((_, _, _) -> applyFilter.run());

        HBox row = new HBox(15, search, categoryBox, newCard);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 0, 0));
        return row;
    }

    private void updateGrid(List<CardDTO> cardsToShow) {
        grid.getChildren().clear();
        for (CardDTO card : cardsToShow) {
            VBox cardBox = cardBox(card);
            cardBox.setPrefWidth(300);
            grid.getChildren().add(cardBox);
        }
        filteredCount = cardsToShow.size();
    }

    private Parent cardsGrid() {
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scrollPane;
    }

    private VBox cardBox(CardDTO card) {
        Label category = new Label(card.category());
        category.setStyle("""
        -fx-background-color: #eef2ff;
        -fx-text-fill: #3b82f6;
        -fx-font-size: 12;
        -fx-padding: 3 10 3 10;
        -fx-background-radius: 10;
                   \s""");

        Label questionLabel = new Label("Frage:");
        questionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        questionLabel.setTextFill(Color.web("#666"));

        Label question = new Label(card.question());
        question.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        question.setTextFill(Color.web("#222"));
        question.setWrapText(true);
        question.setMaxWidth(260);

        Separator sep1 = new Separator();
        Platform.runLater(() -> {
            var line = sep1.lookup(".line");
            if (line != null)
                line.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 1;");
        });

        Label answerLabel = new Label("Antwort:");
        answerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        answerLabel.setTextFill(Color.web("#666"));

        Label answer = new Label(card.answer());
        answer.setFont(Font.font("Arial", 13));
        answer.setTextFill(Color.web("#222"));
        answer.setWrapText(true);
        answer.setMaxWidth(260);

        Separator sep2 = new Separator();
        Platform.runLater(() -> {
            var line = sep2.lookup(".line");
            if (line != null)
                line.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 1;");
        });

        Label created = new Label("Erstellt: " + card.created());
        created.setFont(Font.font("Arial", 11));
        created.setTextFill(Color.web("#888"));

        VBox content = new VBox(6, questionLabel, question, sep1, answerLabel, answer, sep2);
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox bottom = new VBox(created);
        bottom.setAlignment(Pos.BOTTOM_LEFT);
        bottom.setPadding(new Insets(12, 0, 0, 0));

        Button deleteButton = new Button("🗑");
        deleteButton.setStyle("""
        -fx-background-color: transparent;
        -fx-font-size: 24;
        -fx-text-fill: #dc2626;
        -fx-cursor: hand;
    """);

        deleteButton.setOnAction(_ -> {
            deleteButton.setDisable(true);

            CompletableFuture
                    .runAsync(() -> {
                        try {
                            ServerGateway.cards().deleteCard(ClientSession.sessionId(), card.id());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }, ServerGateway.EXECUTOR)
                    .thenRun(() -> Platform.runLater(this::loadCardsFromServer))
                    .exceptionally(ex2 -> {
                        Platform.runLater(() -> {
                            deleteButton.setDisable(false);
                            Throwable cause = ex2.getCause() != null ? ex2.getCause() : ex2;
                            AlertUtil.error("Löschen fehlgeschlagen",
                                    cause.getMessage() != null ? cause.getMessage() : cause.toString());
                        });
                        return null;
                    });
        });

        VBox innerCard = new VBox(10, category, content, bottom);
        innerCard.setPadding(new Insets(20, 22, 20, 22));
        innerCard.setPrefSize(300, 250);
        innerCard.setMinSize(300, 250);
        innerCard.setMaxSize(300, 250);
        innerCard.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 14;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0, 0.06), 10, 0, 0, 3);
    """);

        StackPane overlay = new StackPane(innerCard, deleteButton);
        overlay.setPrefSize(300, 250);
        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        StackPane.setMargin(deleteButton, new Insets(10, 14, 0, 0));

        return new VBox(overlay);
    }
}
