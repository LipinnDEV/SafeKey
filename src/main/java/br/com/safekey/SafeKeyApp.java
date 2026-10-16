package br.com.safekey;

import br.com.safekey.model.PasswordOptions;
import br.com.safekey.model.PasswordStrength;
import br.com.safekey.service.PasswordGenerator;
import br.com.safekey.ui.DarkCheckBox;
import br.com.safekey.ui.DarkSliderUI;
import br.com.safekey.ui.LogoIcon;
import br.com.safekey.ui.PasswordDisplay;
import br.com.safekey.ui.RoundedButton;
import br.com.safekey.ui.RoundedPanel;
import br.com.safekey.ui.StrengthBar;
import br.com.safekey.ui.Theme;
import br.com.safekey.util.ClipboardUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Janela principal do SafeKey: monta a interface e liga os botões às regras do programa.
 *
 * <p>Esta classe só cuida da tela e da interação. Gerar a senha é trabalho do
 * {@link PasswordGenerator}, calcular a força é do {@link PasswordStrength} e copiar para a
 * área de transferência é do {@link ClipboardUtil}.</p>
 *
 * <p>Fluxo ao clicar em "Gerar nova senha": ler as opções da tela, validar, gerar a senha em
 * segundo plano ({@link SwingWorker}) e, de volta à thread da interface, mostrar a senha e a força.</p>
 */
public class SafeKeyApp {

    private static final String VERSION = "1.0.0";
    private static final int DEFAULT_LENGTH = 16;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final PasswordGenerator generator = new PasswordGenerator();

    // Senha e força
    private final PasswordDisplay passwordDisplay = new PasswordDisplay();
    private final RoundedButton copyButton = new RoundedButton("Copiar");
    private final JLabel strengthLabel = new JLabel(" ");
    private final JLabel entropyLabel = new JLabel(" ");
    private final StrengthBar strengthBar = new StrengthBar();

    // Opções
    private final JSlider lengthSlider = new JSlider(
            PasswordGenerator.MIN_LENGTH, PasswordGenerator.MAX_LENGTH, DEFAULT_LENGTH);
    private final JTextField lengthField = new JTextField(String.valueOf(DEFAULT_LENGTH), 3);
    private final DarkCheckBox lowercaseBox = new DarkCheckBox("Minúsculas (a-z)", true);
    private final DarkCheckBox uppercaseBox = new DarkCheckBox("Maiúsculas (A-Z)", true);
    private final DarkCheckBox digitsBox = new DarkCheckBox("Números (0-9)", true);
    private final DarkCheckBox symbolsBox = new DarkCheckBox("Símbolos (!@#$%)", true);
    private final DarkCheckBox similarBox = new DarkCheckBox("Evitar caracteres parecidos (O/0, I/l/1)", false);

    // Ação principal e mensagens
    private final RoundedButton generateButton = new RoundedButton("Gerar nova senha");
    private final JLabel statusLabel = new JLabel(" ");
    private final Timer messageTimer = new Timer(4000, e -> statusLabel.setText(" "));

    private final JPanel content;

    // Evita que slider e campo de texto fiquem se atualizando em círculo.
    private boolean syncingLength;
    // Impede começar uma geração enquanto outra ainda não terminou.
    private boolean generating;

    public SafeKeyApp() {
        content = buildContent();
        connectEvents();
        messageTimer.setRepeats(false);
    }

    /** Ponto de entrada. Toda a interface Swing deve ser criada na thread de eventos (EDT). */
    public static void main(String[] args) {
        // Suavização de texto mais nítida, principalmente no Linux.
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            Theme.install();
            new SafeKeyApp().open();
        });
    }

    /** Cria a janela, mostra na tela e gera a primeira senha. */
    public void open() {
        JFrame frame = new JFrame("SafeKey");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new LogoIcon(64).toImage());
        frame.setContentPane(content);
        // Enter aciona "Gerar nova senha".
        frame.getRootPane().setDefaultButton(generateButton);

        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setSize(Math.max(frame.getWidth(), 560), frame.getHeight());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        generatePassword(false);
    }

    // ------------------------------------------------------------------ construção da tela

    private JPanel buildContent() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Theme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 14, 24));

        generateButton.setFont(Theme.uiFont(Font.BOLD, 15));
        generateButton.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        root.add(buildHeader(), row(0, 0));
        root.add(buildPasswordCard(), row(1, 22));
        root.add(buildOptionsCard(), row(2, 14));
        root.add(generateButton, row(3, 18));
        root.add(buildStatusLabel(), row(4, 10));

        // Espaço elástico: ao aumentar a janela, o conteúdo fica no topo e o rodapé desce.
        GridBagConstraints spacer = row(5, 0);
        spacer.weighty = 1.0;
        spacer.fill = GridBagConstraints.BOTH;
        root.add(Box.createVerticalGlue(), spacer);

        root.add(buildFooter(), row(6, 8));
        return root;
    }

    private JPanel buildHeader() {
        JLabel title = label("SafeKey", Font.BOLD, 24, Theme.TEXT);
        JLabel subtitle = label("Gerador de senhas seguras e aleatórias", Font.PLAIN, 13, Theme.TEXT_MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Título e subtítulo empilhados e centralizados na altura do logotipo.
        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(Box.createVerticalGlue());
        titles.add(title);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);
        titles.add(Box.createVerticalGlue());

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(false);
        header.add(new JLabel(new LogoIcon(48)), BorderLayout.WEST);
        header.add(titles, BorderLayout.CENTER);
        return header;
    }

    private RoundedPanel buildPasswordCard() {
        RoundedPanel card = newCard();

        // Caixa escura onde a senha aparece. Altura fixa para a janela não "pular".
        RoundedPanel displayBox = new RoundedPanel(new BorderLayout(), 14, Theme.FIELD, Theme.BORDER);
        displayBox.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        displayBox.setPreferredSize(new Dimension(120, 88));
        displayBox.add(passwordDisplay, BorderLayout.CENTER);

        copyButton.setToolTipText("Copiar a senha para a área de transferência");
        JPanel copyWrapper = new JPanel(new GridBagLayout()); // centraliza o botão na vertical
        copyWrapper.setOpaque(false);
        copyWrapper.add(copyButton);

        JPanel passwordLine = new JPanel(new BorderLayout(12, 0));
        passwordLine.setOpaque(false);
        passwordLine.add(displayBox, BorderLayout.CENTER);
        passwordLine.add(copyWrapper, BorderLayout.EAST);

        strengthLabel.setFont(Theme.uiFont(Font.BOLD, 14));
        entropyLabel.setFont(Theme.uiFont(Font.PLAIN, 13));
        entropyLabel.setForeground(Theme.TEXT_MUTED);
        entropyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel level = new JPanel(new BorderLayout(6, 0));
        level.setOpaque(false);
        level.add(label("Força estimada:", Font.PLAIN, 13, Theme.TEXT_MUTED), BorderLayout.WEST);
        level.add(strengthLabel, BorderLayout.CENTER);

        JPanel strengthRow = new JPanel(new BorderLayout());
        strengthRow.setOpaque(false);
        strengthRow.add(level, BorderLayout.WEST);
        strengthRow.add(entropyLabel, BorderLayout.EAST);

        card.add(passwordLine, row(0, 0));
        card.add(strengthRow, row(1, 16));
        card.add(strengthBar, row(2, 8));
        return card;
    }

    private RoundedPanel buildOptionsCard() {
        RoundedPanel card = newCard();

        lengthSlider.setOpaque(false);
        lengthSlider.setUI(new DarkSliderUI(lengthSlider));
        lengthSlider.getAccessibleContext().setAccessibleName("Comprimento da senha");

        JPanel lengthRow = new JPanel(new BorderLayout(14, 0));
        lengthRow.setOpaque(false);
        lengthRow.add(label("Comprimento", Font.PLAIN, 14, Theme.TEXT), BorderLayout.WEST);
        lengthRow.add(lengthSlider, BorderLayout.CENTER);
        lengthRow.add(buildLengthField(), BorderLayout.EAST);

        JPanel types = new JPanel(new GridLayout(2, 2, 16, 12));
        types.setOpaque(false);
        types.add(lowercaseBox);
        types.add(uppercaseBox);
        types.add(digitsBox);
        types.add(symbolsBox);

        similarBox.setToolTipText("Remove O, 0, o, I, l, 1 e | do conjunto, para facilitar a leitura");

        card.add(lengthRow, row(0, 0));
        card.add(types, row(1, 18));
        card.add(similarBox, row(2, 12));
        return card;
    }

    /** Campo numérico ao lado do slider, dentro de uma moldura arredondada. */
    private RoundedPanel buildLengthField() {
        lengthField.setOpaque(false);
        lengthField.setBorder(null);
        lengthField.setHorizontalAlignment(SwingConstants.CENTER);
        lengthField.setFont(Theme.monoFont(Font.BOLD, 15));
        lengthField.setForeground(Theme.TEXT);
        lengthField.setCaretColor(Theme.TEXT);
        lengthField.setSelectionColor(Theme.PRIMARY);
        lengthField.setSelectedTextColor(Color.WHITE);
        lengthField.setToolTipText(
                "Digite um valor de " + PasswordGenerator.MIN_LENGTH + " a " + PasswordGenerator.MAX_LENGTH);
        lengthField.getAccessibleContext().setAccessibleName("Comprimento da senha");

        RoundedPanel box = new RoundedPanel(new BorderLayout(), 12, Theme.FIELD, Theme.BORDER);
        box.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        box.add(lengthField, BorderLayout.CENTER);
        return box;
    }

    private JLabel buildStatusLabel() {
        statusLabel.setFont(Theme.uiFont(Font.PLAIN, 13));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return statusLabel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setOpaque(false);
        footer.add(label("Tudo é gerado neste computador. Nenhuma senha é salva ou enviada.",
                Font.PLAIN, 12, Theme.TEXT_MUTED), BorderLayout.WEST);
        footer.add(label("v" + VERSION, Font.PLAIN, 12, Theme.TEXT_MUTED), BorderLayout.EAST);
        return footer;
    }

    private static RoundedPanel newCard() {
        RoundedPanel card = new RoundedPanel(new GridBagLayout(), 20, Theme.SURFACE, Theme.BORDER);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return card;
    }

    private static JLabel label(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.uiFont(style, size));
        label.setForeground(color);
        return label;
    }

    /** Restrições de uma linha do GridBagLayout: ocupa toda a largura e cresce com a janela. */
    private static GridBagConstraints row(int gridY, int topGap) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridY;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(topGap, 0, 0, 0);
        return constraints;
    }

    // ------------------------------------------------------------------ eventos

    private void connectEvents() {
        generateButton.addActionListener(e -> generatePassword(true));
        copyButton.addActionListener(e -> copyPassword());

        lengthSlider.addChangeListener(e -> {
            if (!syncingLength) {
                syncingLength = true;
                try {
                    lengthField.setText(String.valueOf(lengthSlider.getValue()));
                } finally {
                    syncingLength = false;
                }
            }
        });

        lengthField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                syncSliderWithField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syncSliderWithField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                syncSliderWithField();
            }
        });
    }

    /** Move o slider enquanto o usuário digita, mas só quando o número é válido. */
    private void syncSliderWithField() {
        if (syncingLength) {
            return;
        }
        try {
            int value = Integer.parseInt(lengthField.getText().trim());
            if (value >= PasswordGenerator.MIN_LENGTH && value <= PasswordGenerator.MAX_LENGTH) {
                syncingLength = true;
                try {
                    lengthSlider.setValue(value);
                } finally {
                    syncingLength = false;
                }
            }
        } catch (NumberFormatException ignored) {
            // Texto vazio ou incompleto: a validação acontece quando o usuário pede para gerar.
        }
    }

    // ------------------------------------------------------------------ ações

    /**
     * Gera uma nova senha com as opções da tela.
     *
     * <p>A geração roda fora da thread de eventos: {@link java.security.SecureRandom} pode demorar
     * na primeira vez em alguns sistemas, e a janela não deve travar esperando.</p>
     *
     * @param announce se deve exibir a mensagem "Nova senha gerada."
     */
    private void generatePassword(boolean announce) {
        if (generating) {
            return;
        }
        PasswordOptions options = readValidOptions();
        if (options == null) {
            return;
        }

        generating = true;
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return generator.generate(options);
            }

            @Override
            protected void done() {
                try {
                    showPassword(get(), options);
                    if (announce) {
                        showSuccess("Nova senha gerada.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    showError("Não foi possível gerar a senha: " + cause.getMessage());
                } finally {
                    generating = false;
                }
            }
        }.execute();
    }

    private void copyPassword() {
        try {
            ClipboardUtil.copy(passwordDisplay.getText());
            showSuccess("Senha copiada para a área de transferência.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    /** Lê e valida as opções. Se algo estiver errado, mostra o motivo e devolve {@code null}. */
    private PasswordOptions readValidOptions() {
        try {
            PasswordOptions options = readOptions();
            generator.validate(options);
            return options;
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return null;
        }
    }

    private PasswordOptions readOptions() {
        int length;
        try {
            length = Integer.parseInt(lengthField.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número inteiro para o comprimento.", e);
        }
        return new PasswordOptions(
                length,
                lowercaseBox.isSelected(),
                uppercaseBox.isSelected(),
                digitsBox.isSelected(),
                symbolsBox.isSelected(),
                similarBox.isSelected());
    }

    private void showPassword(String password, PasswordOptions options) {
        passwordDisplay.setText(password);

        PasswordStrength strength = PasswordStrength.estimate(options.length(), generator.charsetSize(options));
        Color color = Theme.strengthColor(strength.getLevel());
        strengthLabel.setText(strength.getLevel().getLabel());
        strengthLabel.setForeground(color);
        entropyLabel.setText(String.format(PT_BR, "~%.1f bits de entropia", strength.getEntropyBits()));
        strengthBar.setStrength(strength.getBarFraction(), color);
    }

    private void showSuccess(String message) {
        statusLabel.setForeground(Theme.SUCCESS);
        statusLabel.setText(message);
        messageTimer.restart(); // a confirmação some sozinha depois de alguns segundos
    }

    private void showError(String message) {
        messageTimer.stop(); // o erro fica na tela até a próxima ação
        statusLabel.setForeground(Theme.DANGER);
        statusLabel.setText(message);
    }
}
