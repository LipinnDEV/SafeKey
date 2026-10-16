package br.com.safekey.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * Painel com cantos arredondados, usado como "card" e como moldura de campos.
 *
 * <p>O painel é transparente ({@code setOpaque(false)}) e desenha o próprio fundo, assim os
 * cantos deixam aparecer a cor do componente que está atrás.</p>
 */
@SuppressWarnings("serial")
public class RoundedPanel extends JPanel {

    private final int arc;
    private final Color fill;
    private final Color outline;

    public RoundedPanel(LayoutManager layout, int arc, Color fill, Color outline) {
        super(layout);
        this.arc = arc;
        this.fill = fill;
        this.outline = outline;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.setColor(outline);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
    }
}
