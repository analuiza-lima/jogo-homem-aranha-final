package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.dao.JogadorDAO;
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
    private static final double HITBOX_X_INICIO = 25.0;
    private double posicaoMarcador = HITBOX_X_INICIO;
    private boolean indoParaDireita = true;
    private double larguraFaixaVerde = 60.0;

    private int superAcumulado = 0;
    private String tipoAtaqueAtual = "ONDA";

    private int episodioResolvido = 1;

    private List<javafx.scene.Node> elementosAtaqueAtivos = new ArrayList<>();
    private Random random = new Random();

    private static final String[][] NARRATIVAS_E_DIALOGOS = {
        // EPISODIO 1: ABUTRE
        {
            "NARRATIVA: Em mais uma de suas patrulhas noturnas, Peter observava a cidade do alto dos predios.",
            "NARRATIVA: Um vulto cruzou o ceu rapidamente, carregando algo pesado.",
            "NARRATIVA: Peter se aproximou em silencio, tentando entender o que estava acontecendo.",
            "NARRATIVA: As asas metalicas refletiram a luz da lua - definitivamente nao era um passaro.",
            "NARRATIVA: Antes que pudesse reagir, a figura mergulhou em sua direcao.",
            "PETER: Ei, senhor... isso nao e horario de voo autorizado.",
            "VILAO: Eu voo quando quiser, garoto. E pego o que quiser.",
            "PETER: Nao se eu puder impedir!"
        },
        // EPISODIO 2: SHOCKER
        {
            "NARRATIVA: Durante uma ronda pelas ruas, Peter ouviu uma sequencia de explosoes abafadas.",
            "NARRATIVA: Seguindo o som, encontrou uma loja parcialmente destruida.",
            "NARRATIVA: No meio dos destrocos, um homem com luvas estranhas disparava ondas pelo ar.",
            "NARRATIVA: Cada impacto fazia o chao vibrar sob seus pes.",
            "NARRATIVA: Quando Peter se aproximou, uma nova rajada veio direto na sua direcao.",
            "VILAO: Hoje eu termino o que comecei, Homem-Aranha!",
            "PETER: Voce comecou alguma coisa?",
            "VILAO: Vou te transformar em poeira!",
            "PETER: Cara, voce fala isso toda vez...",
            "VILAO: E dessa vez e serio!",
            "PETER: Ta bom, se voce diz."
        },
        // EPISODIO 3: LAGARTO
        {
            "NARRATIVA: Investigando relatos estranhos sobre uma criatura causando visivel caos na cidade, Peter entrou em um laboratorio aparentemente abandonado.",
            "NARRATIVA: O lugar estava revirado, com equipamentos quebrados e marcas pelas paredes.",
            "NARRATIVA: Entre os destrocos, ele encontrou sinais de experimentos recentes.",
            "NARRATIVA: Entao a avistou... e a reconheceu na hora.",
            "NARRATIVA: Antes que pudesse falar, o monstro avancou com violencia.",
            "PETER: Dr. Connors, voce precisa lutar contra isso!",
            "VILAO: CONNORS NAO EXISTE MAIS!",
            "PETER: Eu sei que ele ainda ta ai. Eu posso te ajudar.",
            "VILAO: QUANTA ARROGANCIA VINDA DE UM INSETO PATETICO COMO VOCE!",
            "PETER: Eu to aqui tentando te salvar!",
            "VILAO: VOCE NAO CONSEGUE SALVAR NEM A SI MESMO, MOLEQUE! DESISTA!"
        }
    };

    private int indiceDialogo = 0;
    private String[] dialogoAtual;

    public void aplicarDanoNoVilao(int quantidadeDano) {
        if (labelDanoPopup == null || imgVilao == null) return;

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
        episodioResolvido = Math.max(1, Math.min(episodioResolvido, 3));

        try {
            vilao = new VilaoDAO().buscarPorEpisodio(episodioResolvido);
        } catch (SQLException e) {
            vilao = null;
        }

        // Defesa extra: cobre tanto exception quanto DAO retornando null sem lancar erro.
        if (vilao == null) {
            vilao = new Vilao();
            vilao.setHpMaximo(100);
            vilao.setHpAtual(100);
            vilao.setNome(vilaoLogado != null ? vilaoLogado.toUpperCase() : "VILAO");
        }

        // Defesa extra: garante jogador valido mesmo se a sessao nao tiver sido preenchida.
        // OBS: Jogador.getHpMaximo() e calculado a partir do nivel (100 + (nivel-1)*20),
        // entao nao existe setHpMaximo() na classe - o construtor ja deixa hpAtual = hpMaximo.
        if (jogador == null) {
            jogador = new Jogador("Peter");
        }

        // BUG CORRIGIDO (pedido do usuário): na Teia de Confrontos as lutas não seguem
        // ordem cronológica (o jogador escolhe livremente qual vilão enfrentar), então
        // terminar uma luta com HP baixo não deveria carregar pra próxima - cada confronto
        // ali é independente. Isso só vale pro fluxo da Teia (vilaoLogado preenchido);
        // no Modo História o HP continua acumulando entre episódios normalmente, pois ali
        // a progressão é sequencial e faz sentido carregar o dano sofrido.
        boolean entrouPelaTeiaDeConfrontos = vilaoLogado != null && !vilaoLogado.isEmpty();
        if (entrouPelaTeiaDeConfrontos) {
            jogador.setHpAtual(jogador.getHpMaximo());
        }

        // 1. Elementos visuais do jogador
        carregarImagemComponente(iconeCoracaoAranha, "/com/mycompany/entreSombrasETeias/jogo/imagens/miranha.png");
        carregarImagemComponente(imgPeterIcone, "/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png");

        // 2. Background (agora varia por episódio - antes era sempre gotham-city.png
        // mesmo nas lutas contra Shocker e Lagarto, que ganharam seus próprios cenários)
        if (episodioResolvido == 2) {
            carregarImagemComponente(imgBackground, "/com/mycompany/entreSombrasETeias/jogo/imagens/background-shocker.jpg");
        } else if (episodioResolvido == 3) {
            carregarImagemComponente(imgBackground, "/com/mycompany/entreSombrasETeias/jogo/imagens/background-lagarto.jpg");
        } else {
            carregarImagemComponente(imgBackground, "/com/mycompany/entreSombrasETeias/jogo/imagens/gotham-city.png");
        }

        // 3. Sprites dos Vilões
        if (episodioResolvido == 1) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-tela-de-vilao.png");
        } else if (episodioResolvido == 2) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/shocker-tela-de-vilao.png");
        } else if (episodioResolvido == 3) {
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/lagarto-tela-de-vilao.png");
        }

        // 4. Cadeia de Diálogos
        int indiceVetor = Math.max(0, Math.min(episodioResolvido - 1, NARRATIVAS_E_DIALOGOS.length - 1));
        dialogoAtual = NARRATIVAS_E_DIALOGOS[indiceVetor];
        indiceDialogo = 0;

        // 5. Dificuldade da hitbox
        switch (episodioResolvido) {
            case 1:  larguraFaixaVerde = 70.0; break;
            case 2:  larguraFaixaVerde = 50.0; break;
            case 3:  larguraFaixaVerde = 32.0; break;
            default: larguraFaixaVerde = 60.0; break;
        }

        // 6. Bloqueio inicial de UI
        setBotoesAcaoBloqueados(true);
        alternarMenusSubinferiores(null);
        if (painelNarrativaCenario != null) painelNarrativaCenario.setVisible(true);
        if (telaGameOver != null) telaGameOver.setVisible(false);
        if (labelStatusTurno != null) labelStatusTurno.setVisible(false);
        if (painelMinigameAtaque != null) painelMinigameAtaque.setVisible(false);

        // 7. Inicializações
        atualizarDialogo();
        atualizarUI();
        configurarLetterbox();
        configurarControleTeclado();

        Platform.runLater(() -> {
            if (caixaCombateUndertale != null && caixaCombateUndertale.getScene() != null) {
                registrarHandlersDeTeclado(caixaCombateUndertale.getScene());
            }
        });
    }

    // ===================== LETTERBOX =====================
    private void configurarLetterbox() {
        if (palco == null || raizExterna == null) return;

        // Garante que o StackPane raiz sempre tente ocupar 100% da Scene,
        // mesmo que o FXML não tenha sido configurado com maxWidth/maxHeight=Infinity.
        // Sem isso, em alguns casos o root não cresce junto com a janela/fullscreen
        // e a largura/altura usadas no cálculo da escala ficam erradas.
        raizExterna.setMaxWidth(Double.MAX_VALUE);
        raizExterna.setMaxHeight(Double.MAX_VALUE);

        // BUG CORRIGIDO: a Scale usava pivô em (0,0) - o canto superior esquerdo do palco.
        // Isso faz a escala "encolher" o conteúdo a partir do canto, enquanto o StackPane
        // centraliza o palco baseado no tamanho de LAYOUT (900x700, sem escala). Resultado:
        // um descompasso entre onde o StackPane acha que o palco está e onde ele realmente
        // aparece na tela, gerando o deslocamento ("elementos tortos/fora do lugar") sempre
        // que o fator de escala é diferente de 1 - ou seja, fora da tela cheia. Usando o
        // pivô no CENTRO do palco (450,350 = metade de 900x700), a escala encolhe/cresce
        // simetricamente a partir do centro, que é exatamente onde o StackPane já posiciona
        // o palco - eliminando o deslocamento.
        Scale escala = new Scale(1, 1, LARGURA_PALCO / 2.0, ALTURA_PALCO / 2.0);
        palco.getTransforms().add(escala);

        ChangeListener<Number> recalcular = (obs, oldV, newV) -> aplicarEscalaLetterbox(escala);

        raizExterna.widthProperty().addListener(recalcular);
        raizExterna.heightProperty().addListener(recalcular);

        // BUG CORRIGIDO: se o Stage entra/sai do fullscreen DEPOIS que esta tela já
        // carregou (ex.: setFullScreen(true) chamado em outro momento do fluxo),
        // o width/height do raizExterna pode levar um instante para refletir o novo
        // tamanho, ou disparar antes do SO terminar a transição. Escutar diretamente
        // a propriedade fullScreenProperty() do Stage garante um recálculo extra
        // assim que a transição de tela cheia terminar.
        Platform.runLater(() -> {
            aplicarEscalaLetterbox(escala);
            if (raizExterna.getScene() != null && raizExterna.getScene().getWindow() instanceof javafx.stage.Stage) {
                javafx.stage.Stage stage = (javafx.stage.Stage) raizExterna.getScene().getWindow();
                stage.fullScreenProperty().addListener((obs, oldV, newV) ->
                    Platform.runLater(() -> aplicarEscalaLetterbox(escala))
                );
            }
        });
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
            if (url != null && iv != null) {
                iv.setImage(new Image(url.toExternalForm()));
            } else {
                System.out.println("Recurso nao encontrado: " + path);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar recurso: " + path + " -> " + e.getMessage());
        }
    }

    private void atualizarUI() {
        if (jogador == null) return;

        int hpMax = jogador.getHpMaximo() > 0 ? jogador.getHpMaximo() : 100;
        if (labelHpJogador != null) labelHpJogador.setText("HP: " + jogador.getHpAtual() + "/" + hpMax);
        if (barraHpJogador != null) barraHpJogador.setProgress((double) jogador.getHpAtual() / hpMax);
        if (labelLvlJogador != null) labelLvlJogador.setText("DIFICULDADE VILAO: LVL " + episodioResolvido);

        if (vilao != null && barraHpVilao != null) {
            int hpMaxVilao = vilao.getHpMaximo() > 0 ? vilao.getHpMaximo() : 1;
            barraHpVilao.setProgress((double) vilao.getHpAtual() / hpMaxVilao);
        }

        if (btnAtaqueSuper != null) {
            btnAtaqueSuper.setText("SUPER (" + Math.min(100, (superAcumulado * 100) / 120) + "%)");
            btnAtaqueSuper.setDisable(superAcumulado < 120);
        }

        if (btnSuplemento != null) {
            btnSuplemento.setText("SUPLEMENTO (" + jogador.getSuplementos() + ")");
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
                String nomeVilao = (vilao != null && vilao.getNome() != null) ? vilao.getNome() : "VILAO";
                if (labelDialogoVilao != null) labelDialogoVilao.setText(falaCompleta.replace("VILAO:", nomeVilao + ":"));
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
            labelStatusTurno.setText("O que o Aranha vai fazer?");
        }
        setBotoesAcaoBloqueados(false);
        if (caixaCombateUndertale != null) {
            Platform.runLater(() -> caixaCombateUndertale.requestFocus());
        }
    }

    private void alternarMenusSubinferiores(HBox painelAlvo) {
        if (painelBotoesLuta != null) painelBotoesLuta.setVisible(painelAlvo == painelBotoesLuta);
        if (painelSubAtaques != null) painelSubAtaques.setVisible(painelAlvo == painelSubAtaques);
        if (painelSubRecuperar != null) painelSubRecuperar.setVisible(painelAlvo == painelSubRecuperar);
    }

    @FXML private void mostrarOpcoesAtaque() { alternarMenusSubinferiores(painelSubAtaques); }
    @FXML private void mostrarOpcoesRecuperar() { alternarMenusSubinferiores(painelSubRecuperar); }

    @FXML
    public void voltarParaMenuLuta() {
        alternarMenusSubinferiores(painelBotoesLuta);
        if (labelStatusTurno != null) {
            labelStatusTurno.setVisible(true);
            labelStatusTurno.setText("O que o Aranha vai fazer?");
        }
    }

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

    // ===================== HITBOX ESTILO UNDERTALE =====================
    private void construirHitboxRitmo() {
        if (containerHitbox == null) return;
        containerHitbox.getChildren().clear();

        double larguraTotal = HITBOX_LARGURA_TOTAL;
        double alturaFaixa = 60.0;
        double centro = larguraTotal / 2.0;

        double meiaVerde = larguraFaixaVerde / 2.0;
        double larguraAmarelo = 90.0;
        double xVerdeInicio = centro - meiaVerde;
        double xVerdeFim = centro + meiaVerde;
        double xAmareloEsqInicio = Math.max(0, xVerdeInicio - larguraAmarelo);
        double xAmareloDirFim = Math.min(larguraTotal, xVerdeFim + larguraAmarelo);

        Rectangle vermelhoEsq = new Rectangle(0, 0, xAmareloEsqInicio, alturaFaixa);
        vermelhoEsq.setFill(Color.web("#cc1111"));
        Rectangle amareloEsq = new Rectangle(xAmareloEsqInicio, 0, xVerdeInicio - xAmareloEsqInicio, alturaFaixa);
        amareloEsq.setFill(Color.web("#e6c200"));
        Rectangle verde = new Rectangle(xVerdeInicio, 0, larguraFaixaVerde, alturaFaixa);
        verde.setFill(Color.web("#22cc44"));
        Rectangle amareloDir = new Rectangle(xVerdeFim, 0, xAmareloDirFim - xVerdeFim, alturaFaixa);
        amareloDir.setFill(Color.web("#e6c200"));
        Rectangle vermelhoDir = new Rectangle(xAmareloDirFim, 0, larguraTotal - xAmareloDirFim, alturaFaixa);
        vermelhoDir.setFill(Color.web("#cc1111"));

        for (Rectangle faixa : new Rectangle[]{vermelhoEsq, amareloEsq, vermelhoDir, amareloDir}) {
            faixa.setArcWidth(0);
        }

        containerHitbox.getChildren().addAll(vermelhoEsq, amareloEsq, verde, amareloDir, vermelhoDir);

        if (barraMarcadorRitmo != null) {
            barraMarcadorRitmo.toFront();
        }
    }

    private void dispararLoopRitmo() {
        if (labelStatusTurno != null) labelStatusTurno.setVisible(false);
        if (painelMinigameAtaque == null || caixaCombateUndertale == null || barraMarcadorRitmo == null) return;

        setBotoesAcaoBloqueados(true);
        if (painelSubAtaques != null) painelSubAtaques.setVisible(false);
        painelMinigameAtaque.setVisible(true);
        caixaCombateUndertale.setVisible(true);
        construirHitboxRitmo();

        posicaoMarcador = HITBOX_X_INICIO;
        indoParaDireita = true;
        barraMarcadorRitmo.setLayoutX(posicaoMarcador);

        if (loopMinigame != null) loopMinigame.stop();

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
                barraMarcadorRitmo.setLayoutX(posicaoMarcador);
            }
        };
        loopMinigame.start();
    }

    // ===================== CONTROLE DE TECLADO =====================
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
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 75 : 35;
            if (labelStatusTurno != null) labelStatusTurno.setText("ACERTO PERFEITO!");
            if (tipoAtaqueAtual.equals("ONDA")) {
                superAcumulado = Math.min(120, superAcumulado + 40);
            } else {
                superAcumulado = 0;
            }
        } else if (margemErro <= meiaVerde + 90.0) {
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 45 : 20;
            if (labelStatusTurno != null) labelStatusTurno.setText("Bom impacto! Causou " + danoFinal + " de dano.");
            superAcumulado = tipoAtaqueAtual.equals("ONDA") ? Math.min(120, superAcumulado + 25) : 0;
        } else {
            if (labelStatusTurno != null) labelStatusTurno.setText("Voce falhou no tempo!");
            if (tipoAtaqueAtual.equals("SUPER")) superAcumulado = 0;
        }

        if (danoFinal > 0 && vilao != null) {
            vilao.setHpAtual(Math.max(0, vilao.getHpAtual() - danoFinal));
            animarDanoInimigo(danoFinal);
        }

        atualizarUI();

        if (vilao != null && vilao.getHpAtual() <= 0) {

            if (jogador != null) jogador.ganharSuplemento();

            // BUG CORRIGIDO: vencer uma luta nunca chamava jogador.adicionarXp(), então o
            // XP do jogador ficava sempre em 0 (mesmo zerando o save, nunca havia ganho).
            // Usa o XP cadastrado pro vilão (vilao.getXpRecompensa()); se o banco não tiver
            // esse valor preenchido (0), cai num valor padrão crescente por episódio para
            // que a recompensa não fique nula mesmo com dados incompletos no banco.
            if (jogador != null) {
                int xpGanho = (vilao.getXpRecompensa() > 0) ? vilao.getXpRecompensa() : (episodioResolvido * 50);
                jogador.adicionarXp(xpGanho);
            }

            if (jogador != null) {
                if (episodioResolvido == 1) {
                    jogador.setDerrotouAbutre(true);
                } else if (episodioResolvido == 2) {
                    jogador.setDerrotouShocker(true);
                } else if (episodioResolvido == 3) {
                    jogador.setDerrotouLagarto(true);
                }
            }

            // BUG CORRIGIDO: vencer o vilão só atualizava flags em memória no objeto
            // Jogador (setDerrotouAbutre/Shocker/Lagarto), mas nada gravava isso no
            // banco na tabela fases_progresso, que é o que o ModoHistoriaController
            // realmente lê para decidir quais episódios mostrar desbloqueados. Por
            // isso vencer o episódio 1 nunca liberava o episódio 2 na tela de
            // seleção. Aqui chamamos FaseProgressoDAO.desbloquearEpisodio() para o
            // PRÓXIMO episódio (episodioResolvido + 1), que é o método que já existe
            // no DAO para isso.
            //
            // BUG CORRIGIDO: pelo mesmo motivo, o XP ganho com adicionarXp() só existia
            // em memória - nunca era salvo, por isso "zerar o jogo" sempre mostrava XP 0.
            // Agora chamamos JogadorDAO.atualizar(jogador), que já salva xp_atual, hp_atual
            // e nivel_atual de uma vez. Ambas as gravações rodam na mesma thread em segundo
            // plano, para não travar a animação de vitória esperando o banco responder.
            int proximoEpisodio = episodioResolvido + 1;
            if (jogador != null) {
                final int idJogadorFinal = jogador.getIdJogador();
                final Jogador jogadorParaSalvar = jogador;
                final int proximoEpisodioFinal = proximoEpisodio;
                Thread threadGravacao = new Thread(() -> {
                    try {
                        new JogadorDAO().atualizar(jogadorParaSalvar);
                    } catch (SQLException ex) {
                        System.err.println("Erro ao salvar progresso do jogador (XP/HP/nivel): " + ex.getMessage());
                    }
                    if (proximoEpisodioFinal <= 3) {
                        try {
                            new FaseProgressoDAO().desbloquearEpisodio(idJogadorFinal, proximoEpisodioFinal);
                        } catch (SQLException ex) {
                            System.err.println("Erro ao desbloquear episodio " + proximoEpisodioFinal + ": " + ex.getMessage());
                        }
                    }
                });
                threadGravacao.setDaemon(true);
                threadGravacao.start();
            }

            if (labelStatusTurno != null)
                labelStatusTurno.setText("Vitoria! O vilao foi derrotado.");

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
        if (jogador == null) return;
        jogador.setHpAtual(Math.min(jogador.getHpMaximo(), jogador.getHpAtual() + 15));
        if (labelStatusTurno != null) labelStatusTurno.setText("Sentido Aranha ativado! +15 HP.");
        concluirAcaoSuporte();
    }

    @FXML
    private void usarSuplemento() {
        if (jogador == null) return;
        if (jogador.usarSuplemento()) {
            if (labelStatusTurno != null)
                labelStatusTurno.setText("Voce recuperou +15 HP.");

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
        explosoesJaDetonadas.clear();

        if (caixaCombateUndertale != null) {
            caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
        }
        elementosAtaqueAtivos.clear();

        executarAtaqueEspecificoVilao();
    }

    private void executarAtaqueEspecificoVilao() {
        int tipoAtaque = random.nextInt(3) + 1;

        if (loopBatalha != null) loopBatalha.stop();

        loopBatalha = new AnimationTimer() {
            private double tempoDecorrido = 0;

            @Override
            public void handle(long now) {
                tempoDecorrido += 0.016;

                // Velocidade de esquiva aumentada (4.0 -> 5.5): combinado com a frequência
                // reduzida dos ataques do Shocker/Lagarto, dá tempo real de reagir e desviar.
                double velocidadePlayer = 5.5;
                if (iconeCoracaoAranha != null && caixaCombateUndertale != null) {
                    double margemSeguranca = 6.0;
                    double larguraIcone = iconeCoracaoAranha.getFitWidth();
                    double alturaIcone = iconeCoracaoAranha.getFitHeight();
                    double limiteEsquerdoJogador = margemSeguranca;
                    double limiteDireitoJogador = caixaCombateUndertale.getWidth() - larguraIcone - margemSeguranca;
                    double limiteSuperiorJogador = margemSeguranca;
                    double limiteInferiorJogador = caixaCombateUndertale.getHeight() - alturaIcone - margemSeguranca;

                    if (cima && iconeCoracaoAranha.getLayoutY() > limiteSuperiorJogador) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() - velocidadePlayer);
                    if (baixo && iconeCoracaoAranha.getLayoutY() < limiteInferiorJogador) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() + velocidadePlayer);
                    if (esquerda && iconeCoracaoAranha.getLayoutX() > limiteEsquerdoJogador) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() - velocidadePlayer);
                    if (direita && iconeCoracaoAranha.getLayoutX() < limiteDireitoJogador) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() + velocidadePlayer);
                }

                if (episodioResolvido == 1) {
                    processarAtaquesAbutre(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 2) {
                    processarAtaquesShocker(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 3) {
                    processarAtaquesLagarto(tipoAtaque, tempoDecorrido);
                }

                verificarColisoesEImpacto();

                // Dificuldade aumentada (pedido do usuário): turno do vilão de 4s -> 6s,
                // dando mais tempo de ataques contínuos sem remover os limites de
                // quantidade simultânea (que é o que mantém a esquiva justa/possível).
                if (tempoDecorrido >= 6.0) {
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

    // Conjunto auxiliar: marca quais nós de explosão já causaram dano (telégrafo -> explosão real)
    private java.util.Set<javafx.scene.Node> explosoesJaDetonadas = new java.util.HashSet<>();

    private void processarAtaquesShocker(int tipo, double tempo) {
        // Dificuldade aumentada (pedido do usuário): frequências e limites de
        // quantidade simultânea subiram um pouco em relação à versão anterior,
        // mantendo os mesmos sprites e o telégrafo da explosão intactos.

        // TIPO 1: raio vindo da direita
        if (tipo == 1 && Math.random() < 0.018 && elementosAtaqueAtivos.size() < 5) {
            ImageView raio = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque2-shocker.png", 50, 50);
            if (raio != null) {
                raio.setLayoutX(640);
                raio.setLayoutY(random.nextInt(110) + 10);
                adicionarObjetoAoPainel(raio);
            }
        }

        // TIPO 2: explosões com telégrafo. Antes só disparava uma vez por turno
        // (tempo < 0.05); agora repete a cada ~2s do turno (mais ondas de explosão),
        // mantendo a mesma lógica de telégrafo (cresce -> só aí causa dano).
        boolean momentoDeNovaOndaExplosao = (tempo < 0.05) || (Math.abs(tempo % 2.0) < 0.05);
        if (tipo == 2 && momentoDeNovaOndaExplosao && elementosAtaqueAtivos.size() < 6) {
            for (int i = 0; i < 3; i++) {
                ImageView exp = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque-explosao-shocker.png", 10, 10);
                if (exp != null) {
                    exp.setLayoutX(120 + (i * 180));
                    exp.setLayoutY(40);
                    exp.setOpacity(0.35); // começa como "aviso" translúcido
                    adicionarObjetoAoPainel(exp);
                }
            }
        }

        // TIPO 3: cometas caindo em diagonal
        if (tipo == 3 && Math.random() < 0.028 && elementosAtaqueAtivos.size() < 5) {
            ImageView cometa = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque-shocker.png", 45, 45);
            if (cometa != null) {
                cometa.setLayoutX(random.nextInt(550) + 20);
                cometa.setLayoutY(-50);
                adicionarObjetoAoPainel(cometa);
            }
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (node instanceof ImageView) {
                ImageView iv = (ImageView) node;
                String caminho = iv.getImage() != null ? iv.getImage().getUrl() : "";

                if (caminho != null && caminho.contains("ataque2-shocker")) {
                    // Raio: anda da direita pra esquerda
                    iv.setLayoutX(iv.getLayoutX() - 4.5);
                } else if (caminho != null && caminho.contains("ataque-explosao-shocker")) {
                    // Explosão: cresce lentamente (telégrafo). Ao chegar no tamanho final,
                    // marca como "detonada" pela primeira vez - é esse instante que conta pra dano.
                    if (iv.getFitWidth() < 70) {
                        double novoTamanho = iv.getFitWidth() + 1.1;
                        iv.setFitWidth(novoTamanho);
                        iv.setFitHeight(novoTamanho);
                        iv.setOpacity(Math.min(1.0, 0.35 + (novoTamanho / 70.0) * 0.65));
                    } else if (!explosoesJaDetonadas.contains(iv)) {
                        explosoesJaDetonadas.add(iv);
                    }
                } else if (caminho != null && caminho.contains("ataque-shocker")) {
                    // Cometa: cai em diagonal (desce e desliza levemente pra esquerda)
                    iv.setLayoutY(iv.getLayoutY() + 4.5);
                    iv.setLayoutX(iv.getLayoutX() - 1.5);
                }
            }
        }
    }

    private void processarAtaquesLagarto(int tipo, double tempo) {
        // Dificuldade aumentada (pedido do usuário): frequências e limites de
        // quantidade simultânea subiram em relação à versão anterior, mantendo o
        // limite de quantidade no tipo 2 (que corrigiu o bug do "spam impossível").
        if (tipo == 1 && Math.random() < 0.018 && elementosAtaqueAtivos.size() < 5) {
            ImageView gosma = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 30, 30);
            if (gosma != null) { gosma.setLayoutX(random.nextInt(580) + 20); gosma.setLayoutY(-30); adicionarObjetoAoPainel(gosma); }
        }
        if (tipo == 2 && Math.random() < 0.02 && elementosAtaqueAtivos.size() < 4) {
            ImageView corrida = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 40, 20);
            if (corrida != null && iconeCoracaoAranha != null) { corrida.setLayoutX(-40); corrida.setLayoutY(iconeCoracaoAranha.getLayoutY()); adicionarObjetoAoPainel(corrida); }
        }
        // Tipo 3 agora repete a cada ~2.2s do turno (antes só disparava uma vez, tempo < 0.05)
        boolean momentoDeNovoArco = (tempo < 0.05) || (Math.abs(tempo % 2.2) < 0.05);
        if (tipo == 3 && momentoDeNovoArco && elementosAtaqueAtivos.size() < 4) {
            Rectangle arco = new Rectangle(100, 12, Color.LIMEGREEN);
            arco.setLayoutX(250); arco.setLayoutY(60);
            adicionarObjetoAoPainel(arco);
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (tipo == 1) node.setLayoutY(node.getLayoutY() + 3.2);
            else if (tipo == 2) node.setLayoutX(node.getLayoutX() + 6);
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
        if (iconeCoracaoAranha == null || jogador == null) return;
        for (javafx.scene.Node node : elementosAtaqueAtivos) {

            // BUG CORRIGIDO (pedido do usuário): a explosão do Shocker tinha que dar tempo
            // do jogador fugir ANTES de explodir de verdade. Enquanto a imagem ainda está
            // "crescendo" (telégrafo), ela não causa dano - só conta como ataque de verdade
            // depois que chega no tamanho final e é marcada em explosoesJaDetonadas.
            if (node instanceof ImageView) {
                ImageView iv = (ImageView) node;
                String caminho = iv.getImage() != null ? iv.getImage().getUrl() : "";
                boolean ehExplosaoShocker = caminho != null && caminho.contains("ataque-explosao-shocker");
                if (ehExplosaoShocker && !explosoesJaDetonadas.contains(iv)) {
                    continue; // ainda em telégrafo, não causa dano
                }
            }

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

        if (jogador != null && jogador.getHpAtual() <= 0) {
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
    @FXML private void voltarMenuInicial() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml"); }
}