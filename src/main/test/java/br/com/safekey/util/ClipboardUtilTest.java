package br.com.safekey.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A cópia em si depende do sistema operacional e não funciona em ambientes sem tela (como
 * servidores de integração contínua), então os testes cobrem apenas a validação, que acontece
 * antes de qualquer acesso à área de transferência.
 */
class ClipboardUtilTest {

    @Test
    void rejectsNullText() {
        assertThrows(IllegalArgumentException.class, () -> ClipboardUtil.copy(null));
    }

    @Test
    void rejectsEmptyText() {
        assertThrows(IllegalArgumentException.class, () -> ClipboardUtil.copy(""));
    }
}
