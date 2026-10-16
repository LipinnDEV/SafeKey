package br.com.safekey.ui;

import br.com.safekey.model.PasswordStrength;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Cores, fontes e ajustes globais do visual escuro do SafeKey.
 *
 * <p>O Swing não traz um tema escuro moderno e o projeto não usa bibliotecas externas, então
 * os componentes do pacote {@code ui} são desenhados à mão com estas cores. Concentrar tudo
 * aqui permite trocar a identidade visual alterando um único arquivo.</p>
 */
public final class Theme {

    private Theme() {
    }

    // Fundos, do mais escuro (janela) ao mais claro (painéis).
    public static final Color BACKGROUND = new Color(0x0E1014);
    public static final Color SURFACE = new Color(0x171A21);
    public static final Color FIELD = new Color(0x0F1217);
    public static final Color BORDER = new Color(0x2A2F3A);
    public static final Color BORDER_STRONG = new Color(0x4A5163);
    public static final Color TRACK = new Color(0x262B35);

    // Texto.
    public static final Color TEXT = new Color(0xE8EAF0);
    public static final Color TEXT_MUTED = new Color(0x8B93A4);

    // Azul: cor principal dos botões e controles.
    public static final Color PRIMARY = new Color(0x3B82F6);
    public static final Color PRIMARY_HOVER = new Color(0x5B9BFA);
    public static final Color PRIMARY_PRESSED = new Color(0x2563EB);
    public static final Color FOCUS_RING = new Color(0x93C5FD);
    public static final Color DISABLED = new Color(0x262B35);

    // Resultados: verde para bons, amarelo para atenção, vermelho para alertas.
    public static final Color SUCCESS = new Color(0x22C55E);
    public static final Color GOOD = new Color(0x84CC16);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color DANGER = new Color(0xEF4444);

    private static final Set<String> INSTALLED_FONTS = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));

    // Usa a primeira fonte instalada da lista; a última é uma fonte lógica do Java, sempre disponível.
    private static final String UI_FAMILY = firstInstalled(
            "Segoe UI", "Helvetica Neue", "Ubuntu", "Noto Sans", "DejaVu Sans", Font.SANS_SERIF);
    private static final String MONO_FAMILY = firstInstalled(
            "JetBrains Mono", "Consolas", "Menlo", "DejaVu Sans Mono", "Liberation Mono", Font.MONOSPACED);

    private static String firstInstalled(String... candidates) {
        for (String candidate : candidates) {
            if (INSTALLED_FONTS.contains(candidate)) {
                return candidate;
            }
        }
        return candidates[candidates.length - 1];
    }

    /** Fonte para textos da interface. */
    public static Font uiFont(int style, int size) {
        return new Font(UI_FAMILY, style, size);
    }

    /** Fonte de largura fixa, usada na senha para diferenciar caracteres parecidos. */
    public static Font monoFont(int style, int size) {
        return new Font(MONO_FAMILY, style, size);
    }

    /** Cor associada a cada nível de força. */
    public static Color strengthColor(PasswordStrength.Level level) {
        return switch (level) {
            case WEAK -> DANGER;
            case REASONABLE -> WARNING;
            case STRONG -> GOOD;
            case VERY_STRONG -> SUCCESS;
        };
    }

    /** Aplica o visual padrão do Swing (igual em todos os sistemas) e ajusta as dicas de ferramenta. */
    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            System.err.println("Não foi possível aplicar o visual padrão do Swing: " + e.getMessage());
        }
        UIManager.put("ToolTip.background", TRACK);
        UIManager.put("ToolTip.foreground", TEXT);
        UIManager.put("ToolTip.font", uiFont(Font.PLAIN, 12));
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_STRONG),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)));
    }
}
