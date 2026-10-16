package br.com.safekey.ui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Logotipo do SafeKey: um cadeado branco sobre um quadrado azul arredondado.
 *
 * <p>É desenhado com Java 2D em qualquer tamanho, então o projeto não precisa de arquivos de
 * imagem. Serve de ícone no cabeçalho ({@link Icon}) e na janela ({@link #toImage()}).</p>
 */
public class LogoIcon implements Icon {

    private final int size;

    public LogoIcon(int size) {
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);

        // Fundo azul com um leve degradê.
        g2.setPaint(new GradientPaint(0, 0, Theme.PRIMARY_HOVER, size, size, Theme.PRIMARY_PRESSED));
        int arc = Math.round(size * 0.30f);
        g2.fillRoundRect(0, 0, size, size, arc, arc);

        // Corpo do cadeado.
        g2.setColor(Color.WHITE);
        int bodyWidth = Math.round(size * 0.50f);
        int bodyHeight = Math.round(size * 0.36f);
        int bodyX = (size - bodyWidth) / 2;
        int bodyY = Math.round(size * 0.46f);
        int bodyArc = Math.round(size * 0.10f);
        g2.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, bodyArc, bodyArc);

        // Argola: meia circunferência no topo e duas hastes até o corpo.
        g2.setStroke(new BasicStroke(size * 0.085f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        int shackleWidth = Math.round(size * 0.30f);
        int shackleX = (size - shackleWidth) / 2;
        int shackleTop = Math.round(size * 0.20f);
        g2.drawArc(shackleX, shackleTop, shackleWidth, shackleWidth, 0, 180);
        int legTop = shackleTop + shackleWidth / 2;
        g2.drawLine(shackleX, legTop, shackleX, bodyY);
        g2.drawLine(shackleX + shackleWidth, legTop, shackleX + shackleWidth, bodyY);

        // Buraco da fechadura.
        g2.setColor(Theme.PRIMARY_PRESSED);
        int hole = Math.max(3, Math.round(size * 0.10f));
        g2.fillOval((size - hole) / 2, bodyY + (bodyHeight - hole) / 2, hole, hole);
        g2.dispose();
    }

    /** Gera uma imagem do logotipo, usada como ícone da janela. */
    public Image toImage() {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paintIcon(null, g2, 0, 0);
        g2.dispose();
        return image;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
