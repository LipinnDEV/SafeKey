package br.com.safekey.ui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Barra de progresso que mostra a força estimada da senha.
 *
 * <p>Recebe uma fração entre 0.0 e 1.0 e a cor do nível atual. O desenho é feito em
 * {@link #paintComponent(Graphics)}: primeiro a trilha cinza, depois o trecho colorido por cima.</p>
 */
@SuppressWarnings("serial")
public class StrengthBar extends JComponent {

    private static final int HEIGHT = 10;

    private double fraction;
    private Color color = Theme.TRACK;

    public StrengthBar() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, HEIGHT));
        setMinimumSize(new Dimension(40, HEIGHT));
    }

    /**
     * Atualiza a barra.
     *
     * @param fraction quanto da barra preencher, de 0.0 a 1.0 (valores fora disso são ajustados)
     * @param color    cor do trecho preenchido
     */
    public void setStrength(double fraction, Color color) {
        this.fraction = Math.max(0.0, Math.min(1.0, fraction));
        this.color = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Theme.TRACK);
        g2.fillRoundRect(0, 0, width, height, height, height);

        if (fraction > 0) {
            // Nunca menor que a altura, para o trecho colorido continuar com ponta redonda.
            int filled = Math.max(height, (int) Math.round(width * fraction));
            g2.setColor(color);
            g2.fillRoundRect(0, 0, filled, height, height, height);
        }
        g2.dispose();
    }
}
