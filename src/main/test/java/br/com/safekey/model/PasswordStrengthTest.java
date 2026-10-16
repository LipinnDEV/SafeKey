package br.com.safekey.model;

import br.com.safekey.model.PasswordStrength.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordStrengthTest {

    private static final double DELTA = 1e-9;

    private static PasswordStrength estimate(int length, int charsetSize) {
        return PasswordStrength.estimate(length, charsetSize);
    }

    @Test
    void entropyIsLengthTimesLog2OfTheCharsetSize() {
        // 8 caracteres de um conjunto de 256 símbolos: 8 x 8 bits = 64 bits.
        assertEquals(64.0, estimate(8, 256).getEntropyBits(), DELTA);

        // 10 dígitos: 10 x log2(10) bits.
        double expected = 10 * Math.log(10) / Math.log(2);
        assertEquals(expected, estimate(10, 10).getEntropyBits(), DELTA);
    }

    @Test
    void doublingTheLengthDoublesTheEntropy() {
        double shorter = estimate(10, 62).getEntropyBits();
        double longer = estimate(20, 62).getEntropyBits();
        assertEquals(2 * shorter, longer, DELTA);
    }

    @Test
    void levelChangesAtTheDocumentedThresholds() {
        // Com 2 símbolos cada caractere vale exatamente 1 bit: o comprimento é a própria entropia.
        assertEquals(Level.WEAK, estimate(39, 2).getLevel());
        assertEquals(Level.REASONABLE, estimate(40, 2).getLevel());
        assertEquals(Level.REASONABLE, estimate(59, 2).getLevel());
        assertEquals(Level.STRONG, estimate(60, 2).getLevel());
        assertEquals(Level.STRONG, estimate(99, 2).getLevel());
        assertEquals(Level.VERY_STRONG, estimate(100, 2).getLevel());
    }

    @Test
    void classifiesTypicalConfigurations() {
        assertEquals(Level.WEAK, estimate(4, 10).getLevel());          // PIN de 4 dígitos
        assertEquals(Level.REASONABLE, estimate(8, 62).getLevel());    // 8 letras e números
        assertEquals(Level.STRONG, estimate(12, 87).getLevel());       // 12 com todos os tipos
        assertEquals(Level.VERY_STRONG, estimate(16, 87).getLevel());  // 16 com todos os tipos
    }

    @Test
    void levelsHaveLabelsInPortuguese() {
        assertEquals("Fraca", Level.WEAK.getLabel());
        assertEquals("Razoável", Level.REASONABLE.getLabel());
        assertEquals("Forte", Level.STRONG.getLabel());
        assertEquals("Muito forte", Level.VERY_STRONG.getLabel());
    }

    @Test
    void barFractionStaysBetweenZeroAndOne() {
        assertTrue(estimate(1, 2).getBarFraction() > 0.0);
        assertEquals(0.5, estimate(64, 2).getBarFraction(), DELTA);  // 64 de 128 bits
        assertEquals(1.0, estimate(128, 2).getBarFraction(), DELTA);
        assertEquals(1.0, estimate(1000, 94).getBarFraction(), DELTA);
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> estimate(0, 10));
        assertThrows(IllegalArgumentException.class, () -> estimate(-3, 10));
        assertThrows(IllegalArgumentException.class, () -> estimate(8, 0));
    }
}
