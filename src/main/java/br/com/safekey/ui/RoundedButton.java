package br.com.safekey.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Botão azul com cantos arredondados.
 *
 * <p>Estende {@link JButton}, então continua funcionando com {@code addActionListener},
 * teclado e {@code setEnabled}. Só o desenho foi substituído: fundo arredondado, cor que muda
 * ao passar o mouse ou clicar, contorno de foco para quem navega pelo teclado e texto centralizado.</p>
 */
@SuppressWarnings("serial")
public class RoundedButton extends JButton {

    private static final int ARC = 14;

    public RoundedButton(String text) {
        super(text);
        setFont(Theme.uiFont(Font.BOLD, 14));
        setForeground(Color.WHITE);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(currentColor());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        if (isFocusOwner()) {
            g2.setColor(Theme.FOCUS_RING);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, ARC, ARC);
        }

        g2.setFont(getFont());
        g2.setColor(isEnabled() ? getForeground() : Theme.TEXT_MUTED);
        FontMetrics metrics = g2.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
    }

    private Color currentColor() {
        if (!isEnabled()) {
            return Theme.DISABLED;
        }
        if (getModel().isPressed()) {
            return Theme.PRIMARY_PRESSED;
        }
        if (getModel().isRollover()) {
            return Theme.PRIMARY_HOVER;
        }
        return Theme.PRIMARY;
    }
}
