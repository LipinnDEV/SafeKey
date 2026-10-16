package br.com.safekey.util;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Copia texto para a área de transferência do sistema usando apenas recursos padrão do Java.
 *
 * <p>Os erros viram exceções com mensagem pronta para o usuário, de modo que a interface
 * possa exibi-las sem encerrar o aplicativo.</p>
 */
public final class ClipboardUtil {

    private ClipboardUtil() {
    }

    /**
     * Copia o texto para a área de transferência.
     *
     * @throws IllegalArgumentException se não houver texto para copiar
     * @throws IllegalStateException    se a área de transferência não estiver acessível
     */
    public static void copy(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Não há senha para copiar. Gere uma senha primeiro.");
        }
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(text), null);
        } catch (HeadlessException | IllegalStateException | SecurityException e) {
            // Ocorre, por exemplo, quando outro programa está usando a área de transferência.
            throw new IllegalStateException(
                    "Não foi possível acessar a área de transferência. Tente copiar novamente.", e);
        }
    }
}
