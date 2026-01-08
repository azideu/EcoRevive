package EcoRevive;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.util.HashMap;
import java.util.Map;

public class FXDashboardPanel extends BorderPane {
    private RecyclingManager manager;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

    public FXDashboardPanel(RecyclingManager manager) {
        this.manager = manager;
        initUI();
        updateCharts();
    }

    private void initUI() {
        Label titleLabel = new Label("Recycling Statistics Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 20px;");
        
        HBox topBox = new HBox(titleLabel);
        topBox.setStyle("-fx-alignment: center;");
        setTop(topBox);

        // Pie Chart
        pieChart = new PieChart();
        pieChart.setTitle("Category Distribution");
        pieChart.setLegendVisible(true);

        // Bar Chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Condition");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Condition Breakdown");
        barChart.setLegendVisible(false);

        HBox chartsBox = new HBox(20, pieChart, barChart);
        chartsBox.setStyle("-fx-alignment: center; -fx-padding: 20px;");
        // Make charts resize
        pieChart.prefWidthProperty().bind(widthProperty().divide(2));
        barChart.prefWidthProperty().bind(widthProperty().divide(2));

        setCenter(chartsBox);
    }

    public void updateCharts() {
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
