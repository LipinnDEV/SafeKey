package br.com.safekey.service;

import br.com.safekey.model.PasswordOptions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    // A geração é aleatória, então cada regra é conferida em muitas senhas.
    private static final int REPETITIONS = 300;

    private final PasswordGenerator generator = new PasswordGenerator();

    private static PasswordOptions allTypes(int length) {
        return new PasswordOptions(length, true, true, true, true, false);
    }

    private static boolean onlyContains(String password, String allowed) {
        return password.chars().allMatch(c -> allowed.indexOf(c) >= 0);
    }

    private static boolean containsAnyOf(String password, String characters) {
        return password.chars().anyMatch(c -> characters.indexOf(c) >= 0);
    }

    @Test
    void generatesPasswordWithRequestedLength() {
        for (int length : new int[] {4, 5, 16, 64, 128}) {
            assertEquals(length, generator.generate(allTypes(length)).length());
        }
    }

    @Test
    void acceptsMinimumAndMaximumLength() {
        assertEquals(PasswordGenerator.MIN_LENGTH,
                generator.generate(allTypes(PasswordGenerator.MIN_LENGTH)).length());
        assertEquals(PasswordGenerator.MAX_LENGTH,
                generator.generate(allTypes(PasswordGenerator.MAX_LENGTH)).length());
    }

    @Test
    void respectsEveryCombinationOfCharacterTypes() {
        // Cada bit de "mask" liga um tipo: 1 = minúsculas, 2 = maiúsculas, 4 = números, 8 = símbolos.
        for (int mask = 1; mask <= 15; mask++) {
            boolean lower = (mask & 1) != 0;
            boolean upper = (mask & 2) != 0;
            boolean digits = (mask & 4) != 0;
            boolean symbols = (mask & 8) != 0;

            StringBuilder allowed = new StringBuilder();
            if (lower) {
                allowed.append(PasswordGenerator.LOWERCASE);
            }
            if (upper) {
                allowed.append(PasswordGenerator.UPPERCASE);
            }
            if (digits) {
                allowed.append(PasswordGenerator.DIGITS);
            }
            if (symbols) {
                allowed.append(PasswordGenerator.SYMBOLS);
            }

            // Comprimento mínimo: o caso mais apertado para garantir um caractere de cada tipo.
            PasswordOptions options = new PasswordOptions(
                    PasswordGenerator.MIN_LENGTH, lower, upper, digits, symbols, false);

            for (int i = 0; i < 100; i++) {
                String password = generator.generate(options);
                assertTrue(onlyContains(password, allowed.toString()),
                        "Caractere fora dos tipos escolhidos: " + password);
                if (lower) {
                    assertTrue(containsAnyOf(password, PasswordGenerator.LOWERCASE), "Faltou minúscula: " + password);
                }
                if (upper) {
                    assertTrue(containsAnyOf(password, PasswordGenerator.UPPERCASE), "Faltou maiúscula: " + password);
                }
                if (digits) {
                    assertTrue(containsAnyOf(password, PasswordGenerator.DIGITS), "Faltou número: " + password);
                }
                if (symbols) {
                    assertTrue(containsAnyOf(password, PasswordGenerator.SYMBOLS), "Faltou símbolo: " + password);
                }
            }
        }
    }

    @Test
    void avoidsSimilarCharactersWhenRequested() {
        PasswordOptions options = new PasswordOptions(PasswordGenerator.MAX_LENGTH, true, true, true, true, true);
        for (int i = 0; i < REPETITIONS; i++) {
            String password = generator.generate(options);
            assertFalse(containsAnyOf(password, PasswordGenerator.SIMILAR_CHARACTERS),
                    "Contém caractere parecido: " + password);
        }
    }

    @Test
    void keepsEveryTypePresentWhenAvoidingSimilarCharacters() {
        PasswordOptions options = new PasswordOptions(PasswordGenerator.MIN_LENGTH, true, true, true, true, true);
        for (int i = 0; i < REPETITIONS; i++) {
            String password = generator.generate(options);
            assertTrue(containsAnyOf(password, PasswordGenerator.LOWERCASE), "Faltou minúscula: " + password);
            assertTrue(containsAnyOf(password, PasswordGenerator.UPPERCASE), "Faltou maiúscula: " + password);
            assertTrue(containsAnyOf(password, PasswordGenerator.DIGITS), "Faltou número: " + password);
            assertTrue(containsAnyOf(password, PasswordGenerator.SYMBOLS), "Faltou símbolo: " + password);
        }
    }

    @Test
    void charsetShrinksWhenAvoidingSimilarCharacters() {
        // Números sem 0 e 1; letras minúsculas sem l e o; maiúsculas sem I e O.
        assertEquals(10, generator.charsetSize(new PasswordOptions(8, false, false, true, false, false)));
        assertEquals(8, generator.charsetSize(new PasswordOptions(8, false, false, true, false, true)));
        assertEquals(26, generator.charsetSize(new PasswordOptions(8, true, false, false, false, false)));
        assertEquals(24, generator.charsetSize(new PasswordOptions(8, true, false, false, false, true)));
        assertEquals(26, generator.charsetSize(new PasswordOptions(8, false, true, false, false, false)));
        assertEquals(24, generator.charsetSize(new PasswordOptions(8, false, true, false, false, true)));
    }

    @Test
    void charsetSizeIsTheSumOfTheSelectedTypes() {
        int lower = generator.charsetSize(new PasswordOptions(8, true, false, false, false, false));
        int upper = generator.charsetSize(new PasswordOptions(8, false, true, false, false, false));
        int digits = generator.charsetSize(new PasswordOptions(8, false, false, true, false, false));
        int symbols = generator.charsetSize(new PasswordOptions(8, false, false, false, true, false));

        assertEquals(lower + upper + digits + symbols, generator.charsetSize(allTypes(8)));
    }

    @Test
    void rejectsSelectionWithoutCharacterTypes() {
        PasswordOptions none = new PasswordOptions(16, false, false, false, false, false);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> generator.generate(none));

        assertTrue(error.getMessage().contains("tipo de caractere"));
    }

    @Test
    void rejectsLengthOutsideTheLimits() {
        for (int length : new int[] {Integer.MIN_VALUE, -1, 0, 3, 129, 1000}) {
            PasswordOptions options = allTypes(length);
            assertThrows(IllegalArgumentException.class, () -> generator.generate(options));
            assertThrows(IllegalArgumentException.class, () -> generator.validate(options));
        }
    }

    @Test
    void generatesDifferentPasswordsEachTime() {
        Set<String> passwords = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            passwords.add(generator.generate(allTypes(32)));
        }
        assertEquals(100, passwords.size());
    }

    @Test
    void shufflesTheGuaranteedCharacters() {
        // Sem o embaralhamento, a primeira posição seria sempre uma minúscula (o primeiro tipo sorteado).
        PasswordOptions options = allTypes(PasswordGenerator.MIN_LENGTH);
        boolean lowercaseFirst = false;
        boolean otherTypeFirst = false;

        for (int i = 0; i < REPETITIONS && !(lowercaseFirst && otherTypeFirst); i++) {
            char first = generator.generate(options).charAt(0);
            if (PasswordGenerator.LOWERCASE.indexOf(first) >= 0) {
                lowercaseFirst = true;
            } else {
                otherTypeFirst = true;
            }
        }

        assertTrue(lowercaseFirst && otherTypeFirst,
                "A primeira posição não variou entre tipos de caractere.");
    }
}
