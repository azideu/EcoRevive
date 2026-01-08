package EcoRevive;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class DashboardPanel extends JPanel {
    private EcoRevive.RecyclingManager manager;
    private float animationProgress = 0f;
    private Timer animationTimer;

    // Modern color palette with gradients
    private final Color[] colors = {
            new Color(94, 114, 228),   // Modern Blue
            new Color(255, 107, 129),  // Coral Red
            new Color(72, 219, 151),   // Mint Green
            new Color(255, 195, 113),  // Warm Yellow
            new Color(168, 85, 247),   // Purple
            new Color(59, 201, 219)    // Cyan
    };

    private final Color darkBg = new Color(18, 18, 18);
    private final Color cardBg = new Color(30, 30, 30);
    private final Color accentGlow = new Color(94, 114, 228, 40);

    public DashboardPanel(EcoRevive.RecyclingManager manager) {
        this.manager = manager;
        setBackground(darkBg);

        // Start animation when panel is displayed
        animationTimer = new Timer(20, e -> {
            if (animationProgress < 1f) {
                animationProgress += 0.02f;
                repaint();
            } else {
                animationTimer.stop();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Animated gradient background overlay
        GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(94, 114, 228, 8),
                width, height, new Color(168, 85, 247, 8)
        );
        g2.setPaint(bgGradient);
        g2.fillRect(0, 0, width, height);

        // Title with glow effect
        drawTitle(g2, width);

        // Statistics cards at top
        drawStatsCards(g2, width);

        // Draw Charts
        int chartY = 160;
        int chartHeight = height - chartY - 20;
        drawEnhancedPieChart(g2, 40, chartY, width / 2 - 60, chartHeight);
        drawEnhancedBarChart(g2, width / 2 + 20, chartY, width / 2 - 60, chartHeight);
    }

    private void drawTitle(Graphics2D g2, int width) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        String title = "Recycling Statistics Dashboard";
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(title)) / 2;
        int y = 45;

        // Glow effect
        g2.setColor(new Color(94, 114, 228, 100));
        g2.drawString(title, x, y + 2);

        // Main text with gradient
        GradientPaint titleGradient = new GradientPaint(
                x, y - 20, new Color(255, 255, 255),
                x + fm.stringWidth(title), y, new Color(200, 210, 255)
        );
        g2.setPaint(titleGradient);
        g2.drawString(title, x, y);
    }

    private void drawStatsCards(Graphics2D g2, int width) {
        LinkedList<EcoRevive.EWasteItem> items = manager.getRecycledItems();
        int totalItems = items.size();
        double totalWeight = items.stream().mapToDouble(EcoRevive.EWasteItem::getWeight).sum();
        int pendingItems = manager.getPendingItems().size();

        int cardWidth = 180;
        int cardHeight = 70;
        int cardY = 70;
        int spacing = 20;
        int totalWidth = (cardWidth * 3) + (spacing * 2);
        int startX = (width - totalWidth) / 2;

        // Card 1: Total Items
        drawStatCard(g2, startX, cardY, cardWidth, cardHeight,
                "Total Items", String.valueOf(totalItems), colors[0]);

        // Card 2: Total Weight
        drawStatCard(g2, startX + cardWidth + spacing, cardY, cardWidth, cardHeight,
                "Total Weight", String.format("%.1f kg", totalWeight), colors[2]);

        // Card 3: Pending
        drawStatCard(g2, startX + (cardWidth + spacing) * 2, cardY, cardWidth, cardHeight,
                "Pending", String.valueOf(pendingItems), colors[1]);
    }

    private void drawStatCard(Graphics2D g2, int x, int y, int w, int h,
                              String label, String value, Color accentColor) {
        // Animated scale
        float scale = Math.min(animationProgress * 1.2f, 1f);
        int scaledW = (int)(w * scale);
        int scaledH = (int)(h * scale);
        int offsetX = x + (w - scaledW) / 2;
        int offsetY = y + (h - scaledH) / 2;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fill(new RoundRectangle2D.Float(offsetX + 3, offsetY + 3, scaledW, scaledH, 15, 15));

        // Card background with gradient
        GradientPaint cardGradient = new GradientPaint(
                offsetX, offsetY, cardBg,
                offsetX, offsetY + scaledH, new Color(40, 40, 40)
        );
        g2.setPaint(cardGradient);
        g2.fill(new RoundRectangle2D.Float(offsetX, offsetY, scaledW, scaledH, 15, 15));

        // Accent bar on left
        g2.setColor(accentColor);
        g2.fill(new RoundRectangle2D.Float(offsetX, offsetY, 4, scaledH, 4, 4));

        // Glow effect
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                accentColor.getBlue(), 20));
        g2.fill(new RoundRectangle2D.Float(offsetX - 2, offsetY - 2, scaledW + 4, scaledH + 4, 17, 17));

        if (scale < 0.5f) return;

        // Label
        g2.setColor(new Color(160, 160, 160));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.drawString(label, offsetX + 15, offsetY + 25);

        // Value
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.drawString(value, offsetX + 15, offsetY + 52);
    }

    private void drawEnhancedPieChart(Graphics2D g2, int x, int y, int w, int h) {
        // Card background
        drawChartCard(g2, x - 20, y - 40, w + 40, h + 60);

        // Title
        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Category Distribution", x, y - 15);

        Map<String, Integer> categoryCount = new HashMap<>();
        LinkedList<EcoRevive.EWasteItem> items = manager.getRecycledItems();
        int total = items.size();

        for (EcoRevive.EWasteItem item : items) {
            categoryCount.put(item.getCategory(), categoryCount.getOrDefault(item.getCategory(), 0) + 1);
        }

        if (total == 0) {
            drawEmptyState(g2, x + w/2 - 60, y + h/2 - 20);
            return;
        }

        int legendWidth = 160;
        int pieSize = Math.min(w - legendWidth - 40, h - 60);
        if (pieSize < 50) pieSize = 50;

        int pieX = x + 20;
        int pieY = y + (h - 60 - pieSize) / 2;

        // Draw pie shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillOval(pieX + 5, pieY + 5, pieSize, pieSize);

        double startAngle = 90;
        int colorIndex = 0;
        int legendY = y + 20;

        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            double amount = entry.getValue();
            double angle = (amount / total) * 360 * animationProgress;

            // Draw slice with gradient
            Color sliceColor = colors[colorIndex % colors.length];
            Color sliceDark = new Color(
                    Math.max(0, sliceColor.getRed() - 40),
                    Math.max(0, sliceColor.getGreen() - 40),
                    Math.max(0, sliceColor.getBlue() - 40)
            );

            g2.setColor(sliceColor);
            g2.fill(new Arc2D.Double(pieX, pieY, pieSize, pieSize, startAngle, -angle, Arc2D.PIE));

            // Inner highlight
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fill(new Arc2D.Double(pieX + 2, pieY + 2, pieSize - 4, pieSize - 4,
                    startAngle, -angle * 0.3, Arc2D.PIE));

            // Legend with animated background
            int legendX = x + w - legendWidth + 20;
            if (animationProgress > 0.5f) {
                // Legend background
                g2.setColor(new Color(40, 40, 40, 150));
                g2.fill(new RoundRectangle2D.Float(legendX - 5, legendY - 12, legendWidth - 15, 20, 8, 8));

                // Color indicator
                g2.setColor(sliceColor);
                g2.fill(new RoundRectangle2D.Float(legendX, legendY - 8, 12, 12, 3, 3));

                // Text
                g2.setColor(new Color(240, 240, 240));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                String text = entry.getKey() + " (" + (int)amount + ")";
                g2.drawString(text, legendX + 18, legendY + 2);
            }

            startAngle -= angle / animationProgress;
            colorIndex++;
            legendY += 28;
        }

        // Center hole for donut effect
        g2.setColor(cardBg);
        int holeSize = pieSize / 3;
        g2.fillOval(pieX + (pieSize - holeSize) / 2, pieY + (pieSize - holeSize) / 2,
                holeSize, holeSize);

        // Center text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String centerText = String.valueOf(total);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(centerText,
                pieX + pieSize/2 - fm.stringWidth(centerText)/2,
                pieY + pieSize/2 + fm.getAscent()/2 - 10);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(180, 180, 180));
        g2.drawString("Total", pieX + pieSize/2 - 15, pieY + pieSize/2 + 8);
    }

    private void drawEnhancedBarChart(Graphics2D g2, int x, int y, int w, int h) {
        // Card background
        drawChartCard(g2, x - 20, y - 40, w + 40, h + 60);

        // Title
        g2.setColor(new Color(240, 240, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Condition Breakdown", x, y - 15);

        Map<String, Integer> conditionCount = new HashMap<>();
        LinkedList<EcoRevive.EWasteItem> items = manager.getRecycledItems();

        for (EcoRevive.EWasteItem item : items) {
            conditionCount.put(item.getCondition(), conditionCount.getOrDefault(item.getCondition(), 0) + 1);
        }

        if (items.isEmpty()) {
            drawEmptyState(g2, x + w/2 - 60, y + h/2 - 20);
            return;
        }

        int maxCount = conditionCount.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int barWidth = Math.min(50, Math.max((w - 120) / Math.max(conditionCount.size(), 1), 40));
        int chartHeight = h - 100;
        int barSpacing = 15;
        int totalBarsWidth = (barWidth * conditionCount.size()) + (barSpacing * (conditionCount.size() - 1));
        int startX = x + (w - totalBarsWidth) / 2;
        int startY = y + chartHeight + 20;

        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : conditionCount.entrySet()) {
            int count = entry.getValue();
            int barHeight = (int)((double) count / maxCount * chartHeight * animationProgress);

            Color barColor = colors[colorIndex % colors.length];

            // Bar shadow
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fill(new RoundRectangle2D.Double(startX + 3, startY - barHeight + 3,
                    barWidth, barHeight, 8, 8));

            // Bar with gradient
            GradientPaint barGradient = new GradientPaint(
                    startX, startY - barHeight, barColor.brighter(),
                    startX, startY, barColor
            );
            g2.setPaint(barGradient);
            g2.fill(new RoundRectangle2D.Double(startX, startY - barHeight,
                    barWidth, barHeight, 8, 8));

            // Highlight on top
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new RoundRectangle2D.Double(startX, startY - barHeight,
                    barWidth, 3, 8, 8));

            if (animationProgress > 0.7f) {
                // Count on top
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String countStr = String.valueOf(count);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(countStr,
                        startX + (barWidth - fm.stringWidth(countStr)) / 2,
                        startY - barHeight - 8);

                // Label below
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(200, 200, 200));
                String label = entry.getKey();
                g2.drawString(label,
                        startX + (barWidth - fm.stringWidth(label)) / 2,
                        startY + 20);
            }

            startX += barWidth + barSpacing;
            colorIndex++;
        }
    }

    private void drawChartCard(Graphics2D g2, int x, int y, int w, int h) {
        // Shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(new RoundRectangle2D.Float(x + 4, y + 4, w, h, 20, 20));

        // Card background
        g2.setColor(cardBg);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, 20, 20));

        // Subtle border
        g2.setColor(new Color(60, 60, 60));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(x, y, w, h, 20, 20));
    }

    private void drawEmptyState(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(100, 100, 100));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString("📊 No Data Available", x, y);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(new Color(120, 120, 120));
        g2.drawString("Start adding items to see statistics", x - 10, y + 20);
    }

    public void startAnimation() {
        animationProgress = 0f;
        animationTimer.start();
    }
}