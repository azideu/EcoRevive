package EcoRevive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    private Color hoverColor = new Color(100, 149, 237);  // Cornflower blue
    private Color normalColor = new Color(70, 130, 180);  // Steel blue
    private Color pressedColor = new Color(65, 105, 225); // Royal blue
    private Color shadowColor = new Color(30, 60, 100, 100);
    private boolean isHovered = false;
    private float shadowOpacity = 0.3f;
    private float glowOpacity = 0f;

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 20, 10, 20));

        // Add smooth hover animation
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                startHoverAnimation();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                startExitAnimation();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                shadowOpacity = 0.1f;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                shadowOpacity = 0.3f;
                repaint();
            }
        });
    }

    private void startHoverAnimation() {
        new Thread(() -> {
            for (float i = 0; i <= 0.5f; i += 0.05f) {
                glowOpacity = i;
                repaint();
                try { Thread.sleep(10); } catch (Exception e) {}
            }
        }).start();
    }

    private void startExitAnimation() {
        new Thread(() -> {
            for (float i = glowOpacity; i >= 0f; i -= 0.05f) {
                glowOpacity = i;
                repaint();
                try { Thread.sleep(10); } catch (Exception e) {}
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int arc = 20;

        // Draw shadow
        if (shadowOpacity > 0) {
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(),
                    shadowColor.getBlue(), (int)(shadowOpacity * 255)));
            g2.fill(new RoundRectangle2D.Float(2, 3, width - 4, height - 4, arc, arc));
        }

        // Draw main button with gradient
        GradientPaint gradient;
        if (getModel().isPressed()) {
            gradient = new GradientPaint(0, 0, pressedColor.darker(),
                    0, height, pressedColor);
        } else if (isHovered) {
            gradient = new GradientPaint(0, 0, hoverColor.brighter(),
                    0, height, hoverColor);
        } else {
            gradient = new GradientPaint(0, 0, normalColor.brighter(),
                    0, height, normalColor);
        }

        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc));

        // Draw glow effect on hover
        if (glowOpacity > 0) {
            g2.setColor(new Color(100, 200, 255, (int)(glowOpacity * 100)));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, width - 3, height - 3, arc, arc));
        }

        // Draw border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc));

        // Draw text with shadow
        FontMetrics fm = g2.getFontMetrics();
        Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();

        int textX = (width - stringBounds.width) / 2;
        int textY = height / 2 + fm.getAscent() / 2 - 2;

        // Text shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(getText(), textX + 1, textY + 1);

        // Main text
        g2.setColor(getForeground());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        return new Dimension(Math.max(dim.width, 120), Math.max(dim.height, 45));
    }
}