package br.com.safekey.ui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Caixa de seleção para o tema escuro.
 *
 * <p>Continua sendo um {@link JCheckBox} (marcar, desmarcar, {@code isSelected}, teclado). Só
 * trocamos os ícones padrão, que ficam claros demais sobre fundo escuro, por ícones desenhados
 * com Java 2D: um quadrado arredondado azul com "check" quando marcado.</p>
 */
@SuppressWarnings("serial")
public class DarkCheckBox extends JCheckBox {

    public DarkCheckBox(String text, boolean selected) {
        super(text, selected);
        setOpaque(false);
        setFocusPainted(false);
        setFont(Theme.uiFont(Font.PLAIN, 14));
        setForeground(Theme.TEXT);
        setIconTextGap(10);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIcon(new BoxIcon(false));
        setSelectedIcon(new BoxIcon(true));
    }

    /** Desenha o quadradinho da caixa de seleção, marcado ou não. */
    private static final class BoxIcon implements Icon {

        private static final int SIZE = 18;
        private static final int ARC = 7;

        private final boolean checked;

        BoxIcon(boolean checked) {
            this.checked = checked;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            if (checked) {
                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(x, y, SIZE, SIZE, ARC, ARC);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(new int[] {x + 4, x + 8, x + 14}, new int[] {y + 9, y + 13, y + 5}, 3);
            } else {
                g2.setColor(Theme.FIELD);
                g2.fillRoundRect(x, y, SIZE, SIZE, ARC, ARC);
                g2.setColor(Theme.BORDER_STRONG);
                g2.drawRoundRect(x, y, SIZE - 1, SIZE - 1, ARC, ARC);
            }

            // Contorno claro para quem navega pelo teclado.
            if (c != null && c.hasFocus()) {
                g2.setColor(Theme.FOCUS_RING);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x + 1, y + 1, SIZE - 3, SIZE - 3, ARC - 1, ARC - 1);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
