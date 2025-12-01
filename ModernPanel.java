package EcoRevive;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernPanel extends JPanel {
    private int cornerRadius = 20;
    private Color backgroundColor = new Color(35, 35, 35);
    private Color shadowColor = new Color(0, 0, 0, 80);
    private int shadowSize = 5;

    public ModernPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
    }

    public ModernPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize, shadowSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - (shadowSize * 2);
        int height = getHeight() - (shadowSize * 2);
        int x = shadowSize;
        int y = shadowSize;

        // Draw Shadow
        g2.setColor(shadowColor);
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 2, width, height, cornerRadius, cornerRadius));

        // Draw Panel
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(x, y, width, height, cornerRadius, cornerRadius));

        g2.dispose();
        super.paintComponent(g);
    }
}
