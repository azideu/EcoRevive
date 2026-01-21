package EcoRevive;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Optional;

public class InventoryPanel extends VBox {
    private RecyclingManager manager;
    private FileService fileService;
    private TableView<EWasteItem> inventoryTable;

    public InventoryPanel(RecyclingManager manager, FileService fileService) {
        this.manager = manager;
        this.fileService = fileService;
        initialize();
    }

    private void initialize() {
        setPadding(new Insets(20));
        setSpacing(10);

        VBox card = new VBox(15);
        card.getStyleClass().add(Constants.STYLE_CARD);
        card.setPadding(new Insets(20));
        VBox.setVgrow(card, Priority.ALWAYS);

        // Search Panel
        HBox searchPanel = createSearchPanel();

        // Table
        inventoryTable = createTable();
        VBox.setVgrow(inventoryTable, Priority.ALWAYS);

        // Buttons
        HBox buttonPanel = createButtonPanel();

        card.getChildren().addAll(searchPanel, inventoryTable, buttonPanel);
        getChildren().add(card);
        
        updateInventoryTable(manager.getRecycledItems());
    }

    private HBox createSearchPanel() {
        HBox searchPanel = new HBox(10);
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name/ID...");
        Button searchBtn = new Button("Search");
        Button clearBtn = new Button("Clear");
        
        searchBtn.setOnAction(e -> updateInventoryTable(manager.searchItems(searchField.getText())));
        
        ComboBox<String> categoryFilterBox = new ComboBox<>(FXCollections.observableArrayList(
            "All Categories", Constants.CAT_MOBILE, Constants.CAT_LAPTOP, Constants.CAT_TABLET, 
            Constants.CAT_TV, Constants.CAT_APPLIANCE, Constants.CAT_OTHER
        ));
        categoryFilterBox.setPromptText("Filter Category");
        categoryFilterBox.setOnAction(e -> {
            String selected = categoryFilterBox.getValue();
            if (selected == null || selected.equals("All Categories")) {
                updateInventoryTable(manager.getRecycledItems());
            } else {
                MyLinkedList<EWasteItem> filtered = new MyLinkedList<>();
                for (EWasteItem item : manager.getRecycledItems()) {
                    if (item.getCategory().equals(selected)) {
                        filtered.add(item);
                    }
                }
                updateInventoryTable(filtered);
            }
        });

        clearBtn.setOnAction(e -> {
            searchField.clear();
            categoryFilterBox.setValue(null);
            updateInventoryTable(manager.getRecycledItems());
        });

        ComboBox<String> sortBox = new ComboBox<>(FXCollections.observableArrayList(
            "ID", "Weight", "Category", "Name", "Condition"
        ));
        sortBox.setPromptText("Sort By...");

        Button orderBtn = new Button("Ascending");
        orderBtn.setOnAction(e -> {
            if (orderBtn.getText().equals("Ascending")) {
                orderBtn.setText("Descending");
            } else {
                orderBtn.setText("Ascending");
            }
            // Trigger sort update
            sortBox.fireEvent(new javafx.event.ActionEvent());
        });

        // Define the sort action
        javafx.event.EventHandler<javafx.event.ActionEvent> sortAction = e -> {
            String criteria = sortBox.getValue();
            boolean ascending = orderBtn.getText().equals("Ascending");
            
            if (criteria != null) {
                java.util.Comparator<EWasteItem> comparator = null;
                
                if (criteria.equals("ID")) {
                    comparator = (i1, i2) -> i1.getId().compareTo(i2.getId());
                } else if (criteria.equals("Weight")) {
                    comparator = (i1, i2) -> Double.compare(i1.getWeight(), i2.getWeight());
                } else if (criteria.equals("Category")) {
                    comparator = (i1, i2) -> i1.getCategory().compareTo(i2.getCategory());
                } else if (criteria.equals("Name")) {
                    comparator = (i1, i2) -> i1.getName().compareTo(i2.getName());
                } else if (criteria.equals("Condition")) {
                    comparator = (i1, i2) -> i1.getCondition().compareTo(i2.getCondition());
                }

                if (comparator != null) {
                    if (!ascending) {
                        comparator = comparator.reversed();
                    }
                    manager.sortItems(comparator);
                    updateInventoryTable(manager.getRecycledItems());
                }
            }
        };

        sortBox.setOnAction(sortAction);
        orderBtn.setOnAction(e -> {
            if (orderBtn.getText().equals("Ascending")) {
                orderBtn.setText("Descending");
            } else {
                orderBtn.setText("Ascending");
            }
            sortAction.handle(new javafx.event.ActionEvent());
        });

        searchPanel.getChildren().addAll(new Label("Filter:"), searchField, searchBtn, clearBtn, sortBox, orderBtn, categoryFilterBox);
        return searchPanel;
    }

    private TableView<EWasteItem> createTable() {
        TableView<EWasteItem> table = new TableView<>();
        
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
        
        colCond.setCellFactory(column -> new TableCell<EWasteItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add(Constants.STYLE_STATUS_BADGE);
                    
                    switch (item.toLowerCase()) {
                        case "new": badge.getStyleClass().add("status-new"); break;
                        case "broken": badge.getStyleClass().add("status-broken"); break;
                        case "used": badge.getStyleClass().add("status-used"); break;
                        case "refurbished": badge.getStyleClass().add("status-refurbished"); break;
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        table.getColumns().add(colId);
        table.getColumns().add(colName);
        table.getColumns().add(colCat);
        table.getColumns().add(colWeight);
        table.getColumns().add(colCond);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        return table;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        
        Button deleteBtn = new Button("Delete Selected");
        Button editBtn = new Button("Edit Selected");
        Button exportBtn = new Button("Export CSV");
        Button refreshBtn = new Button("Refresh");
        Button undoBtn = new Button("Undo Delete");

        deleteBtn.setOnAction(e -> deleteSelectedItem());
        editBtn.setOnAction(e -> editSelectedItem());
        exportBtn.setOnAction(e -> exportCSV());
        refreshBtn.setOnAction(e -> updateInventoryTable(manager.getRecycledItems()));
        undoBtn.setOnAction(e -> {
            if (manager.undoDelete()) {
                updateInventoryTable(manager.getRecycledItems());
                showAlert("Success", "Restored last deleted item.");
            } else {
                showAlert("Info", "Nothing to undo.");
            }
        });

        buttonPanel.getChildren().addAll(deleteBtn, editBtn, exportBtn, refreshBtn, undoBtn);
        return buttonPanel;
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

    private void deleteSelectedItem() {
        EWasteItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an item to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Item " + selected.getId() + "?");
        if (getScene() != null && getScene().getWindow() != null) {
            alert.initOwner(getScene().getWindow());
        }
        styleDialog(alert);
        
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
        if (getScene() != null && getScene().getWindow() != null) {
            dialog.initOwner(getScene().getWindow());
        }
        styleDialog(dialog);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selected.getName());
        ComboBox<String> categoryBox = new ComboBox<>(FXCollections.observableArrayList(
            Constants.CAT_MOBILE, Constants.CAT_LAPTOP, Constants.CAT_TABLET, 
            Constants.CAT_TV, Constants.CAT_APPLIANCE, Constants.CAT_OTHER
        ));
        categoryBox.setValue(selected.getCategory());
        TextField weightField = new TextField(String.valueOf(selected.getWeight()));
        ComboBox<String> conditionBox = new ComboBox<>(FXCollections.observableArrayList(
            Constants.COND_NEW, Constants.COND_USED, Constants.COND_BROKEN, Constants.COND_REFURBISHED
        ));
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
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        
        if (file != null) {
            fileService.exportToCSV(manager.getRecycledItems(), file.getAbsolutePath());
            showAlert("Success", "Exported to " + file.getName());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (getScene() != null && getScene().getWindow() != null) {
            alert.initOwner(getScene().getWindow());
        }
        styleDialog(alert);
        alert.showAndWait();
    }

    private void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        pane.getStyleClass().add(Constants.STYLE_CUSTOM_DIALOG);
    }
}
