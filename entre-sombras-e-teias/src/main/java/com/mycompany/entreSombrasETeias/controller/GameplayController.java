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

    // Efeitos temporários que valem só para o PRÓXIMO turno do inimigo, consumidos
    // (lidos e zerados) no início de executarAtaqueEspecificoVilao(). Sem essa leitura,
    // as flags eram setadas mas nunca tinham efeito real no jogo - eram só cosméticas.
    private boolean sentidoAranhaAtivoParaProximoTurno = false;
    private boolean inimigoPresoNaTeiaNoProximoTurno = false;
    // Multiplicadores efetivamente aplicados durante o turno em andamento (1.0 = normal).
    private double multiplicadorVelocidadeInimigoNoTurno = 1.0;
    private double multiplicadorDanoRecebidoNoTurno = 1.0;

    private Timeline loopVenenoTimeline;

    // BUG CORRIGIDO (pedido do usuário): random.nextInt(3)+1 puro podia, por sorte,
    // repetir o mesmo tipo de ataque várias vezes seguidas ao longo de uma luta,
    // dando a impressão de que o vilão só usava 2 dos 3 ataques disponíveis. Um
    // "shuffle bag" garante que os 3 tipos apareçam todos antes de qualquer um repetir.
    private List<Integer> sacoDeTiposDeAtaque = new ArrayList<>();

    private int sortearProximoTipoDeAtaque() {
        if (sacoDeTiposDeAtaque.isEmpty()) {
            sacoDeTiposDeAtaque.add(1);
            sacoDeTiposDeAtaque.add(2);
            sacoDeTiposDeAtaque.add(3);
            java.util.Collections.shuffle(sacoDeTiposDeAtaque, random);
        }
        return sacoDeTiposDeAtaque.remove(0);
    }

    private static final String[][] NARRATIVAS_E_DIALOGOS = {
        // EPISÓDIO 1: ABUTRE
        {
            "NARRATIVA: Em mais uma de suas patrulhas noturnas, Peter observava a cidade do alto dos prédios.",
            "NARRATIVA: Um vulto cruzou o céu rapidamente, carregando algo pesado.",
            "NARRATIVA: Peter se aproximou em silêncio, tentando entender o que estava acontecendo.",
            "NARRATIVA: As asas metálicas refletiram a luz da lua - definitivamente não era um pássaro.",
            "NARRATIVA: Antes que pudesse reagir, a figura mergulhou em sua direção.",
            "PETER: Ei, senhor... isso não é horário de voo autorizado.",
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
            "PETER: Cara, você fala isso toda vez...",
            "VILAO: E dessa vez é sério!",
            "PETER: Tá bom, se você diz."
        },
        // EPISÓDIO 3: LAGARTO
        {
            "NARRATIVA: Investigando relatos estranhos sobre uma criatura causando visível caos na cidade, Peter entrou em um laboratório aparentemente abandonado.",
            "NARRATIVA: O lugar estava revirado, com equipamentos quebrados e marcas pelas paredes.",
            "NARRATIVA: Entre os destroços, ele encontrou sinais de experimentos recentes.",
            "NARRATIVA: Então a avistou... e a reconheceu na hora.",
            "NARRATIVA: Antes que pudesse falar, o monstro avançou com violência.",
            "PETER: Dr. Connors, você precisa lutar contra isso!",
            "VILAO: CONNORS NÃO EXISTE MAIS!",
            "PETER: Eu sei que ele ainda está aí. Eu posso te ajudar.",
            "VILAO: QUANTA ARROGÂNCIA VINDA DE UM INSETO PATÉTICO COMO VOCÊ!",
            "PETER: Eu tô aqui tentando te salvar!",
            "VILAO: VOCÊ NÃO CONSEGUE SALVAR NEM A SI MESMO, MOLEQUE! DESISTA!"
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
        Jogador jogadorSessao = SessaoJogo.get().getJogador();
        String vilaoLogado = SessaoJogo.get().getVilaoAtual();

        boolean entrouPelaTeiaDeConfrontos = vilaoLogado != null && !vilaoLogado.isEmpty();

        if (entrouPelaTeiaDeConfrontos) {
            switch (vilaoLogado.toLowerCase()) {
                case "abutre":  episodioResolvido = 1; break;
                case "shocker": episodioResolvido = 2; break;
                case "lagarto": episodioResolvido = 3; break;
                default:         episodioResolvido = 1; break;
            }

            // Cria uma instância "clone" para a luta da Teia: qualquer dano tomado aqui
            // NÃO altera o HP salvo do Modo História, já que as lutas da Teia não seguem
            // ordem cronológica e cada confronto ali é independente.
            if (jogadorSessao != null) {
                this.jogador = new Jogador(jogadorSessao.getNome());
                this.jogador.setIdJogador(jogadorSessao.getIdJogador());
                this.jogador.setXpAtual(jogadorSessao.getXpAtual());
                this.jogador.setNivelAtual(jogadorSessao.getNivelAtual());
                this.jogador.setHpAtual(this.jogador.getHpMaximo());
            } else {
                this.jogador = new Jogador("Peter");
            }
        } else {
            // Modo História: usa a referência real da sessão para acumular dano/progresso sequencial
            this.jogador = jogadorSessao;
            if (this.jogador == null) {
                this.jogador = new Jogador("Peter");
            }
            episodioResolvido = this.jogador.getNivelAtual();
        }

        episodioResolvido = Math.max(1, Math.min(episodioResolvido, 3));

        try {
            vilao = new VilaoDAO().buscarPorEpisodio(episodioResolvido);
        } catch (SQLException e) {
            vilao = null;
        }

        if (vilao == null) {
            vilao = new Vilao();
            vilao.setHpMaximo(100);
            vilao.setHpAtual(100);
            vilao.setNome(vilaoLogado != null ? vilaoLogado.toUpperCase() : "VILAO");
        }

        // 1. Elementos visuais do jogador
        carregarImagemComponente(iconeCoracaoAranha, "/com/mycompany/entreSombrasETeias/jogo/imagens/miranha.png");
        carregarImagemComponente(imgPeterIcone, "/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png");

        // 2. Background (varia por episódio)
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

        raizExterna.setMaxWidth(Double.MAX_VALUE);
        raizExterna.setMaxHeight(Double.MAX_VALUE);

        Scale escala = new Scale(1, 1, LARGURA_PALCO / 2.0, ALTURA_PALCO / 2.0);
        palco.getTransforms().add(escala);

        ChangeListener<Number> recalcular = (obs, oldV, newV) -> aplicarEscalaLetterbox(escala);

        raizExterna.widthProperty().addListener(recalcular);
        raizExterna.heightProperty().addListener(recalcular);

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

            // Pedido do usuário: o Super, além do dano extra, prende o vilão numa teia -
            // o próximo turno dele fica mais lento e com menos dano (flag consumida em
            // executarAtaqueEspecificoVilao), e ainda recebe veneno (5 dano/s por 5s).
            if (tipoAtaqueAtual.equals("SUPER")) {
                inimigoPresoNaTeiaNoProximoTurno = true;
                iniciarVenenoNoVilao();
            }
        }

        atualizarUI();

        if (vilao != null && vilao.getHpAtual() <= 0) {
            processarVitoria();
            return;
        }

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    // Lógica de vitória extraída para ser reutilizável: tanto quando o golpe do
    // minigame de ritmo derruba o vilão para 0, quanto quando o veneno do Super
    // (que tickeia fora do fluxo do minigame) faz isso sozinho.
    private void processarVitoria() {
        if (jogador != null) jogador.ganharSuplemento();

        // XP ganho ao vencer: usa o XP cadastrado pro vilão; se o banco não tiver esse
        // valor preenchido (0), cai num valor padrão crescente por episódio.
        if (jogador != null && vilao != null) {
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

        int proximoEpisodio = episodioResolvido + 1;

        if (jogador != null) {
            String vilaoLogado = SessaoJogo.get().getVilaoAtual();
            boolean isTeia = vilaoLogado != null && !vilaoLogado.isEmpty();

            // Só avança o nível de progresso real (que abre o próximo episódio no Modo
            // História) quando a vitória aconteceu de fato no Modo História. Na Teia de
            // Confrontos, jogador é um "clone" só desta luta, então não tem efeito sobre
            // a sessão real mesmo - mas a flag isTeia ainda protege a gravação no banco.
            if (!isTeia && proximoEpisodio <= 3) {
                jogador.setNivelAtual(proximoEpisodio);
            }

            final int idJogadorFinal = jogador.getIdJogador();
            final Jogador jogadorParaSalvar = jogador;
            final int proximoEpisodioFinal = proximoEpisodio;
            final boolean isTeiaFinal = isTeia;

            Thread threadGravacao = new Thread(() -> {
                try {
                    new JogadorDAO().atualizar(jogadorParaSalvar);
                    if (!isTeiaFinal && proximoEpisodioFinal <= 3) {
                        new FaseProgressoDAO().desbloquearEpisodio(idJogadorFinal, proximoEpisodioFinal);
                    }
                } catch (SQLException ex) {
                    System.err.println("Erro ao salvar progresso do jogador: " + ex.getMessage());
                }
            });
            threadGravacao.setDaemon(true);
            threadGravacao.start();
        }

        if (labelStatusTurno != null) {
            labelStatusTurno.setVisible(true);
            labelStatusTurno.setText("Vitoria! O vilao foi derrotado.");
        }

        Timeline vitoriaDelay = new Timeline(new KeyFrame(Duration.seconds(2.0), e -> {
            SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
        }));

        vitoriaDelay.play();
    }

    // Chamado quando o veneno do Super derruba o vilão para 0 fora do fluxo normal
    // do minigame de ritmo. BUG CORRIGIDO: a versão anterior chamava computarDanoRitmo()
    // de novo nesse caso, o que reexecutava todo o cálculo de dano do minigame (usando a
    // posição antiga do marcador) só para chegar no bloco de vitória - instável e podia
    // causar dano duplicado. Agora chama processarVitoria() diretamente.
    private void finalizarVitoriaPorVeneno() {
        if (painelMinigameAtaque != null) painelMinigameAtaque.setVisible(false);
        if (loopMinigame != null) loopMinigame.stop();
        if (loopBatalha != null) loopBatalha.stop();
        processarVitoria();
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

        // Pedido do usuário: Sentido Aranha também desacelera os ataques do próximo
        // turno do inimigo (flag consumida em executarAtaqueEspecificoVilao).
        sentidoAranhaAtivoParaProximoTurno = true;

        if (labelStatusTurno != null) {
            labelStatusTurno.setText("Sentido Aranha ativado! +15 HP e inimigo desacelerado.");
        }

        concluirAcaoSuporte();
    }

    @FXML
    private void usarSuplemento() {
        if (jogador == null) return;
        if (jogador.usarSuplemento()) {
            if (labelStatusTurno != null)
                labelStatusTurno.setText("Voce recuperou +35 HP.");

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
        lagartosLateraisJaAtiraram.clear();

        if (caixaCombateUndertale != null) {
            caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
        }
        elementosAtaqueAtivos.clear();

        executarAtaqueEspecificoVilao();
    }

    private void executarAtaqueEspecificoVilao() {
        int tipoAtaque = sortearProximoTipoDeAtaque();

        // BUG CORRIGIDO: sentidoAranhaAtivoParaProximoTurno e inimigoPresoNaTeiaNoProximoTurno
        // eram setados em usarSentidoAranha()/computarDanoRitmo() mas nunca eram lidos em
        // lugar nenhum - a desaceleração e a redução de dano nunca aconteciam de fato.
        // Agora, no início de cada turno do inimigo, consumimos essas flags (lemos e
        // resetamos) para definir os multiplicadores que valem só para ESTE turno.
        multiplicadorVelocidadeInimigoNoTurno = 1.0;
        multiplicadorDanoRecebidoNoTurno = 1.0;

        if (sentidoAranhaAtivoParaProximoTurno) {
            // Pedido do usuário: 30-40% mais lento -> usamos 35% de redução.
            multiplicadorVelocidadeInimigoNoTurno *= 0.65;
            sentidoAranhaAtivoParaProximoTurno = false;
        }
        if (inimigoPresoNaTeiaNoProximoTurno) {
            // Vilão "preso na teia" pelo Super: ataques mais lentos E menos danosos.
            multiplicadorVelocidadeInimigoNoTurno *= 0.55;
            multiplicadorDanoRecebidoNoTurno *= 0.5;
            inimigoPresoNaTeiaNoProximoTurno = false;
            if (labelStatusTurno != null) {
                // Esse texto não fica visível durante o turno do inimigo (o status some),
                // mas garantimos que ao voltar para o turno do jogador não sobre texto preso.
            }
        }

        if (loopBatalha != null) loopBatalha.stop();

        loopBatalha = new AnimationTimer() {
            private double tempoDecorrido = 0;

            @Override
            public void handle(long now) {
                tempoDecorrido += 0.016;

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

                // Turno do inimigo dura 6s (dificuldade aumentada a pedido do usuário).
                if (tempoDecorrido >= 6.0) {
                    this.stop();
                    finalizarTurnoDefensivo();
                }
            }
        };
        loopBatalha.start();
    }

    private void processarAtaquesAbutre(int tipo, double tempo) {
        double vel = multiplicadorVelocidadeInimigoNoTurno;

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
            if (tipo == 1) node.setLayoutY(node.getLayoutY() + 5 * vel);
            else if (tipo == 2) node.setLayoutX(node.getLayoutX() + 6 * vel);
            else node.setLayoutX(node.getLayoutX() - 4 * vel);
        }
    }

    // Conjunto auxiliar: marca quais nós de explosão já causaram dano (telégrafo -> explosão real)
    private java.util.Set<javafx.scene.Node> explosoesJaDetonadas = new java.util.HashSet<>();

    private void processarAtaquesShocker(int tipo, double tempo) {
        double vel = multiplicadorVelocidadeInimigoNoTurno;

        // TIPO 1: raio vindo da direita
        if (tipo == 1 && Math.random() < 0.018 && elementosAtaqueAtivos.size() < 5) {
            ImageView raio = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/raio-shocker-ataque2.png", 50, 50);
            if (raio != null) {
                raio.setLayoutX(640);
                raio.setLayoutY(random.nextInt(110) + 10);
                adicionarObjetoAoPainel(raio);
            }
        }

        // TIPO 2: explosões com telégrafo, repetindo a cada ~2s do turno.
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

                if (caminho != null && caminho.contains("raio-shocker-ataque2")) {
                    iv.setLayoutX(iv.getLayoutX() - 4.5 * vel);
                } else if (caminho != null && caminho.contains("ataque-explosao-shocker")) {
                    if (iv.getFitWidth() < 70) {
                        double novoTamanho = iv.getFitWidth() + 1.1 * vel;
                        iv.setFitWidth(novoTamanho);
                        iv.setFitHeight(novoTamanho);
                        iv.setOpacity(Math.min(1.0, 0.35 + (novoTamanho / 70.0) * 0.65));
                    } else if (!explosoesJaDetonadas.contains(iv)) {
                        explosoesJaDetonadas.add(iv);
                    }
                } else if (caminho != null && caminho.contains("ataque-shocker")) {
                    iv.setLayoutY(iv.getLayoutY() + 4.5 * vel);
                    iv.setLayoutX(iv.getLayoutX() - 1.5 * vel);
                }
            }
        }
    }

    // Marca quais sprites de "lagarto parado nas laterais" (ataque 3) já lançaram
    // sua gosma nesta aparição, para cada um lançar uma vez só por ciclo.
    private java.util.Set<javafx.scene.Node> lagartosLateraisJaAtiraram = new java.util.HashSet<>();

    private void processarAtaquesLagarto(int tipo, double tempo) {
        double vel = multiplicadorVelocidadeInimigoNoTurno;

        // Ataques reformulados (pedido do usuário) seguindo o documento de design:
        // 1) Salto aleatório: ataque1-lagarto aparece em posição aleatória e "bate" no chão
        // 2) Corrida horizontal: VÁRIOS ataque2-lagarto atravessam rápido a tela
        // 3) Cauda em arco: 2 ataque3-lagarto ficam parados nas laterais (esquerda/direita)
        //    e lançam a gosma.png pré-existente em direção ao centro

        // TIPO 1: salto aleatório - aparece em posição aleatória e bate no chão
        if (tipo == 1 && Math.random() < 0.022 && elementosAtaqueAtivos.size() < 4) {
            ImageView salto = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque1-lagarto.png", 55, 55);
            if (salto != null) {
                salto.setLayoutX(random.nextInt(560) + 20);
                salto.setLayoutY(random.nextInt(110) + 15);
                adicionarObjetoAoPainel(salto);
            }
        }

        // TIPO 2: corrida horizontal - vários lagartos atravessando a tela ao mesmo tempo
        if (tipo == 2 && Math.random() < 0.025 && elementosAtaqueAtivos.size() < 6) {
            boolean vemDaDireita = random.nextBoolean();
            ImageView corredor = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque2-lagarto.png", 40, 40);
            if (corredor != null) {
                corredor.setLayoutX(vemDaDireita ? 650 : -45);
                corredor.setLayoutY(random.nextInt(120) + 10);
                corredor.setUserData(vemDaDireita ? "esquerda" : "direita");
                adicionarObjetoAoPainel(corredor);
            }
        }

        // TIPO 3: cauda em arco - 2 lagartos parados nas laterais (um de cada lado)
        // lançando a gosma pré-existente em direção ao centro. Repete a cada ~2.5s.
        boolean momentoDeNovaSalva = (tempo < 0.05) || (Math.abs(tempo % 2.5) < 0.05);
        if (tipo == 3 && momentoDeNovaSalva && elementosAtaqueAtivos.size() < 5) {
            ImageView lagartoEsq = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque3-lagarto.png", 50, 50);
            if (lagartoEsq != null) {
                lagartoEsq.setLayoutX(5);
                lagartoEsq.setLayoutY(60);
                lagartoEsq.setUserData("lateral-esquerda");
                adicionarObjetoAoPainel(lagartoEsq);
            }
            ImageView lagartoDir = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/ataque3-lagarto.png", 50, 50);
            if (lagartoDir != null) {
                lagartoDir.setLayoutX(595);
                lagartoDir.setLayoutY(60);
                lagartoDir.setUserData("lateral-direita");
                adicionarObjetoAoPainel(lagartoDir);
            }
        }

        // Itera sobre uma CÓPIA da lista: o loop abaixo pode adicionar uma nova gosma
        // lançada à lista original (elementosAtaqueAtivos) quando um lagarto lateral
        // ainda não atirou - modificar a lista original durante sua própria iteração
        // causaria ConcurrentModificationException.
        for (javafx.scene.Node node : new ArrayList<>(elementosAtaqueAtivos)) {
            if (!(node instanceof ImageView)) continue;
            ImageView iv = (ImageView) node;
            Object userData = iv.getUserData();

            if ("esquerda".equals(userData)) {
                iv.setLayoutX(iv.getLayoutX() - 7.0 * vel);
            } else if ("direita".equals(userData)) {
                iv.setLayoutX(iv.getLayoutX() + 7.0 * vel);
            } else if ("lateral-esquerda".equals(userData) || "lateral-direita".equals(userData)) {
                // lagarto lateral parado: lança uma gosma em direção ao centro, uma única vez
                if (!lagartosLateraisJaAtiraram.contains(iv)) {
                    lagartosLateraisJaAtiraram.add(iv);
                    boolean vemDaEsquerda = "lateral-esquerda".equals(userData);
                    ImageView gosmaLancada = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 35, 20);
                    if (gosmaLancada != null) {
                        gosmaLancada.setLayoutX(vemDaEsquerda ? 55 : 565);
                        gosmaLancada.setLayoutY(iv.getLayoutY() + 15);
                        gosmaLancada.setUserData(vemDaEsquerda ? "gosma-direita" : "gosma-esquerda");
                        adicionarObjetoAoPainel(gosmaLancada);
                    }
                }
            } else if ("gosma-direita".equals(userData)) {
                iv.setLayoutX(iv.getLayoutX() + 6.5 * vel);
            } else if ("gosma-esquerda".equals(userData)) {
                iv.setLayoutX(iv.getLayoutX() - 6.5 * vel);
            }
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

            // Explosão do Shocker em telégrafo não causa dano enquanto ainda está crescendo.
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
                    // Pedido do usuário: vilão "preso na teia" pelo Super causa MENOS dano
                    // no turno seguinte (multiplicadorDanoRecebidoNoTurno consumido aqui).
                    int danoBase = 15;
                    int danoFinal = (int) Math.round(danoBase * multiplicadorDanoRecebidoNoTurno);
                    jogador.setHpAtual(Math.max(0, jogador.getHpAtual() - danoFinal));
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

    // Pedido do usuário: veneno do Super - 5 de dano por segundo, durante 5 segundos
    // (5 ticks de 5 dano cada = 25 de dano total). Para imediatamente se o vilão
    // morrer antes do veneno terminar (não continua tickando em um vilão já derrotado).
    private void iniciarVenenoNoVilao() {
        if (vilao == null || imgVilao == null || labelDanoPopup == null) return;

        if (loopVenenoTimeline != null) {
            loopVenenoTimeline.stop();
        }

        // Deixa o vilão visualmente esverdeado durante o efeito.
        ColorAdjust efeitoVerdeVeneno = new ColorAdjust();
        efeitoVerdeVeneno.setContrast(0.3);
        efeitoVerdeVeneno.setHue(0.4);
        efeitoVerdeVeneno.setSaturation(0.8);
        imgVilao.setEffect(efeitoVerdeVeneno);

        loopVenenoTimeline = new Timeline();
        for (int tick = 1; tick <= 5; tick++) {
            KeyFrame frameVeneno = new KeyFrame(Duration.seconds(tick), event -> {
                if (vilao == null || vilao.getHpAtual() <= 0) {
                    if (loopVenenoTimeline != null) loopVenenoTimeline.stop();
                    return;
                }

                vilao.setHpAtual(Math.max(0, vilao.getHpAtual() - 5));
                atualizarUI();

                labelDanoPopup.setText("-5 HP (veneno)");
                labelDanoPopup.setStyle("-fx-text-fill: #22cc44; -fx-font-weight: bold;");
                labelDanoPopup.setVisible(true);

                double originalX = imgVilao.getTranslateX();
                Timeline tremorVeneno = new Timeline(
                    new KeyFrame(Duration.millis(0),   new KeyValue(imgVilao.translateXProperty(), originalX)),
                    new KeyFrame(Duration.millis(50),  new KeyValue(imgVilao.translateXProperty(), originalX + 6)),
                    new KeyFrame(Duration.millis(100), new KeyValue(imgVilao.translateXProperty(), originalX - 6)),
                    new KeyFrame(Duration.millis(150), new KeyValue(imgVilao.translateXProperty(), originalX))
                );
                tremorVeneno.play();

                Timeline fecharPopup = new Timeline(new KeyFrame(Duration.millis(600), e -> labelDanoPopup.setVisible(false)));
                fecharPopup.play();

                // BUG CORRIGIDO: a versão anterior chamava computarDanoRitmo() aqui dentro,
                // o que reexecutava o cálculo de dano do minigame de ritmo (usando a posição
                // antiga do marcador) só para chegar no bloco de vitória - instável e podia
                // gerar dano duplicado. Agora chama finalizarVitoriaPorVeneno() diretamente,
                // que para os loops em andamento e processa a vitória sem recalcular dano.
                if (vilao.getHpAtual() <= 0) {
                    if (loopVenenoTimeline != null) loopVenenoTimeline.stop();
                    imgVilao.setEffect(null);
                    finalizarVitoriaPorVeneno();
                }
            });
            loopVenenoTimeline.getKeyFrames().add(frameVeneno);
        }

        loopVenenoTimeline.setOnFinished(e -> {
            if (imgVilao != null) imgVilao.setEffect(null);
            if (labelDanoPopup != null) labelDanoPopup.setStyle("");
        });

        loopVenenoTimeline.play();
    }
}