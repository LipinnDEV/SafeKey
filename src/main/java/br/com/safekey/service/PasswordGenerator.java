package br.com.safekey.service;

import br.com.safekey.model.PasswordOptions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Valida as configurações e gera senhas aleatórias.
 *
 * <p>Toda a aleatoriedade vem de {@link SecureRandom}, que é um gerador criptograficamente
 * seguro. {@code java.util.Random} não serve para senhas: sua sequência pode ser prevista
 * a partir de poucos valores observados.</p>
 *
 * <p>A classe não guarda nenhuma senha: cada chamada a {@link #generate(PasswordOptions)}
 * devolve um valor novo e esquece o anterior.</p>
 */
public class PasswordGenerator {

    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 128;

    // Visíveis ao pacote para que os testes consigam conferir o resultado.
    static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String DIGITS = "0123456789";
    static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?/|";

    // Caracteres que se confundem entre si em muitas fontes: O/0/o, I/l/1 e |.
    static final String SIMILAR_CHARACTERS = "O0oIl1|";

    private final SecureRandom random = new SecureRandom();

    /**
     * Confere se as configurações permitem gerar uma senha.
     *
     * @throws IllegalArgumentException com uma mensagem pronta para o usuário
     */
    public void validate(PasswordOptions options) {
        Objects.requireNonNull(options, "options");
        if (options.length() < MIN_LENGTH || options.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "O comprimento deve estar entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres.");
        }
        if (!options.hasAnyCharacterType()) {
            throw new IllegalArgumentException("Selecione ao menos um tipo de caractere.");
        }
    }

    /**
     * Gera uma senha que respeita as configurações.
     *
     * <ol>
     *   <li>Sorteia um caractere de cada tipo selecionado, para que todos apareçam
     *       (quando o comprimento comporta isso).</li>
     *   <li>Completa o restante sorteando de todos os tipos selecionados juntos.</li>
     *   <li>Embaralha tudo, para que os caracteres garantidos não fiquem sempre no começo.</li>
     * </ol>
     *
     * @throws IllegalArgumentException se as configurações forem inválidas
     */
    public String generate(PasswordOptions options) {
        validate(options);

        List<String> alphabets = selectedAlphabets(options);
        String allCharacters = String.join("", alphabets);

        char[] password = new char[options.length()];
        int filled = 0;

        // Com o comprimento mínimo de 4 sempre há espaço para os 4 tipos possíveis,
        // mas a verificação mantém o código correto se esses limites mudarem.
        if (password.length >= alphabets.size()) {
            for (String alphabet : alphabets) {
                password[filled++] = randomCharFrom(alphabet);
            }
        }
        while (filled < password.length) {
            password[filled++] = randomCharFrom(allCharacters);
        }

        shuffle(password);
        return new String(password);
    }

    /**
     * Quantidade de caracteres distintos que as configurações permitem sortear.
     * Os tipos não compartilham caracteres, então basta somar os tamanhos.
     */
    public int charsetSize(PasswordOptions options) {
        return selectedAlphabets(options).stream().mapToInt(String::length).sum();
    }

    /** Monta o alfabeto de cada tipo selecionado, já sem os caracteres parecidos se for o caso. */
    private List<String> selectedAlphabets(PasswordOptions options) {
        List<String> alphabets = new ArrayList<>();
        if (options.useLowercase()) {
            alphabets.add(prepare(LOWERCASE, options));
        }
        if (options.useUppercase()) {
            alphabets.add(prepare(UPPERCASE, options));
        }
        if (options.useDigits()) {
            alphabets.add(prepare(DIGITS, options));
        }
        if (options.useSymbols()) {
            alphabets.add(prepare(SYMBOLS, options));
        }
        return alphabets;
    }

    private static String prepare(String alphabet, PasswordOptions options) {
        return options.avoidSimilar() ? withoutSimilarCharacters(alphabet) : alphabet;
    }

    private static String withoutSimilarCharacters(String alphabet) {
        StringBuilder filtered = new StringBuilder();
        for (char c : alphabet.toCharArray()) {
            if (SIMILAR_CHARACTERS.indexOf(c) < 0) {
                filtered.append(c);
            }
        }
        return filtered.toString();
    }

    private char randomCharFrom(String alphabet) {
        // nextInt(bound) sorteia de forma uniforme, sem favorecer nenhum caractere.
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }

    /** Embaralhamento de Fisher-Yates: cada permutação tem a mesma probabilidade. */
    private void shuffle(char[] characters) {
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
    }
}
