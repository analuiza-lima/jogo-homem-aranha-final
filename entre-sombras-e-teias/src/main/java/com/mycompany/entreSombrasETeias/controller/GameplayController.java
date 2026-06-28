package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.VilaoDAO;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.model.Vilao;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GameplayController implements Initializable {

    // ===================== LETTERBOX =====================
    @FXML private StackPane raizExterna;
    @FXML private AnchorPane palco;
    private static final double LARGURA_PALCO = 900.0;
    private static final double ALTURA_PALCO = 700.0;

    @FXML private Label labelVilaoNome;
    @FXML private Label labelHpJogador;
    @FXML private Label labelLvlJogador;
    @FXML private Label labelStatusTurno;
    @FXML private Label labelDialogoVilao;
    @FXML private Label labelDialogoDinamico;
    @FXML private Label labelDanoPopup;

    @FXML private ImageView iconeCoracaoAranha;
    @FXML private ProgressBar barraHpJogador;
    @FXML private ProgressBar barraHpVilao;
    @FXML private ImageView imgVilao;
    @FXML private ImageView imgPeterIcone;
    @FXML private ImageView imgBackground;

    @FXML private AnchorPane painelBalaoVilao;
    @FXML private AnchorPane caixaCombateUndertale;
    @FXML private AnchorPane painelMinigameAtaque;
    @FXML private AnchorPane painelNarrativaCenario;
    @FXML private AnchorPane containerHitbox;
    @FXML private Region barraMarcadorRitmo;

    @FXML private HBox painelBotoesLuta;
    @FXML private HBox painelSubAtaques;
    @FXML private HBox painelSubRecuperar;
    @FXML private VBox telaGameOver;

    @FXML private Button btnAtacarAranha;
    @FXML private Button btnRecuperar;
    @FXML private Button btnFugir;
    @FXML private Button btnAvancarDialogo;
    @FXML private Button btnAtaqueSuper;
    @FXML private Button btnSuplemento;

    private Jogador jogador;
    private Vilao vilao;
    private boolean turnoDoJogador = true;

    private AnimationTimer loopBatalha;
    private AnimationTimer loopMinigame;
    private boolean jaLevouDanoNesteTurno = false;

    private boolean cima, baixo, esquerda, direita;

    // ----- Hitbox estilo Undertale (faixa horizontal: vermelho|amarelo|verde|amarelo|vermelho) -----
    private static final double HITBOX_LARGURA_TOTAL = 600.0;
    private static final double HITBOX_X_INICIO = 25.0; // mesmo X do containerHitbox/barraMarcadorRitmo no FXML
    private double posicaoMarcador = HITBOX_X_INICIO;
    private boolean indoParaDireita = true;
    private double larguraFaixaVerde = 60.0; // recalculada conforme dificuldade do vilão

    private int superAcumulado = 0;
    private String tipoAtaqueAtual = "ONDA";

    private int episodioResolvido = 1;
   

    private List<javafx.scene.Node> elementosAtaqueAtivos = new ArrayList<>();
    private Random random = new Random();

    // Matriz contendo o Contexto Narrativo seguido do Diálogo Obrigatório de cada chefe
    // Apenas 3 episódios: ABUTRE, SHOCKER, LAGARTO
    private static final String[][] NARRATIVAS_E_DIALOGOS = {
        // EPISÓDIO 1: ABUTRE
        {
            "NARRATIVA: Em mais uma de suas patrulhas noturnas, Peter observava a cidade do alto dos prédios.",
            "NARRATIVA: Um vulto cruzou o céu rapidamente, carregando algo pesado.",
            "NARRATIVA: Peter se aproximou em silêncio, tentando entender o que estava acontecendo.",
            "NARRATIVA: As asas metálicas refletiram a luz da lua — definitivamente não era um pássaro.",
            "NARRATIVA: Antes que pudesse reagir, a figura mergulhou em sua direção.",
            "PETER: Ei, senhor… isso não é horário de voo autorizado.",
            "VILAO: Eu voo quando quiser, garoto. E pego o que quiser.",
            "PETER: Não se eu puder impedir!"
        },
        // EPISÓDIO 2: SHOCKER
        {
            "NARRATIVA: Durante uma ronda pelas ruas, Peter ouviu uma sequência de explosões abafadas.",
            "NARRATIVA: Seguindo o som, encontrou uma loja parcialmente destruída.",
            "NARRATIVA: No meio dos destroços, um homem com luvas estranhas disparava ondas pelo ar.",
            "NARRATIVA: Cada impacto fazia o chão vibrar sob seus pés.",
            "NARRATIVA: Quando Peter se aproximou, uma nova rajada veio direto na sua direção.",
            "VILAO: Hoje eu termino o que comecei, Homem-Aranha!",
            "PETER: Você começou alguma coisa?",
            "VILAO: Vou te transformar em poeira!",
            "PETER: Cara, você fala isso toda vez…",
            "VILAO: E dessa vez é sério!",
            "PETER: Tá bom, se você diz."
        },
        // EPISÓDIO 3: LAGARTO
        {
            "NARRATIVA: Investigando relatos estranhos sobre uma criatura causando visível caos na cidade, Peter entrou em um laboratório aparentemente abandonado.",
            "NARRATIVA: O lugar estava revirado, com equipamentos quebrados e marcas pelas paredes.",
            "NARRATIVA: Entre os destroços, ele encontrou sinais de experimentos recentes.",
            "NARRATIVA: Então a avistou… e a reconheceu na hora.",
            "NARRATIVA: Antes que pudesse falar, o monstro avançou com violência.",
            "PETER: Dr. Connors, você precisa lutar contra isso!",
            "VILAO: CONNORS NÃO EXISTE MAIS!",
            "PETER: Eu sei que ele ainda tá aí. Eu posso te ajudar.",
            "VILAO: QUANTA ARROGÂNCIA VINDA DE UM INSETO PATÉTICO COMO VOCÊ!",
            "PETER: Eu tô aqui tentando te salvar!",
            "VILAO: VOCÊ NÃO CONSEGUE SALVAR NEM A SI MESMO, MOLEQUE! DESISTA!"
        }
    };

    private int indiceDialogo = 0;
    private String[] dialogoAtual;

    public void aplicarDanoNoVilao(int quantidadeDano) {
        labelDanoPopup.setText("-" + quantidadeDano + " HP");
        labelDanoPopup.setVisible(true);

        ColorAdjust efeitoVermelho = new ColorAdjust();
        efeitoVermelho.setContrast(0.5);
        efeitoVermelho.setHue(-0.1);
        efeitoVermelho.setSaturation(1.0);
        imgVilao.setEffect(efeitoVermelho);

        double originalX = imgVilao.getTranslateX();
        Timeline tremor = new Timeline(
            new KeyFrame(Duration.millis(0),   new KeyValue(imgVilao.translateXProperty(), originalX)),
            new KeyFrame(Duration.millis(50),  new KeyValue(imgVilao.translateXProperty(), originalX + 10)),
            new KeyFrame(Duration.millis(100), new KeyValue(imgVilao.translateXProperty(), originalX - 10)),
            new KeyFrame(Duration.millis(150), new KeyValue(imgVilao.translateXProperty(), originalX + 10)),
            new KeyFrame(Duration.millis(200), new KeyValue(imgVilao.translateXProperty(), originalX - 10)),
            new KeyFrame(Duration.millis(250), new KeyValue(imgVilao.translateXProperty(), originalX + 5)),
            new KeyFrame(Duration.millis(300), new KeyValue(imgVilao.translateXProperty(), originalX - 5)),
            new KeyFrame(Duration.millis(350), new KeyValue(imgVilao.translateXProperty(), originalX))
        );
        tremor.play();

        // Trocado Thread.sleep por Timeline (evita criar Threads soltas e problemas de concorrência com a UI)
        Timeline resetEfeito = new Timeline(new KeyFrame(Duration.millis(900), e -> {
            labelDanoPopup.setVisible(false);
            imgVilao.setEffect(null);
        }));
        resetEfeito.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jogador = SessaoJogo.get().getJogador();
        String vilaoLogado = SessaoJogo.get().getVilaoAtual();

        if (vilaoLogado != null && !vilaoLogado.isEmpty()) {
            switch (vilaoLogado.toLowerCase()) {
                case "abutre":   episodioResolvido = 1; break;
                case "shocker":  episodioResolvido = 2; break;
                case "lagarto":  episodioResolvido = 3; break;
                default:         episodioResolvido = 1; break;
            }
        } else {
            episodioResolvido = (jogador != null) ? jogador.getNivelAtual() : 1;
        }
        // Garante que nunca passe de 3, já que só existem 3 episódios agora
        episodioResolvido = Math.max(1, Math.min(episodioResolvido, 3));

        try {
            vilao = new VilaoDAO().buscarPorEpisodio(episodioResolvido);
        } catch (SQLException e) {
            vilao = new Vilao();
            vilao.setHpMaximo(100);
            vilao.setHpAtual(100);
            vilao.setNome(vilaoLogado != null ? vilaoLogado.toUpperCase() : "VILÃO");
        }

        if (labelVilaoNome != null) labelVilaoNome.setText(vilao.getNome());

        // 1. Carrega os elementos visuais do jogador
        carregarImagemComponente(iconeCoracaoAranha, "/com/mycompany/entreSombrasETeias/jogo/imagens/homemaranhacorrendo.gif");
        carregarImagemComponente(imgPeterIcone, "/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png");

        // 2. Background
        carregarImagemComponente(imgBackground, "/com/mycompany/entreSombrasETeias/jogo/imagens/gotham-city.png");

        // 3. Carregamento dos Sprites dos Vilões (apenas 3 episódios)
        if (episodioResolvido == 1) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-tela-de-vilao.png");
        } else if (episodioResolvido == 2) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/shocker-tela-de-vilao.png");
        } else if (episodioResolvido == 3) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/lagarto-tela-de-vilao.png");
        }

        // 4. Inicialização da cadeia de Diálogos
        int indiceVetor = Math.max(0, Math.min(episodioResolvido - 1, NARRATIVAS_E_DIALOGOS.length - 1));
        dialogoAtual = NARRATIVAS_E_DIALOGOS[indiceVetor];
        indiceDialogo = 0;

        // 5. Dificuldade: faixa verde da hitbox encolhe conforme o episódio avança
        // Episódio 1 -> faixa larga (mais fácil) | Episódio 3 -> faixa estreita (mais difícil)
        switch (episodioResolvido) {
            case 1:  larguraFaixaVerde = 70.0; break;
            case 2:  larguraFaixaVerde = 50.0; break;
            case 3:  larguraFaixaVerde = 32.0; break;
            default: larguraFaixaVerde = 60.0; break;
        }

        // 6. Configuração e Bloqueio Inicial
        setBotoesAcaoBloqueados(true);
        if (painelBotoesLuta != null) painelBotoesLuta.setVisible(false);
        if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(true);
        if (telaGameOver != null) telaGameOver.setVisible(false);

        // 7. Atualizações do sistema
        atualizarDialogo();
        atualizarUI();
        configurarLetterbox();
        configurarControleTeclado();
    }

    // ===================== LETTERBOX (tela ajustável sem deformar/cortar) =====================
    private void configurarLetterbox() {
        if (palco == null || raizExterna == null) return;

        Scale escala = new Scale(1, 1, 0, 0);
        palco.getTransforms().add(escala);

        // Recalcula a escala sempre que o container externo muda de tamanho.
        // Usa Math.min para garantir letterbox: o palco nunca estica além do menor eixo,
        // então nunca corta nada e nunca deforma a proporção 900x700.
        ChangeListener<Number> recalcular = (obs, oldV, newV) -> aplicarEscalaLetterbox(escala);

        raizExterna.widthProperty().addListener(recalcular);
        raizExterna.heightProperty().addListener(recalcular);

        // Aplica uma vez assim que o nó estiver de fato na cena, com tamanho real conhecido
        Platform.runLater(() -> aplicarEscalaLetterbox(escala));
    }

    private void aplicarEscalaLetterbox(Scale escala) {
        double larguraDisponivel = raizExterna.getWidth();
        double alturaDisponivel = raizExterna.getHeight();
        if (larguraDisponivel <= 0 || alturaDisponivel <= 0) return;

        double fatorEscala = Math.min(larguraDisponivel / LARGURA_PALCO, alturaDisponivel / ALTURA_PALCO);
        if (fatorEscala <= 0) return;

        escala.setX(fatorEscala);
        escala.setY(fatorEscala);
    }

    private void carregarImagemComponente(ImageView iv, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url != null && iv != null) iv.setImage(new Image(url.toExternalForm()));
        } catch (Exception e) {
            System.out.println("Recurso não encontrado: " + path);
        }
    }

    private void atualizarUI() {
    if (jogador == null) return;

    int hpMax = jogador.getHpMaximo() > 0 ? jogador.getHpMaximo() : 100;
    labelHpJogador.setText("HP: " + jogador.getHpAtual() + "/" + hpMax);
    barraHpJogador.setProgress((double) jogador.getHpAtual() / hpMax);
    labelLvlJogador.setText("DIFICULDADE VILÃO: LVL " + episodioResolvido);

    if (vilao != null) {
        barraHpVilao.setProgress((double) vilao.getHpAtual() / vilao.getHpMaximo());
    }

    if (btnAtaqueSuper != null) {
        btnAtaqueSuper.setText("💥 SUPER (" + Math.min(100, (superAcumulado * 100) / 120) + "%)");
        btnAtaqueSuper.setDisable(superAcumulado < 120);
    }

    if (btnSuplemento != null) {
        btnSuplemento.setText("🥤 SUPLEMENTO (" + jogador.getSuplementos() + ")");
        btnSuplemento.setDisable(jogador.getSuplementos() == 0);
    }
}
    @FXML
    public void avancarDialogo() {
        indiceDialogo++;
        atualizarDialogo();
    }

    private void atualizarDialogo() {
        if (dialogoAtual == null) return;
        if (indiceDialogo < dialogoAtual.length) {
            String falaCompleta = dialogoAtual[indiceDialogo];

            if (falaCompleta.startsWith("NARRATIVA:")) {
                if (imgPeterIcone != null) imgPeterIcone.setVisible(false);
                if (painelBalaoVilao != null) painelBalaoVilao.setVisible(false);
                if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(true);
                if (labelDialogoDinamico != null) labelDialogoDinamico.setText(falaCompleta.replace("NARRATIVA:", "").trim());
            } else if (falaCompleta.startsWith("PETER:")) {
                if (imgPeterIcone != null) imgPeterIcone.setVisible(true);
                if (painelBalaoVilao != null) painelBalaoVilao.setVisible(false);
                if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(true);
                if (labelDialogoDinamico != null) labelDialogoDinamico.setText(falaCompleta);
            } else {
                if (imgPeterIcone != null) imgPeterIcone.setVisible(true);
                if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(true);
                if (labelDialogoDinamico != null) labelDialogoDinamico.setText("...");
                if (painelBalaoVilao != null) painelBalaoVilao.setVisible(true);
                if (labelDialogoVilao != null) labelDialogoVilao.setText(falaCompleta.replace("VILAO:", vilao.getNome() + ":"));
            }
        } else {
            if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(false);
            if (painelBalaoVilao != null) painelBalaoVilao.setVisible(false);
            if (imgPeterIcone != null) imgPeterIcone.setVisible(true);
            iniciarTurnoJogador();
        }
    }

    private void iniciarTurnoJogador() {
        turnoDoJogador = true;
        alternarMenusSubinferiores(painelBotoesLuta);
        if (labelStatusTurno != null) {
            labelStatusTurno.setVisible(true);
            labelStatusTurno.setText("⭐ O que o Aranha vai fazer?");
        }
        setBotoesAcaoBloqueados(false);
        if (caixaCombateUndertale != null) caixaCombateUndertale.requestFocus();
    }

    private void alternarMenusSubinferiores(HBox painelAlvo) {
        if (painelBotoesLuta != null) painelBotoesLuta.setVisible(painelAlvo == painelBotoesLuta);
        if (painelSubAtaques != null) painelSubAtaques.setVisible(painelAlvo == painelSubAtaques);
        if (painelSubRecuperar != null) painelSubRecuperar.setVisible(painelAlvo == painelSubRecuperar);
    }

    @FXML private void mostrarOpcoesAtaque() { alternarMenusSubinferiores(painelSubAtaques); }
    @FXML private void mostrarOpcoesRecuperar() { alternarMenusSubinferiores(painelSubRecuperar); }
    @FXML public void voltarParaMenuLuta() { alternarMenusSubinferiores(painelBotoesLuta); }

    @FXML
    private void iniciarMinigameOnda() {
        tipoAtaqueAtual = "ONDA";
        dispararLoopRitmo();
    }

    @FXML
    private void iniciarMinigameSuper() {
        if (superAcumulado >= 120) {
            tipoAtaqueAtual = "SUPER";
            dispararLoopRitmo();
        }
    }

    // ===================== HITBOX ESTILO UNDERTALE (fiel ao wireframe) =====================
    // Constrói a faixa: VERMELHO | AMARELO | VERDE(fino, centro) | AMARELO | VERMELHO
    private void construirHitboxRitmo() {
        if (containerHitbox == null) return;
        containerHitbox.getChildren().clear();

        double larguraTotal = HITBOX_LARGURA_TOTAL;
        double alturaFaixa = 60.0;
        double centro = larguraTotal / 2.0;

        double meiaVerde = larguraFaixaVerde / 2.0;
        double larguraAmarelo = 90.0; // faixa amarela de cada lado da verde
        double xVerdeInicio = centro - meiaVerde;
        double xVerdeFim = centro + meiaVerde;
        double xAmareloEsqInicio = Math.max(0, xVerdeInicio - larguraAmarelo);
        double xAmareloDirFim = Math.min(larguraTotal, xVerdeFim + larguraAmarelo);

        // Vermelho esquerdo
        Rectangle vermelhoEsq = new Rectangle(0, 0, xAmareloEsqInicio, alturaFaixa);
        vermelhoEsq.setFill(Color.web("#cc1111"));
        // Amarelo esquerdo
        Rectangle amareloEsq = new Rectangle(xAmareloEsqInicio, 0, xVerdeInicio - xAmareloEsqInicio, alturaFaixa);
        amareloEsq.setFill(Color.web("#e6c200"));
        // Verde (centro fino - acerto perfeito)
        Rectangle verde = new Rectangle(xVerdeInicio, 0, larguraFaixaVerde, alturaFaixa);
        verde.setFill(Color.web("#22cc44"));
        // Amarelo direito
        Rectangle amareloDir = new Rectangle(xVerdeFim, 0, xAmareloDirFim - xVerdeFim, alturaFaixa);
        amareloDir.setFill(Color.web("#e6c200"));
        // Vermelho direito
        Rectangle vermelhoDir = new Rectangle(xAmareloDirFim, 0, larguraTotal - xAmareloDirFim, alturaFaixa);
        vermelhoDir.setFill(Color.web("#cc1111"));

        // Bolinhas decorativas (igual ao padrão pontilhado do wireframe)
        for (Rectangle faixa : new Rectangle[]{vermelhoEsq, amareloEsq, vermelhoDir, amareloDir}) {
            faixa.setArcWidth(0);
        }

        containerHitbox.getChildren().addAll(vermelhoEsq, amareloEsq, verde, amareloDir, vermelhoDir);

        // Garante que o marcador fique por cima de tudo
        if (barraMarcadorRitmo != null) {
            barraMarcadorRitmo.toFront();
        }
    }

    private void dispararLoopRitmo() {
        if (labelStatusTurno != null) labelStatusTurno.setVisible(false);
        if (painelMinigameAtaque != null) painelMinigameAtaque.setVisible(true);
        setBotoesAcaoBloqueados(true);
        if (painelSubAtaques != null) painelSubAtaques.setVisible(false);

        construirHitboxRitmo();

        posicaoMarcador = HITBOX_X_INICIO;
        indoParaDireita = true;
        if (barraMarcadorRitmo != null) barraMarcadorRitmo.setLayoutX(posicaoMarcador);

        loopMinigame = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double velocidadeMovel = 6.0;
                double limiteEsquerdo = HITBOX_X_INICIO;
                double limiteDireito = HITBOX_X_INICIO + HITBOX_LARGURA_TOTAL - barraMarcadorRitmo.getPrefWidth();

                if (indoParaDireita) {
                    posicaoMarcador += velocidadeMovel;
                    if (posicaoMarcador >= limiteDireito) { posicaoMarcador = limiteDireito; indoParaDireita = false; }
                } else {
                    posicaoMarcador -= velocidadeMovel;
                    if (posicaoMarcador <= limiteEsquerdo) { posicaoMarcador = limiteEsquerdo; indoParaDireita = true; }
                }
                if (barraMarcadorRitmo != null) barraMarcadorRitmo.setLayoutX(posicaoMarcador);
            }
        };
        loopMinigame.start();
    }

    // ===================== CONTROLE DE TECLADO (bug corrigido) =====================
    // BUG ORIGINAL: o handler de teclado só era registrado dentro de um listener de
    // sceneProperty(). Esse listener só dispara quando a propriedade MUDA de valor.
    // Se a Scene já estivesse definida no momento do initialize() (o que é o caso normal
    // em troca de tela via SceneManager, já que o Stage já tem uma Scene quando o FXML
    // é carregado), o listener nunca executava e os botões UP/DOWN/LEFT/RIGHT/SPACE
    // nunca eram capturados -> minigame de ataque nunca registrava o SPACE -> "ataque não funciona".
    // CORREÇÃO: registra direto se a cena já existir, e também escuta futuras trocas de cena.
    private void configurarControleTeclado() {
        if (caixaCombateUndertale == null) return;

        if (caixaCombateUndertale.getScene() != null) {
            registrarHandlersDeTeclado(caixaCombateUndertale.getScene());
        }
        caixaCombateUndertale.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) registrarHandlersDeTeclado(newScene);
        });
    }

    private void registrarHandlersDeTeclado(Scene cena) {
        // addEventFilter no nível da Scene garante que o jogo responda ao teclado
        // independentemente de qual nó tem o foco no momento (botão, painel, etc).
        cena.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, evento -> {
            switch (evento.getCode()) {
                case UP:    cima = true; break;
                case DOWN:  baixo = true; break;
                case LEFT:  esquerda = true; break;
                case RIGHT: direita = true; break;
                case SPACE:
                case ENTER:
                    if (painelMinigameAtaque != null && painelMinigameAtaque.isVisible()) {
                        computarDanoRitmo();
                        evento.consume();
                    }
                    break;
                default: break;
            }
        });
        cena.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, evento -> {
            switch (evento.getCode()) {
                case UP:    cima = false; break;
                case DOWN:  baixo = false; break;
                case LEFT:  esquerda = false; break;
                case RIGHT: direita = false; break;
                default: break;
            }
        });
    }

    private void computarDanoRitmo() {
        if (loopMinigame != null) loopMinigame.stop();
        if (painelMinigameAtaque != null) painelMinigameAtaque.setVisible(false);
        if (labelStatusTurno != null) labelStatusTurno.setVisible(true);

        double centroAlvo = HITBOX_X_INICIO + HITBOX_LARGURA_TOTAL / 2.0;
        double margemErro = Math.abs(posicaoMarcador - centroAlvo);
        double meiaVerde = larguraFaixaVerde / 2.0;
        int danoFinal = 0;

        if (margemErro <= meiaVerde) {
            // Acerto perfeito (caiu na faixa verde central)
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 75 : 35;
            if (labelStatusTurno != null) labelStatusTurno.setText("⭐ ACERTO PERFEITO!");
            if (tipoAtaqueAtual.equals("ONDA")) {
                superAcumulado = Math.min(120, superAcumulado + 40);
            } else {
                superAcumulado = 0;
            }
        } else if (margemErro <= meiaVerde + 90.0) {
            // Acerto bom (caiu na faixa amarela)
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 45 : 20;
            if (labelStatusTurno != null) labelStatusTurno.setText("* Bom impacto! Causou " + danoFinal + " de dano.");
            superAcumulado = tipoAtaqueAtual.equals("ONDA") ? Math.min(120, superAcumulado + 25) : 0;
        } else {
            // Errou (faixa vermelha)
            if (labelStatusTurno != null) labelStatusTurno.setText("* Você falhou no tempo!");
            if (tipoAtaqueAtual.equals("SUPER")) superAcumulado = 0;
        }

        if (danoFinal > 0 && vilao != null) {
            vilao.setHpAtual(Math.max(0, vilao.getHpAtual() - danoFinal));
            animarDanoInimigo(danoFinal);
        }

        atualizarUI();

        if (vilao != null && vilao.getHpAtual() <= 0) {

            jogador.ganharSuplemento();
            
            if (episodioResolvido == 1) {
                jogador.setDerrotouAbutre(true);
            } else if (episodioResolvido == 2) {
                jogador.setDerrotouShocker(true);
            } else if (episodioResolvido == 3) {
                jogador.setDerrotouLagarto(true);
            }

            if (labelStatusTurno != null)
                labelStatusTurno.setText("⭐ Vitória! O vilão foi derrotado.");

            Timeline vitoriaDelay = new Timeline(new KeyFrame(Duration.seconds(2.0), e -> {
                SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
            }));

            vitoriaDelay.play();
            return;
        }

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    private void animarDanoInimigo(int dano) {
        if (labelDanoPopup != null) {
            labelDanoPopup.setText("-" + dano + " HP");
            labelDanoPopup.setVisible(true);
        }

        if (imgVilao != null) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), imgVilao);
            tt.setByX(10);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.setOnFinished(e -> {
                if (labelDanoPopup != null) labelDanoPopup.setVisible(false);
                imgVilao.setTranslateX(0);
            });
            tt.play();
        }
    }

    @FXML
    private void usarSentidoAranha() {
        jogador.setHpAtual(Math.min(jogador.getHpMaximo(), jogador.getHpAtual() + 15));
        if (labelStatusTurno != null) labelStatusTurno.setText("* Sentido Aranha ativado! +15 HP.");
        concluirAcaoSuporte();
    }

    @FXML
private void usarSuplemento() {

    if (jogador.usarSuplemento()) {

        if (labelStatusTurno != null)
            labelStatusTurno.setText("* Você recuperou +15 HP.");

        concluirAcaoSuporte();
    }
}

    private void concluirAcaoSuporte() {
        atualizarUI();
        alternarMenusSubinferiores(null);
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    private void iniciarTurnoInimigo() {
        turnoDoJogador = false;
        alternarMenusSubinferiores(null);
        if (labelStatusTurno != null) labelStatusTurno.setVisible(false);
        if (iconeCoracaoAranha != null) {
            iconeCoracaoAranha.setVisible(true);
            iconeCoracaoAranha.setLayoutX(300);
            iconeCoracaoAranha.setLayoutY(55);
        }
        jaLevouDanoNesteTurno = false;

        if (caixaCombateUndertale != null) {
            caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
        }
        elementosAtaqueAtivos.clear();

        executarAtaqueEspecificoVilao();
    }

    private void executarAtaqueEspecificoVilao() {
        int tipoAtaque = random.nextInt(3) + 1;

        loopBatalha = new AnimationTimer() {
            private double tempoDecorrido = 0;

            @Override
            public void handle(long now) {
                tempoDecorrido += 0.016;

                double velocidadePlayer = 4.0;
                if (iconeCoracaoAranha != null) {
                    if (cima && iconeCoracaoAranha.getLayoutY() > 10) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() - velocidadePlayer);
                    if (baixo && iconeCoracaoAranha.getLayoutY() < 100) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() + velocidadePlayer);
                    if (esquerda && iconeCoracaoAranha.getLayoutX() > 10) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() - velocidadePlayer);
                    if (direita && iconeCoracaoAranha.getLayoutX() < 600) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() + velocidadePlayer);
                }

                if (episodioResolvido == 1) {
                    processarAtaquesAbutre(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 2) {
                    processarAtaquesShocker(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 3) {
                    processarAtaquesLagarto(tipoAtaque, tempoDecorrido);
                }

                verificarColisoesEImpacto();

                if (tempoDecorrido >= 4.0) {
                    this.stop();
                    finalizarTurnoDefensivo();
                }
            }
        };
        loopBatalha.start();
    }

    private void processarAtaquesAbutre(int tipo, double tempo) {
        if (tempo < 0.05) {
            if (tipo == 1) {
                ImageView img = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-ataque.png", 60, 60);
                if (img != null) { img.setLayoutX(300); img.setLayoutY(-50); adicionarObjetoAoPainel(img); }
            } else if (tipo == 2) {
                ImageView img = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-ataque2.png", 70, 50);
                if (img != null) { img.setLayoutX(-80); img.setLayoutY(50); adicionarObjetoAoPainel(img); }
            }
        }

        if (tipo == 3 && Math.random() < 0.03 && elementosAtaqueAtivos.size() < 3) {
            Rectangle barra = new Rectangle(25, 8, Color.SILVER);
            barra.setLayoutX(640); barra.setLayoutY(random.nextInt(100) + 10);
            adicionarObjetoAoPainel(barra);
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (tipo == 1) node.setLayoutY(node.getLayoutY() + 5);
            else if (tipo == 2) node.setLayoutX(node.getLayoutX() + 6);
            else node.setLayoutX(node.getLayoutX() - 4);
        }
    }

    private void processarAtaquesShocker(int tipo, double tempo) {
        if (tipo == 1 && Math.random() < 0.02) {
            Rectangle onda = new Rectangle(12, 40, Color.DEEPPINK);
            onda.setLayoutX(640); onda.setLayoutY(random.nextBoolean() ? 15 : 75);
            adicionarObjetoAoPainel(onda);
        }
        if (tipo == 2 && tempo < 0.05) {
            for (int i = 0; i < 3; i++) {
                Circle exp = new Circle(4, Color.ORANGERED);
                exp.setLayoutX(150 + (i * 160)); exp.setLayoutY(65);
                adicionarObjetoAoPainel(exp);
            }
        }
        if (tipo == 3 && Math.random() < 0.04 && elementosAtaqueAtivos.size() < 5) {
            Circle pulso = new Circle(8, Color.YELLOW);
            pulso.setLayoutX(random.nextInt(550) + 30); pulso.setLayoutY(random.nextInt(90) + 15);
            adicionarObjetoAoPainel(pulso);
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (node instanceof Circle) {
                Circle c = (Circle) node;
                if (tipo == 2 && c.getRadius() < 45) c.setRadius(c.getRadius() + 1.2);
            } else {
                node.setLayoutX(node.getLayoutX() - 5);
            }
        }
    }

    private void processarAtaquesLagarto(int tipo, double tempo) {
        if (tipo == 1 && Math.random() < 0.02) {
            ImageView gosma = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 30, 30);
            if (gosma != null) { gosma.setLayoutX(random.nextInt(580) + 20); gosma.setLayoutY(-30); adicionarObjetoAoPainel(gosma); }
        }
        if (tipo == 2 && Math.random() < 0.03) {
            ImageView corrida = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 40, 20);
            if (corrida != null && iconeCoracaoAranha != null) { corrida.setLayoutX(-40); corrida.setLayoutY(iconeCoracaoAranha.getLayoutY()); adicionarObjetoAoPainel(corrida); }
        }
        if (tipo == 3 && tempo < 0.05) {
            Rectangle arco = new Rectangle(100, 12, Color.LIMEGREEN);
            arco.setLayoutX(250); arco.setLayoutY(60);
            adicionarObjetoAoPainel(arco);
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (tipo == 1) node.setLayoutY(node.getLayoutY() + 4);
            else if (tipo == 2) node.setLayoutX(node.getLayoutX() + 8);
        }
    }

    private ImageView criarProjetilImagem(String path, double w, double h) {
        ImageView iv = new ImageView();
        iv.setFitWidth(w); iv.setFitHeight(h);
        carregarImagemComponente(iv, path);
        return iv;
    }

    private void adicionarObjetoAoPainel(javafx.scene.Node node) {
        elementosAtaqueAtivos.add(node);
        if (caixaCombateUndertale != null) caixaCombateUndertale.getChildren().add(node);
    }

    private void verificarColisoesEImpacto() {
        if (iconeCoracaoAranha == null) return;
        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (node.getBoundsInParent().intersects(iconeCoracaoAranha.getBoundsInParent())) {
                if (!jaLevouDanoNesteTurno) {
                    jogador.setHpAtual(Math.max(0, jogador.getHpAtual() - 15));
                    jaLevouDanoNesteTurno = true;
                    atualizarUI();

                    iconeCoracaoAranha.setOpacity(0.3);
                    Timeline piscar = new Timeline(new KeyFrame(Duration.seconds(0.3), e -> iconeCoracaoAranha.setOpacity(1.0)));
                    piscar.play();
                }
            }
        }
    }

    private void finalizarTurnoDefensivo() {
        if (iconeCoracaoAranha != null) iconeCoracaoAranha.setVisible(false);
        if (caixaCombateUndertale != null) caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
        elementosAtaqueAtivos.clear();

        if (jogador.getHpAtual() <= 0) {
            if (telaGameOver != null) telaGameOver.setVisible(true);
        } else {
            iniciarTurnoJogador();
        }
    }

    private void setBotoesAcaoBloqueados(boolean status) {
        if (btnAtacarAranha != null) btnAtacarAranha.setDisable(status);
        if (btnRecuperar != null) btnRecuperar.setDisable(status);
        if (btnFugir != null) btnFugir.setDisable(status);
    }

    @FXML private void executarFugir() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameover.fxml"); }
    @FXML private void voltarMenuInicial() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/menu.fxml"); }
}