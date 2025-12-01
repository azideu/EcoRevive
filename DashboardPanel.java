package EcoRevive;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class DashboardPanel extends JPanel {
    private RecyclingManager manager;
    
    // Colors for charts
    private final Color[] colors = {
        new Color(75, 110, 175), // Blue
        new Color(175, 75, 75),  // Red
        new Color(75, 175, 110), // Green
        new Color(175, 175, 75), // Yellow
        new Color(110, 75, 175), // Purple
        new Color(75, 175, 175)  // Cyan
    };

    public DashboardPanel(RecyclingManager manager) {
        this.manager = manager;
        setBackground(new Color(30, 30, 30)); // Match dark theme
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Title
        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String title = "Recycling Statistics Dashboard";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 30);

        // Draw Charts
        drawPieChart(g2, 50, 60, width / 2 - 60, height - 100);
        drawBarChart(g2, width / 2 + 10, 60, width / 2 - 60, height - 100);
    }

    private void drawPieChart(Graphics2D g2, int x, int y, int w, int h) {
        // Data: Category Distribution
        Map<String, Integer> categoryCount = new HashMap<>();
        LinkedList<EWasteItem> items = manager.getRecycledItems();
        int total = items.size();

        for (EWasteItem item : items) {
            categoryCount.put(item.getCategory(), categoryCount.getOrDefault(item.getCategory(), 0) + 1);
        }

        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("Category Distribution", x + w/2 - 80, y - 10);

        if (total == 0) {
            g2.setColor(Color.GRAY);
            g2.drawString("No Data", x + w/2 - 30, y + h/2);
            return;
        }

        int legendWidth = 140; // Reserved width for legend
        int pieSize = Math.min(w - legendWidth, h) - 20;
        
        // Ensure pie isn't too small or negative
        if (pieSize < 50) pieSize = 50;
        
        int pieX = x + 10;
        int pieY = y + (h - pieSize) / 2; // Center vertically

        double startAngle = 90;
        int colorIndex = 0;
        int legendY = y + 20;

        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            double amount = entry.getValue();
            double angle = (amount / total) * 360;

            g2.setColor(colors[colorIndex % colors.length]);
            g2.fill(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, -angle, Arc2D.PIE));

            // Legend
            int legendX = x + w - legendWidth + 10;
            g2.fillRect(legendX, legendY, 10, 10);
            g2.setColor(new Color(240, 240, 240));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString(entry.getKey() + " (" + (int)amount + ")", legendX + 15, legendY + 10);
            
            startAngle -= angle;
            colorIndex++;
            legendY += 20;
        }
    }

    private void drawBarChart(Graphics2D g2, int x, int y, int w, int h) {
        // Data: Condition Breakdown
        Map<String, Integer> conditionCount = new HashMap<>();
        LinkedList<EWasteItem> items = manager.getRecycledItems();
        
        for (EWasteItem item : items) {
            conditionCount.put(item.getCondition(), conditionCount.getOrDefault(item.getCondition(), 0) + 1);
        }

        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("Condition Breakdown", x + w/2 - 80, y - 10);

        if (items.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("No Data", x + w/2 - 30, y + h/2);
            return;
        }

        int maxCount = 0;
        for (int count : conditionCount.values()) {
            if (count > maxCount) maxCount = count;
        }

        int barWidth = (w - 40) / (conditionCount.size() > 0 ? conditionCount.size() : 1);
        int chartHeight = h - 40;
        int startX = x + 20;
        int startY = y + chartHeight + 20;

        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : conditionCount.entrySet()) {
            int count = entry.getValue();
            int barHeight = (int) ((double) count / maxCount * chartHeight);

            g2.setColor(colors[colorIndex % colors.length]);
            g2.fill(new Rectangle2D.Double(startX, startY - barHeight, barWidth - 10, barHeight));

            // Label
            g2.setColor(new Color(240, 240, 240));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String label = entry.getKey();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, startX + (barWidth - 10 - fm.stringWidth(label))/2, startY + 15);
            g2.drawString(String.valueOf(count), startX + (barWidth - 10 - fm.stringWidth(String.valueOf(count)))/2, startY - barHeight - 5);

            startX += barWidth;
            colorIndex++;
        }
    }
}
