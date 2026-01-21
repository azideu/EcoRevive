package EcoRevive;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.util.HashMap;
import java.util.Map;

public class FXDashboardPanel extends BorderPane {
    private RecyclingManager manager;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;
    
    private Label totalItemsLabel;
    private Label totalWeightLabel;
    private Label pendingLabel;
    private Label ecoPointsLabel;

    public FXDashboardPanel(RecyclingManager manager) {
        this.manager = manager;
        initUI();
        updateCharts();
    }

    private void initUI() {
        Label titleLabel = new Label("Recycling Statistics Dashboard");
        titleLabel.getStyleClass().add("header-label");
        
        HBox topBox = new HBox(titleLabel);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));
        
        // Summary Cards
        HBox summaryBox = new HBox(20);
        summaryBox.setAlignment(Pos.CENTER);
        
        VBox card1 = createSummaryCard("Total Items", "0", "card-blue");
        totalItemsLabel = (Label) card1.getChildren().get(1);
        
        VBox card2 = createSummaryCard("Total Weight", "0kg", "card-orange");
        totalWeightLabel = (Label) card2.getChildren().get(1);
        
        VBox card3 = createSummaryCard("Pending Requests", "0", "card-purple");
        pendingLabel = (Label) card3.getChildren().get(1);

        VBox card4 = createSummaryCard("Eco-Points", "0", "card-green");
        ecoPointsLabel = (Label) card4.getChildren().get(1);
        
        summaryBox.getChildren().addAll(card1, card2, card3, card4);

        // Pie Chart
        pieChart = new PieChart();
        pieChart.setTitle("Category Distribution");
        pieChart.setLegendVisible(true);
        VBox pieContainer = new VBox(pieChart);
        pieContainer.getStyleClass().add("card");
        VBox.setVgrow(pieContainer, Priority.ALWAYS);

        // Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Condition");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");
        yAxis.setTickUnit(1);
        yAxis.setMinorTickVisible(false);
        yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() % 1 == 0) {
                    return String.format("%.0f", object);
                }
                return "";
            }
            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        });

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Condition Breakdown");
        barChart.setLegendVisible(false);
        VBox barContainer = new VBox(barChart);
        barContainer.getStyleClass().add("card");
        VBox.setVgrow(barContainer, Priority.ALWAYS);

        HBox chartsBox = new HBox(20, pieContainer, barContainer);
        chartsBox.setAlignment(Pos.CENTER);
        chartsBox.setPadding(new Insets(20, 0, 0, 0));
        
        // Make charts resize
        pieContainer.prefWidthProperty().bind(widthProperty().divide(2));
        barContainer.prefWidthProperty().bind(widthProperty().divide(2));
        
        VBox centerContent = new VBox(summaryBox, chartsBox);
        setCenter(centerContent);
        setTop(topBox);
        setPadding(new Insets(20));
    }
    
    private VBox createSummaryCard(String title, String initialValue, String colorClass) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("summary-card", colorClass);
        card.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("summary-card-title");
        
        Label valueLbl = new Label(initialValue);
        valueLbl.getStyleClass().add("summary-card-value");
        
        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }

    public void updateCharts() {
        // Update Summary Cards
        int totalItems = manager.getRecycledItems().size();
        double totalWeight = 0;
        for (EWasteItem item : manager.getRecycledItems()) {
            totalWeight += item.getWeight();
        }
        int pendingCount = manager.getPendingItems().size();
        int ecoPoints = manager.calculateEcoPoints();
        
        if (totalItemsLabel != null) totalItemsLabel.setText(String.valueOf(totalItems));
        if (totalWeightLabel != null) totalWeightLabel.setText(String.format("%.1f kg", totalWeight));
        if (pendingLabel != null) pendingLabel.setText(String.valueOf(pendingCount));
        if (ecoPointsLabel != null) ecoPointsLabel.setText(String.valueOf(ecoPoints));

        // Update Pie Chart
        Map<String, Integer> categoryCount = new HashMap<>();
        for (EWasteItem item : manager.getRecycledItems()) {
            categoryCount.put(item.getCategory(), categoryCount.getOrDefault(item.getCategory(), 0) + 1);
        }

        pieChart.getData().clear();
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Update Bar Chart
        Map<String, Integer> conditionCount = new HashMap<>();
        for (EWasteItem item : manager.getRecycledItems()) {
            conditionCount.put(item.getCondition(), conditionCount.getOrDefault(item.getCondition(), 0) + 1);
        }

        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : conditionCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
    }
}
