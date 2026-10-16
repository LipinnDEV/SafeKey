package br.com.safekey.ui;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

/**
 * Aparência do {@link JSlider} no tema escuro.
 *
 * <p>No Swing, o desenho de cada componente fica em uma classe "UI". Aqui reaproveitamos a
 * lógica de {@link BasicSliderUI} (arrastar, teclado, valores) e trocamos apenas o desenho da
 * trilha e do botão deslizante. Também fazemos o clique na trilha levar o botão direto até o
 * ponto clicado, em vez do comportamento padrão de andar aos poucos.</p>
 */
public class DarkSliderUI extends BasicSliderUI {

    private static final int TRACK_HEIGHT = 6;
    private static final int THUMB_SIZE = 18;

    public DarkSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;
        g2.setColor(Theme.TRACK);
        g2.fillRoundRect(trackRect.x, y, trackRect.width, TRACK_HEIGHT, TRACK_HEIGHT, TRACK_HEIGHT);

        // Trecho já percorrido, em azul, até o centro do botão.
        int filledWidth = thumbRect.x + thumbRect.width / 2 - trackRect.x;
        g2.setColor(Theme.PRIMARY);
        g2.fillRoundRect(trackRect.x, y, filledWidth, TRACK_HEIGHT, TRACK_HEIGHT, TRACK_HEIGHT);
        g2.dispose();
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(java.awt.Color.WHITE);
        g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        g2.setColor(Theme.PRIMARY);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(thumbRect.x + 1, thumbRect.y + 1, thumbRect.width - 3, thumbRect.height - 3);
        g2.dispose();
    }

    @Override
    public void paintFocus(Graphics g) {
        // Sem o retângulo tracejado padrão: o botão já fica destacado ao receber o foco.
    }

    @Override
    protected TrackListener createTrackListener(JSlider target) {
        return new TrackListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (target.isEnabled()) {
                    target.setValue(valueForXPosition(e.getX()));
                }
                super.mousePressed(e);
            }

            @Override
            public boolean shouldScroll(int direction) {
                return false;
            }
        };
    }
}
