package EcoRevive;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class EcoReviveFX extends Application {

    private RecyclingManager manager;
    private FileService fileService;
    private BorderPane mainLayout;
    private TableView<EWasteItem> inventoryTable;
    private Label pendingCountLabel;
    private TextArea logArea;
    private FXDashboardPanel dashboardPanel;

    @Override
    public void start(Stage primaryStage) {
        manager = new RecyclingManager();
        fileService = new FileService();
        manager.setRecycledItems(fileService.loadInventory());

        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Navigation
        HBox navBar = createNavBar();
        mainLayout.setTop(navBar);

        // Initial View
        showManagementPanel();

        Scene scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("EcoRevive: Smart E-Waste Recycling (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            saveAndExit();
        });
        primaryStage.show();

        updatePendingCount();
    }

    private HBox createNavBar() {
        HBox nav = new HBox(15);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(15));
        nav.getStyleClass().add("nav-bar");

        ToggleGroup group = new ToggleGroup();
        ToggleButton manageBtn = createNavButton("Manage Items", group);
        ToggleButton inventoryBtn = createNavButton("Inventory", group);
        ToggleButton statsBtn = createNavButton("Statistics", group);

        manageBtn.setSelected(true);
        manageBtn.setOnAction(e -> showManagementPanel());
        inventoryBtn.setOnAction(e -> showInventoryPanel());
        statsBtn.setOnAction(e -> showStatsPanel());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button themeBtn = new Button("☀");
        themeBtn.setTooltip(new Tooltip("Toggle Light/Dark Mode"));
        themeBtn.setOnAction(e -> {
            if (mainLayout.getStyleClass().contains("light-theme")) {
                mainLayout.getStyleClass().remove("light-theme");
                themeBtn.setText("☀");
            } else {
                mainLayout.getStyleClass().add("light-theme");
                themeBtn.setText("☾");
            }
        });

        nav.getChildren().addAll(manageBtn, inventoryBtn, statsBtn, spacer, themeBtn);
        return nav;
    }

    private ToggleButton createNavButton(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefSize(160, 40);
        return btn;
    }

    private void showManagementPanel() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4); // 40% for submit form

        VBox submitPane = createSubmitPane();
        VBox processPane = createProcessPane();

        splitPane.getItems().addAll(submitPane, processPane);
        mainLayout.setCenter(splitPane);
    }

    private VBox createSubmitPane() {
        VBox container = new VBox();
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        Label header = new Label("New Entry");
        header.getStyleClass().add("header-label");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList("Mobile", "Laptop", "Tablet", "TV", "Appliance", "Other"));
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        TextField weightField = new TextField();
        ComboBox<String> conditionBox = new ComboBox<>(FXCollections.observableArrayList("New", "Used", "Broken", "Refurbished"));
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
                double weight = Double.parseDouble(weightField.getText());
                String condition = conditionBox.getValue();

                if (name.isEmpty() || category == null) {
                    showAlert("Error", "Please fill all fields.");
                    return;
                }

                String id = manager.generateNextId();
                EWasteItem item = new EWasteItem(id, name, category, weight, condition);
                manager.addItemToQueue(item);

                nameField.clear();
                weightField.clear();
                categoryBox.getSelectionModel().clearSelection();
                conditionBox.getSelectionModel().clearSelection();

                updatePendingCount();
                showAlert("Success", "Item added to queue!");
                if (logArea != null) logArea.appendText("Submitted: " + item.getName() + "\n");

            } catch (NumberFormatException ex) {
                showAlert("Error", "Invalid weight format.");
            }
        });

        grid.add(submitBtn, 1, 4);
        
        container.getChildren().addAll(header, grid);
        return container;
    }

    private VBox createProcessPane() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label header = new Label("Processing Queue");
        header.getStyleClass().add("header-label");

        HBox top = new HBox(20);
        top.setAlignment(Pos.CENTER_LEFT);
        pendingCountLabel = new Label("Pending Items: " + manager.getPendingItems().size());
        pendingCountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
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

        panel.getChildren().addAll(header, top, logArea);
        return panel;
    }

    private void showInventoryPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        // Search Panel
        HBox searchPanel = new HBox(10);
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        Button searchBtn = new Button("Search");
        Button clearBtn = new Button("Clear");
        
        searchBtn.setOnAction(e -> updateInventoryTable(manager.searchItems(searchField.getText())));
        clearBtn.setOnAction(e -> {
            searchField.clear();
            updateInventoryTable(manager.getRecycledItems());
        });

        ComboBox<String> sortBox = new ComboBox<>(FXCollections.observableArrayList(
            "Weight (Low-High)", "Weight (High-Low)", "Category", "Name", "Condition"
        ));
        sortBox.setPromptText("Sort By...");
        sortBox.setOnAction(e -> {
            String criteria = sortBox.getValue();
            if (criteria != null) {
                if (criteria.equals("Weight (Low-High)")) {
                    manager.sortItems((i1, i2) -> Double.compare(i1.getWeight(), i2.getWeight()));
                } else if (criteria.equals("Weight (High-Low)")) {
                    manager.sortItems((i1, i2) -> Double.compare(i2.getWeight(), i1.getWeight()));
                } else if (criteria.equals("Category")) {
                    manager.sortItems((i1, i2) -> i1.getCategory().compareTo(i2.getCategory()));
                } else if (criteria.equals("Name")) {
                    manager.sortItems((i1, i2) -> i1.getName().compareTo(i2.getName()));
                } else if (criteria.equals("Condition")) {
                    manager.sortItems((i1, i2) -> i1.getCondition().compareTo(i2.getCondition()));
                }
                updateInventoryTable(manager.getRecycledItems());
            }
        });

        searchPanel.getChildren().addAll(new Label("Search:"), searchField, searchBtn, clearBtn, sortBox);

        // Table
        inventoryTable = new TableView<>();
        TableColumn<EWasteItem, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));
        
        TableColumn<EWasteItem, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        
        TableColumn<EWasteItem, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory()));
        
        TableColumn<EWasteItem, Number> colWeight = new TableColumn<>("Weight (kg)");
        colWeight.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getWeight()));
        
        TableColumn<EWasteItem, String> colCond = new TableColumn<>("Condition");
        colCond.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCondition()));

        inventoryTable.getColumns().add(colId);
        inventoryTable.getColumns().add(colName);
        inventoryTable.getColumns().add(colCat);
        inventoryTable.getColumns().add(colWeight);
        inventoryTable.getColumns().add(colCond);
        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        // Buttons
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        Button deleteBtn = new Button("Delete Selected");
        Button editBtn = new Button("Edit Selected");
        Button exportBtn = new Button("Export CSV");
        Button refreshBtn = new Button("Refresh");

        deleteBtn.setOnAction(e -> deleteSelectedItem());
        editBtn.setOnAction(e -> editSelectedItem());
        exportBtn.setOnAction(e -> exportCSV());
        refreshBtn.setOnAction(e -> updateInventoryTable(manager.getRecycledItems()));

        buttonPanel.getChildren().addAll(deleteBtn, editBtn, exportBtn, refreshBtn);

        panel.getChildren().addAll(searchPanel, inventoryTable, buttonPanel);
        mainLayout.setCenter(panel);
        updateInventoryTable(manager.getRecycledItems());
    }

    private void showStatsPanel() {
        dashboardPanel = new FXDashboardPanel(manager);
        
        VBox container = new VBox(20, dashboardPanel);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        Button refreshBtn = new Button("Refresh Statistics");
        refreshBtn.setOnAction(e -> dashboardPanel.updateCharts());
        
        container.getChildren().add(refreshBtn);
        mainLayout.setCenter(container);
    }

    private void updateInventoryTable(MyLinkedList<EWasteItem> items) {
        if (inventoryTable != null) {
            ObservableList<EWasteItem> list = FXCollections.observableArrayList();
            for (EWasteItem item : items) {
                list.add(item);
            }
            inventoryTable.setItems(list);
        }
    }

    private void updatePendingCount() {
        if (pendingCountLabel != null) {
            pendingCountLabel.setText("Pending Items: " + manager.getPendingItems().size());
        }
    }

    private void deleteSelectedItem() {
        EWasteItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an item to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Item " + selected.getId() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (manager.removeItem(selected.getId())) {
                updateInventoryTable(manager.getRecycledItems());
            }
        }
    }

    private void editSelectedItem() {
        EWasteItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an item to edit.");
            return;
        }

        Dialog<EWasteItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.setHeaderText("Edit details for " + selected.getId());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selected.getName());
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList("Mobile", "Laptop", "Tablet", "TV", "Appliance", "Other"));
        categoryBox.setValue(selected.getCategory());
        TextField weightField = new TextField(String.valueOf(selected.getWeight()));
        ComboBox<String> conditionBox = new ComboBox<>(FXCollections.observableArrayList("New", "Used", "Broken", "Refurbished"));
        conditionBox.setValue(selected.getCondition());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryBox, 1, 1);
        grid.add(new Label("Weight:"), 0, 2);
        grid.add(weightField, 1, 2);
        grid.add(new Label("Condition:"), 0, 3);
        grid.add(conditionBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    manager.updateItem(selected.getId(), nameField.getText(), categoryBox.getValue(), Double.parseDouble(weightField.getText()), conditionBox.getValue());
                    updateInventoryTable(manager.getRecycledItems());
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid weight.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(mainLayout.getScene().getWindow());
        
        if (file != null) {
            fileService.exportToCSV(manager.getRecycledItems(), file.getAbsolutePath());
            showAlert("Success", "Exported to " + file.getName());
        }
    }

    private void saveAndExit() {
        fileService.saveInventory(manager.getRecycledItems());
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
