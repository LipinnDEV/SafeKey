package br.com.safekey.ui;

import javax.swing.JComponent;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Mostra a senha gerada em fonte de largura fixa.
 *
 * <p>Um campo de texto de uma linha esconderia parte de senhas longas (até 128 caracteres).
 * Este componente quebra o texto em até três linhas e, se preciso, diminui a fonte até a senha
 * inteira caber. A senha fica sempre visível por completo, centralizada na vertical, e o
 * tamanho do componente nunca muda, o que evita "pulos" na janela.</p>
 */
@SuppressWarnings("serial")
public class PasswordDisplay extends JComponent {

    private static final int MAX_LINES = 3;
    private static final int MAX_FONT_SIZE = 24;
    private static final int MIN_FONT_SIZE = 10;
    private static final String PLACEHOLDER = "Sua senha aparece aqui";

    private String text = "";

    public PasswordDisplay() {
        setOpaque(false);
    }

    /** Senha atualmente exibida (vazio se ainda não houver nenhuma). */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text == null ? "" : text;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (text.isEmpty()) {
            paintPlaceholder(g2);
        } else {
            paintPassword(g2);
        }
        g2.dispose();
    }

    private void paintPlaceholder(Graphics2D g2) {
        g2.setFont(Theme.uiFont(Font.PLAIN, 15));
        g2.setColor(Theme.TEXT_MUTED);
        FontMetrics metrics = g2.getFontMetrics();
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g2.drawString(PLACEHOLDER, 0, y);
    }

    private void paintPassword(Graphics2D g2) {
        Font font = null;
        FontMetrics metrics = null;
        int charsPerLine = 1;
        int lines = 1;

        // Procura a maior fonte em que a senha cabe em, no máximo, MAX_LINES linhas.
        for (int size = MAX_FONT_SIZE; size >= MIN_FONT_SIZE; size--) {
            font = Theme.monoFont(Font.BOLD, size);
            metrics = g2.getFontMetrics(font);
            charsPerLine = Math.max(1, getWidth() / metrics.charWidth('M'));
            lines = (text.length() + charsPerLine - 1) / charsPerLine;
            if (lines <= MAX_LINES) {
                break;
            }
        }

        g2.setFont(font);
        g2.setColor(Theme.TEXT);

        int lineHeight = metrics.getHeight();
        int firstBaseline = (getHeight() - lines * lineHeight) / 2 + metrics.getAscent();
        for (int i = 0; i < lines; i++) {
            int start = i * charsPerLine;
            String line = text.substring(start, Math.min(text.length(), start + charsPerLine));
            g2.drawString(line, 0, firstBaseline + i * lineHeight);
        }
    }
}
