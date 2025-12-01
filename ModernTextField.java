package EcoRevive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernTextField extends JTextField {
    private Color backgroundColor = new Color(80, 80, 80);
    private Color textColor = new Color(240, 240, 240);

    public ModernTextField(int columns) {
        super(columns);
        setOpaque(false);
        setForeground(textColor);
        setCaretColor(textColor);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBorder(new EmptyBorder(6, 10, 6, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

        super.paintComponent(g);
        g2.dispose();
    }
}
