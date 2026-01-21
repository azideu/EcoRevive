package EcoRevive;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class EcoReviveFX extends Application {

    private RecyclingManager manager;
    private FileService fileService;
    private BorderPane mainLayout;
    private FXDashboardPanel dashboardPanel;

    @Override
    public void start(Stage primaryStage) {
        manager = new RecyclingManager();
        fileService = new FileService();
        manager.setRecycledItems(fileService.loadInventory());

        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add(Constants.STYLE_ROOT);

        // Navigation
        StackPane navBar = createNavBar();
        mainLayout.setTop(navBar);

        // Initial View
        showManagementPanel();

        Scene scene = new Scene(mainLayout, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle(Constants.APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            saveAndExit();
        });
        primaryStage.show();
    }

    private StackPane createNavBar() {
        StackPane nav = new StackPane();
        nav.setPadding(new Insets(15));
        nav.getStyleClass().add(Constants.STYLE_NAV_BAR);

        // Left: Branding
        Label brandLabel = new Label("EcoRevive");
        brandLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox leftBox = new HBox(brandLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPickOnBounds(false);

        // Center: Nav Buttons
        ToggleGroup group = new ToggleGroup();
        ToggleButton manageBtn = createNavButton("Manage Items", group);
        ToggleButton inventoryBtn = createNavButton("Inventory", group);
        ToggleButton statsBtn = createNavButton("Statistics", group);

        // Prevent deselection
        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            }
        });

        manageBtn.setSelected(true);
        manageBtn.setOnAction(e -> showManagementPanel());
        inventoryBtn.setOnAction(e -> showInventoryPanel());
        statsBtn.setOnAction(e -> showStatsPanel());

        HBox centerBox = new HBox(15, manageBtn, inventoryBtn, statsBtn);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPickOnBounds(false);

        // Right: Theme Button
        Button themeBtn = new Button("☀");
        themeBtn.setTooltip(new Tooltip("Toggle Light/Dark Mode"));
        themeBtn.setOnAction(e -> {
            if (mainLayout.getStyleClass().contains(Constants.STYLE_LIGHT_THEME)) {
                mainLayout.getStyleClass().remove(Constants.STYLE_LIGHT_THEME);
                themeBtn.setText("☀");
            } else {
                mainLayout.getStyleClass().add(Constants.STYLE_LIGHT_THEME);
                themeBtn.setText("☾");
            }
        });
        
        HBox rightBox = new HBox(themeBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPickOnBounds(false);

        nav.getChildren().addAll(leftBox, centerBox, rightBox);
        return nav;
    }

    private ToggleButton createNavButton(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefSize(160, 40);
        return btn;
    }

    private void showManagementPanel() {
        ManagementPanel panel = new ManagementPanel(manager);
        mainLayout.setCenter(panel);
    }

    private void showInventoryPanel() {
        InventoryPanel panel = new InventoryPanel(manager, fileService);
        mainLayout.setCenter(panel);
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

    private void saveAndExit() {
        fileService.saveInventory(manager.getRecycledItems());
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
