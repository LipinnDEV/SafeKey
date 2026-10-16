package br.com.safekey.model;

/**
 * Configurações escolhidas pelo usuário para gerar uma senha.
 *
 * <p>É um {@code record}: um objeto imutável que apenas carrega dados. Agrupar as seis opções
 * em um único objeto evita métodos com muitos parâmetros e facilita os testes. A validação dos
 * valores fica em {@code PasswordGenerator}.</p>
 *
 * @param length       quantidade de caracteres da senha
 * @param useLowercase inclui letras minúsculas (a-z)
 * @param useUppercase inclui letras maiúsculas (A-Z)
 * @param useDigits    inclui números (0-9)
 * @param useSymbols   inclui símbolos especiais
 * @param avoidSimilar remove caracteres visualmente parecidos (como O e 0)
 */
public record PasswordOptions(
        int length,
        boolean useLowercase,
        boolean useUppercase,
        boolean useDigits,
        boolean useSymbols,
        boolean avoidSimilar) {

    /** Indica se pelo menos um tipo de caractere foi selecionado. */
    public boolean hasAnyCharacterType() {
        return useLowercase || useUppercase || useDigits || useSymbols;
    }
}
