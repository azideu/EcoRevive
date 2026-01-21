package EcoRevive;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ManagementPanel extends SplitPane {
    private RecyclingManager manager;
    private TextArea logArea;
    private Label pendingCountLabel;

    public ManagementPanel(RecyclingManager manager) {
        this.manager = manager;
        initialize();
    }

    private void initialize() {
        setDividerPositions(0.4); // 40% for submit form

        VBox submitPane = createSubmitPane();
        VBox processPane = createProcessPane();

        getItems().addAll(submitPane, processPane);
    }

    private VBox createSubmitPane() {
        VBox container = new VBox();
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        VBox card = new VBox(20);
        card.getStyleClass().add(Constants.STYLE_CARD);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        
        Label header = new Label("New Entry");
        header.getStyleClass().add(Constants.STYLE_HEADER_LABEL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList(
            Constants.CAT_MOBILE, Constants.CAT_LAPTOP, Constants.CAT_TABLET, 
            Constants.CAT_TV, Constants.CAT_APPLIANCE, Constants.CAT_OTHER
        ));
        categoryBox.setPromptText("Select Category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        
        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");
        
        ComboBox<String> conditionBox = new ComboBox<>(FXCollections.observableArrayList(
            Constants.COND_NEW, Constants.COND_USED, Constants.COND_BROKEN, Constants.COND_REFURBISHED
        ));
        conditionBox.setPromptText("Condition");
        conditionBox.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryBox, 1, 1);
        grid.add(new Label("Weight (kg):"), 0, 2);
        grid.add(weightField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionBox, 1, 3);

        Button submitBtn = new Button("Submit Item");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String category = categoryBox.getValue();
                String weightText = weightField.getText();
                String condition = conditionBox.getValue();

                if (name.isEmpty() || category == null || weightText.isEmpty() || condition == null) {
                    showAlert("Error", "Please fill all fields.");
                    return;
                }
                
                double weight = Double.parseDouble(weightText);

                String id = manager.generateNextId();
                EWasteItem item = new EWasteItem(id, name, category, weight, condition);
                manager.addItemToQueue(item);

                nameField.clear();
                weightField.clear();
                categoryBox.getSelectionModel().clearSelection();
                categoryBox.setPromptText("Select Category");
                conditionBox.getSelectionModel().clearSelection();
                conditionBox.setPromptText("Condition");

                updatePendingCount();
                showAlert("Success", "Item added to queue!");
                if (logArea != null) logArea.appendText("Submitted: " + item.getName() + " (" + condition + ")\n");

            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid weight format.");
            }
        });

        grid.add(submitBtn, 1, 4);
        
        card.getChildren().addAll(header, grid);
        container.getChildren().add(card);
        return container;
    }

    private VBox createProcessPane() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        VBox card = new VBox(15);
        card.getStyleClass().add(Constants.STYLE_CARD);
        card.setPadding(new Insets(20));
        VBox.setVgrow(card, Priority.ALWAYS);

        Label header = new Label("Processing Queue");
        header.getStyleClass().add(Constants.STYLE_HEADER_LABEL);

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER_LEFT);
        pendingCountLabel = new Label("Pending Items: " + manager.getPendingItems().size());
        pendingCountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        
        Button processBtn = new Button("Process Pending");
        processBtn.setOnAction(e -> {
            MyLinkedList<EWasteItem> processed = manager.processQueue();
            if (processed.isEmpty()) {
                showAlert("Info", "No items to process.");
            } else {
                for (EWasteItem item : processed) {
                    if (logArea != null) logArea.appendText("Processed: " + item + "\n");
                }
                updatePendingCount();
                showAlert("Success", "Processed " + processed.size() + " items.");
            }
        });

        top.getChildren().addAll(pendingCountLabel, processBtn);

        logArea = new TextArea();
        logArea.setEditable(false);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        card.getChildren().addAll(header, top, logArea);
        panel.getChildren().add(card);
        
        return panel;
    }

    private void updatePendingCount() {
        if (pendingCountLabel != null) {
            pendingCountLabel.setText("Pending Items: " + manager.getPendingItems().size());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Fix for fullscreen mode: Set owner to the main window
        if (getScene() != null && getScene().getWindow() != null) {
            alert.initOwner(getScene().getWindow());
        }

        DialogPane pane = alert.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        pane.getStyleClass().add(Constants.STYLE_CUSTOM_DIALOG);
        
        // Note: Accessing the main scene to check theme might be tricky here without passing parent.
        // For now, we'll rely on the stylesheet. In a more complex app, we might pass a ThemeManager or similar.
        
        alert.showAndWait();
    }
}
