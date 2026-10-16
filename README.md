# SafeKey

![Java 17+](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-blue?logo=apachemaven&logoColor=white)
![Interface](https://img.shields.io/badge/Interface-Java%20Swing-informational)
![Licença MIT](https://img.shields.io/badge/Licen%C3%A7a-MIT-green)

Gerador de senhas aleatórias para desktop, escrito em Java, com interface gráfica escura e moderna.
Tudo acontece no seu computador: nenhuma senha é enviada, salva ou registrada.

## Captura de tela

> Espaço reservado para a captura de tela da interface.
> Salve a imagem como `docs/screenshot.png`, remova este bloco e use a linha abaixo no lugar dele:
>
> `![Interface do SafeKey](docs/screenshot.png)`

<!-- ![Interface do SafeKey](docs/screenshot.png) -->

## Objetivo

O SafeKey é um projeto de estudo que reúne, em um aplicativo pequeno e completo, assuntos importantes
da programação Java: interface gráfica com Swing, organização em pacotes, geração segura de números
aleatórios, validação de dados, tratamento de erros, testes automatizados com JUnit e build com Maven.

## Funcionalidades

- Senhas de **4 a 128 caracteres**, com o comprimento escolhido no controle deslizante ou digitado.
- Escolha dos tipos de caractere: minúsculas, maiúsculas, números e símbolos.
- Garantia de **pelo menos um caractere de cada tipo selecionado**, com os caracteres embaralhados no final.
- Opção para **evitar caracteres parecidos** (`O`, `0`, `o`, `I`, `l`, `1` e `|`).
- **Indicador de força**: barra colorida, nível (Fraca, Razoável, Forte ou Muito forte) e entropia estimada em bits.
- **Copiar para a área de transferência** com um clique e mensagem de confirmação.
- Mensagens claras de validação (por exemplo, quando nenhum tipo de caractere está marcado).
- Senhas longas aparecem por inteiro: a fonte diminui e o texto quebra em até três linhas.
- Interface escura que se adapta ao redimensionamento da janela.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 17 ou superior | Linguagem do projeto (usa `record` e `switch` com expressões) |
| Java Swing e Java 2D | Interface gráfica, com componentes desenhados à mão |
| `java.security.SecureRandom` | Sorteio seguro dos caracteres |
| Maven | Compilação, testes e empacotamento |
| JUnit 5 | Testes automatizados (somente no escopo de teste) |

Não há banco de dados nem bibliotecas externas no aplicativo. O JUnit só é usado para rodar os testes e
não entra no `.jar` final.

## Requisitos

- **JDK 17 ou superior.** Confira com `java -version`.
- **Maven 3.8 ou superior** para usar o terminal. NetBeans e IntelliJ IDEA já trazem um Maven embutido.
- Conexão com a internet **apenas na primeira vez**, para o Maven baixar plugins e o JUnit.
  Depois disso o projeto compila e roda offline.
- Um ambiente gráfico (Windows, macOS ou Linux com interface). Em servidores sem tela a janela não abre.

## Como executar

### NetBeans

1. Abra **File > Open Project...**, selecione a pasta `SafeKey` (o ícone de projeto Maven aparece) e clique em **Open Project**.
2. Aguarde o NetBeans carregar o projeto. Na primeira vez ele baixa as dependências de teste.
3. Confirme que o NetBeans usa um JDK 17 ou superior em **Tools > Java Platforms**.
4. Clique com o botão direito no projeto e escolha **Run** (ou pressione `F6`).
   Se ele perguntar pela classe principal, escolha `br.com.safekey.SafeKeyApp`.
5. Para rodar os testes, clique com o botão direito no projeto e escolha **Test** (`Alt+F6`).

### IntelliJ IDEA

1. Abra **File > Open...**, selecione a pasta `SafeKey` (ou o arquivo `pom.xml`) e escolha **Open as Project**.
2. Aguarde a importação do Maven terminar (a barra de progresso fica no canto inferior direito).
3. Em **File > Project Structure > Project**, defina o **SDK** como JDK 17 ou superior e o **Language level** como 17.
4. Abra `src/main/java/br/com/safekey/SafeKeyApp.java` e clique no triângulo verde ao lado do método `main`,
   depois em **Run 'SafeKeyApp.main()'**.
5. Para rodar os testes, clique com o botão direito na pasta `src/test/java` e escolha **Run 'All Tests'**.

### Terminal

```bash
# 1. Confira as versões (Java 17 ou superior)
java -version
mvn -version

# 2. Entre na pasta do projeto
cd SafeKey

# 3. Compile
mvn clean compile

# 4. Execute os testes
mvn test

# 5. Abra o aplicativo
mvn compile exec:java
```

Para gerar um `.jar` executável e abri-lo diretamente:

```bash
mvn clean package
java -jar target/safekey-1.0.0.jar
```

O comando `mvn package` também roda os testes antes de gerar o `.jar`.

## Testes

```bash
mvn test
```

Os testes ficam em `src/test/java` e cobrem:

- **Geração:** comprimento exato (incluindo os limites 4 e 128), uso exclusivo dos tipos escolhidos em
  todas as 15 combinações possíveis, presença de ao menos um caractere de cada tipo e embaralhamento.
- **Caracteres parecidos:** nenhum deles aparece quando a opção está ligada e o conjunto de caracteres encolhe.
- **Validações:** nenhum tipo selecionado e comprimentos fora do intervalo são rejeitados com mensagem clara.
- **Força:** a fórmula da entropia, as faixas de cada nível, a fração da barra e os argumentos inválidos.
- **Área de transferência:** rejeição de texto vazio. A cópia em si depende do sistema operacional e não é
  testada automaticamente.

## Estrutura do projeto

```
SafeKey/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
└── src/
    ├── main/java/br/com/safekey/
    │   ├── SafeKeyApp.java              # janela principal: monta a tela e liga os eventos
    │   ├── model/
    │   │   ├── PasswordOptions.java     # opções escolhidas pelo usuário (record imutável)
    │   │   └── PasswordStrength.java    # estimativa de força: entropia e níveis
    │   ├── service/
    │   │   └── PasswordGenerator.java   # validação e geração com SecureRandom
    │   ├── ui/                          # componentes visuais do tema escuro
    │   │   ├── Theme.java               # cores, fontes e ajustes globais
    │   │   ├── RoundedPanel.java        # cards e molduras com cantos arredondados
    │   │   ├── RoundedButton.java       # botão azul arredondado
    │   │   ├── DarkCheckBox.java        # caixa de seleção
    │   │   ├── DarkSliderUI.java        # aparência do controle deslizante
    │   │   ├── StrengthBar.java         # barra de força
    │   │   ├── PasswordDisplay.java     # exibição da senha em várias linhas
    │   │   └── LogoIcon.java            # logotipo desenhado com Java 2D
    │   └── util/
    │       └── ClipboardUtil.java       # cópia para a área de transferência
    └── test/java/br/com/safekey/
        ├── model/PasswordStrengthTest.java
        ├── service/PasswordGeneratorTest.java
        └── util/ClipboardUtilTest.java
```

O Swing não traz um tema escuro moderno e o projeto não usa bibliotecas externas. Por isso existe o
pacote `ui`: ele guarda os poucos componentes que precisaram ser desenhados à mão, deixando a
`SafeKeyApp` focada em montar a tela e tratar os eventos.

## Como funciona

Ao clicar em **Gerar nova senha**:

1. A `SafeKeyApp` lê as opções da tela e monta um `PasswordOptions`.
2. O `PasswordGenerator` valida as opções (comprimento de 4 a 128 e ao menos um tipo de caractere).
   Se algo estiver errado, a mensagem aparece em vermelho e nada é gerado.
3. A senha é gerada em segundo plano, com um `SwingWorker`, para a janela nunca travar:
   - sorteia um caractere de cada tipo selecionado, garantindo que todos apareçam;
   - completa o restante sorteando entre todos os tipos selecionados;
   - embaralha tudo (algoritmo de Fisher-Yates) para que os caracteres garantidos não fiquem no início.
4. O `PasswordStrength` calcula a entropia e o nível, e a tela mostra a senha, a barra e o texto.

### Como a força é estimada

```
entropia (bits) = comprimento × log2(tamanho do conjunto de caracteres)
```

Com os quatro tipos marcados, o conjunto tem 87 caracteres (26 + 26 + 10 + 25). Com a opção de evitar
caracteres parecidos, ele tem 80. Uma senha de 16 caracteres com os quatro tipos tem cerca de 103,1 bits.
Uma senha de 8 números tem cerca de 26,6 bits.

| Entropia estimada | Nível |
|---|---|
| menos de 40 bits | Fraca |
| de 40 a menos de 60 bits | Razoável |
| de 60 a menos de 100 bits | Forte |
| 100 bits ou mais | Muito forte |

Essas faixas são escolhas práticas deste projeto, não um padrão oficial. A barra de progresso fica
cheia em 128 bits.

> **Atenção:** a entropia é uma **estimativa matemática**, não uma garantia absoluta de segurança.
> O indicador simplifica a análise e **não avalia todos os fatores** que importam para a segurança de uma
> senha, como reutilizá-la em vários serviços, vazamentos de dados, phishing, malware ou a forma como o
> serviço a armazena. A estimativa também fica levemente otimista, porque a regra de ter ao menos um
> caractere de cada tipo reduz um pouco o número real de combinações (o efeito é maior em senhas muito curtas).

## Segurança e privacidade

- As senhas são sorteadas com `java.security.SecureRandom`, um gerador criptograficamente seguro.
  O `java.util.Random` **não** é usado, pois sua sequência pode ser prevista.
- Tudo é local: o programa não usa rede, banco de dados nem arquivos para guardar senhas.
- Não existe histórico. Cada senha existe apenas na memória do programa enquanto ele estiver aberto
  e na área de transferência, se você clicar em **Copiar**.
- Não há senhas fixas, credenciais ou registros (logs) com senhas no código.

### Limitações conhecidas

- Outros programas do computador podem ler a área de transferência, e o SafeKey não a limpa
  automaticamente. Cole a senha onde precisa e, se quiser, copie outro texto em seguida.
- O SafeKey gera senhas, mas não as guarda. Para armazená-las com segurança, use um gerenciador de senhas.

## Solução de problemas

- **`release version 17 not supported` ou `invalid target release: 17`:** o JDK em uso é anterior ao 17.
  Instale um JDK 17 ou superior e configure o `JAVA_HOME` (terminal) ou o SDK do projeto (IDE).
- **Acentos quebrados ao compilar com `javac` manualmente:** use `javac -encoding UTF-8`.
  O Maven já faz isso por conta da configuração do `pom.xml`.
- **A janela não abre no Linux:** o programa precisa de um ambiente gráfico (X11 ou Wayland).

## Licença

Distribuído sob a licença [MIT](LICENSE).

##  Autor

Desenvolvido por **Felipe Mousinho**, estudante de Desenvolvimento de Sistemas, com interesse em Java, desenvolvimento de software e segurança digital.

- **GitHub:** [@LipinnDEV](https://github.com/LipinnDEV)
- **Repositórios:** [Confira meus projetos](https://github.com/LipinnDEV?tab=repositories)

Projeto desenvolvido com fins educacionais e para aprimoramento de conhecimentos em Java.
