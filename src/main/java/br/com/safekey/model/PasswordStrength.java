package br.com.safekey.model;

/**
 * Estimativa da força de uma senha gerada aleatoriamente.
 *
 * <p>A estimativa usa a entropia em bits:
 * {@code comprimento x log2(tamanho do conjunto de caracteres)}. Cada bit a mais dobra o número
 * de combinações possíveis.</p>
 *
 * <p><b>Importante:</b> a entropia é uma estimativa matemática, não uma garantia absoluta de
 * segurança. O cálculo assume que cada caractere foi sorteado de forma uniforme e independente
 * dentro do conjunto escolhido. Ele simplifica a análise e não avalia outros fatores que
 * importam na prática, como reutilizar a senha em vários serviços, vazamentos de dados,
 * phishing, malware ou a forma como o serviço guarda a senha. A estimativa também fica
 * levemente otimista, porque a regra "pelo menos um caractere de cada tipo" reduz um pouco o
 * número real de combinações (o efeito é maior em senhas muito curtas).</p>
 */
public final class PasswordStrength {

    /** Níveis de força, do mais fraco ao mais forte. */
    public enum Level {
        WEAK("Fraca"),
        REASONABLE("Razoável"),
        STRONG("Forte"),
        VERY_STRONG("Muito forte");

        private final String label;

        Level(String label) {
            this.label = label;
        }

        /** Texto exibido ao usuário. */
        public String getLabel() {
            return label;
        }
    }

    // Faixas de entropia (em bits) de cada nível. São escolhas práticas deste projeto,
    // não um padrão oficial.
    static final double REASONABLE_FROM_BITS = 40.0;
    static final double STRONG_FROM_BITS = 60.0;
    static final double VERY_STRONG_FROM_BITS = 100.0;

    // Entropia que preenche 100% da barra de progresso.
    static final double FULL_BAR_BITS = 128.0;

    private final double entropyBits;
    private final Level level;

    private PasswordStrength(double entropyBits, Level level) {
        this.entropyBits = entropyBits;
        this.level = level;
    }

    /**
     * Estima a força de uma senha sorteada com o comprimento e o conjunto de caracteres informados.
     *
     * @param length      quantidade de caracteres da senha (pelo menos 1)
     * @param charsetSize quantidade de caracteres distintos disponíveis para o sorteio (pelo menos 1)
     * @throws IllegalArgumentException se algum valor for menor que 1
     */
    public static PasswordStrength estimate(int length, int charsetSize) {
        if (length < 1) {
            throw new IllegalArgumentException("O comprimento deve ser maior que zero.");
        }
        if (charsetSize < 1) {
            throw new IllegalArgumentException("O conjunto de caracteres deve ter ao menos um símbolo.");
        }
        double bitsPerCharacter = Math.log(charsetSize) / Math.log(2);
        double bits = length * bitsPerCharacter;
        return new PasswordStrength(bits, levelFor(bits));
    }

    private static Level levelFor(double bits) {
        if (bits < REASONABLE_FROM_BITS) {
            return Level.WEAK;
        }
        if (bits < STRONG_FROM_BITS) {
            return Level.REASONABLE;
        }
        if (bits < VERY_STRONG_FROM_BITS) {
            return Level.STRONG;
        }
        return Level.VERY_STRONG;
    }

    /** Entropia estimada, em bits. */
    public double getEntropyBits() {
        return entropyBits;
    }

    public Level getLevel() {
        return level;
    }

    /** Valor entre 0.0 e 1.0 para preencher a barra de progresso. */
    public double getBarFraction() {
        return Math.min(1.0, entropyBits / FULL_BAR_BITS);
    }
}
