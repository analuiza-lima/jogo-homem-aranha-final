package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.VilaoDAO;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.model.Vilao;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GameplayController implements Initializable {

    @FXML private Label labelVilaoNome;
    @FXML private Label labelHpJogador;
    @FXML private Label labelLvlJogador;
    @FXML private Label labelStatusTurno;
    @FXML private Label labelDialogoVilao;
    @FXML private Label labelDialogoPeter;
    @FXML private Label labelSuperStatus;
    @FXML private Label labelDanoPopup;
    
    @FXML private ImageView iconeCoracaoAranha;
    @FXML private ProgressBar barraHpJogador;
    @FXML private ProgressBar barraHpVilao;
    @FXML private ImageView imgVilao;
    @FXML private ImageView imgPeterIcone; 
    
    @FXML private StackPane containerVilao;
    @FXML private AnchorPane painelBalaoVilao;
    @FXML private AnchorPane painelBalaoPeter;
    @FXML private AnchorPane caixaCombateUndertale;
    @FXML private AnchorPane painelMinigameAtaque;
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
    private double posicaoMarcador = 20.0;
    private boolean indoParaDireita = true;
    private int superAcumulado = 0;
    private String tipoAtaqueAtual = "ONDA";

    private int episodioResolvido = 1;
    private int quantidadeSuplementos = 3; 

    private List<javafx.scene.Node> elementosAtaqueAtivos = new ArrayList<>();
    private Random random = new Random();
    
    // Matriz contendo o Contexto Narrativo seguido do Diálogo Obrigatório de cada chefe
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
        },
        // EPISÓDIO 4: ELECTRO
        {
            "NARRATIVA: A cidade começou a sofrer apagões em sequência durante a noite.",
            "NARRATIVA: Peter seguiu a origem da energia até uma subestação elétrica isolada.",
            "NARRATIVA: Faíscas iluminavam o local de forma instável e perigosa.",
            "NARRATIVA: No centro, um homem envolto em eletricidade absorvia energia da rede.",
            "NARRATIVA: Assim que percebeu a presença do herói, a energia explodiu ao redor.",
            "VILAO: Olha só… finalmente você está prestando atenção em mim.",
            "PETER: Difícil ignorar quando você tá fritando a cidade.",
            "VILAO: Agora todo mundo vai saber quem eu sou!",
            "PETER: Não sei se esse é o melhor jeito de ficar conhecido.",
            "VILAO: Você não é páreo para mim! Se não quer morrer, eu te aconselho a ir embora agora, criança.",
            "PETER: Tô suicida hoje, pode vir."
        },
        // EPISÓDIO 5: DOUTOR OCTOPUS
        {
            "NARRATIVA: Após rastrear atividades suspeitas, Peter chegou a um laboratório secreto.",
            "NARRATIVA: Equipamentos avançados estavam sendo usados de forma imprudente.",
            "NARRATIVA: No centro da sala, uma figura manipulava braços mecânicos com precisão absurda.",
            "NARRATIVA: Peter reconheceu imediatamente o cientista por trás daquilo tudo.",
            "NARRATIVA: Sem hesitar, os tentáculos se voltaram contra ele.",
            "VILAO: Você nunca entendeu o que é verdadeiro intelecto.",
            "PETER: Ah, entendi sim. Só não uso para o mal.",
            "VILAO: Você desperdiça seu potencial.",
            "PETER: E você usa o seu muito mal.",
            "VILAO: Eu sou superior a você em todos os aspectos.",
            "PETER: Então vem X1."
        },
        // EPISÓDIO 6: DUENDE VERDE
        {
            "NARRATIVA: Durante a noite, uma explosão iluminou o céu da cidade.",
            "NARRATIVA: Peter correu até o local e encontrou destruição espalhada por todos os lados.",
            "NARRATIVA: Entre a fumaça, uma figura surgia sobre um planador, observando tudo com calma.",
            "NARRATIVA: Aquela presença… ele sabia exatamente quem era.",
            "NARRATIVA: Antes que pudesse reagir, a receita de uma risada ecoou e uma bomba foi lançada em sua direção.",
            "VILAO: Olá, Peter, quanto tempo… sentiu minha falta?",
            "PETER: Eu tava torcendo pra nunca mais te ver.",
            "VILAO: Sério? Mas nós sempre acabamos cruzando os caminhos novamente. Talvez seja um sinal. Já estava começando a sentir saudade de você.",
            "PETER: Chega. Isso acaba hoje.",
            "VILAO: Você ainda acha que pode vencer?",
            "PETER: Eu não acho. Eu sei."
        }
    };

    private int indiceDialogo = 0;
    private String[] dialogoAtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jogador = SessaoJogo.get().getJogador();
        String vilaoLogado = SessaoJogo.get().getVilaoAtual();
        
        if (vilaoLogado != null && !vilaoLogado.isEmpty()) {
            switch (vilaoLogado.toLowerCase()) {
                case "abutre":   episodioResolvido = 1; break;
                case "shocker":  episodioResolvido = 2; break;
                case "lagarto":  episodioResolvido = 3; break;
                case "electro":  episodioResolvido = 4; break;
                case "octopus":  episodioResolvido = 5; break;
                case "duende":   episodioResolvido = 6; break;
                default:         episodioResolvido = 1; break;
            }
        } else {
            episodioResolvido = (jogador != null) ? jogador.getNivelAtual() : 1;
        }

        try {
            vilao = new VilaoDAO().buscarPorEpisodio(episodioResolvido);
        } catch (SQLException e) {
            vilao = new Vilao();
            vilao.setHpMaximo(100);
            vilao.setHpAtual(100);
            vilao.setNome(vilaoLogado != null ? vilaoLogado.toUpperCase() : "VILÃO");
        }

        if (labelVilaoNome != null) labelVilaoNome.setText(vilao.getNome());
        
        carregarImagemComponente(iconeCoracaoAranha, "/com/mycompany/entreSombrasETeias/jogo/imagens/homemaranhacorrendo.gif");
        carregarImagemComponente(imgPeterIcone, "/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png");
        carregarImagemComponente(iconeCoracaoAranha, "/com/mycompany/entreSombrasETeias/jogo/imagens/homemaranhacorrendo.gif");
        carregarImagemComponente(imgPeterIcone, "/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png");
        
        // === ADICIONE ESTE BLOCO NO FINAL DO INITIALIZE, APÓS CARREGARimgPeterIcone ===
        if (episodioResolvido == 1) { // ABUTRE
            // Carrega a pose ESTÁTICA/BASE de batalha do Abutre
            carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-tela-de-vilao.png");
        } else if (episodioResolvido == 2) {
          carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/shocker-tela-de-vilao.png");
        }
        else if (episodioResolvido == 3) {
        carregarImagemComponente(imgVilao, "/com/mycompany/entreSombrasETeias/jogo/imagens/-tela-de-vilao.png");

    }
        // ============================================================================
        int indiceVetor = Math.max(0, Math.min(episodioResolvido - 1, NARRATIVAS_E_DIALOGOS.length - 1));
        dialogoAtual = NARRATIVAS_E_DIALOGOS[indiceVetor];

        setBotoesAcaoBloqueados(true);
        atualizarDialogo();
        atualizarUI();
        configurarControleTeclado();
    } // fim do initialize 
    
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
        labelLvlJogador.setText("LVL: " + jogador.getNivelAtual());
        labelSuperStatus.setText("SUPER: " + superAcumulado + "/120");
        
        if (vilao != null) {
            barraHpVilao.setProgress((double) vilao.getHpAtual() / vilao.getHpMaximo());
        }

        if (episodioResolvido == 1) {
            btnSuplemento.setDisable(true);
            btnSuplemento.setText("🥤 BLOQUEADO");
        } else {
            btnSuplemento.setText("🥤 SUPLEMENTO (" + quantidadeSuplementos + ")");
            btnSuplemento.setDisable(quantidadeSuplementos <= 0);
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
                // Modo Narrativo Prata: Oculta o ícone do rosto do Peter e foca no texto do narrador
                imgPeterIcone.setVisible(false);
                painelBalaoVilao.setVisible(false);
                painelBalaoPeter.setVisible(true);
                labelDialogoPeter.setText(falaCompleta.replace("NARRATIVA:", "").trim());
            } else if (falaCompleta.startsWith("PETER:")) {
                // Fala do Homem Aranha
                imgPeterIcone.setVisible(true);
                painelBalaoVilao.setVisible(false);
                painelBalaoPeter.setVisible(true);
                labelDialogoPeter.setText(falaCompleta);
            } else {
                // Fala do Vilão correspondente da fase
                imgPeterIcone.setVisible(true);
                painelBalaoPeter.setVisible(true);
                labelDialogoPeter.setText("...");
                painelBalaoVilao.setVisible(true);
                labelDialogoVilao.setText(falaCompleta.replace("VILAO:", vilao.getNome() + ":"));
            }
        } else {
            // Fim do prólogo e início oficial da batalha
            painelBalaoPeter.setVisible(false);
            painelBalaoVilao.setVisible(false);
            imgPeterIcone.setVisible(true);
            iniciarTurnoJogador();
        }
    }

    private void iniciarTurnoJogador() {
        turnoDoJogador = true;
        alternarMenusSubinferiores(painelBotoesLuta);
        labelStatusTurno.setVisible(true);
        labelStatusTurno.setText("⭐ O que o Aranha vai fazer?");
        setBotoesAcaoBloqueados(false);
        caixaCombateUndertale.requestFocus();
    }

    private void alternarMenusSubinferiores(HBox painelAlvo) {
        painelBotoesLuta.setVisible(painelAlvo == painelBotoesLuta);
        painelSubAtaques.setVisible(painelAlvo == painelSubAtaques);
        painelSubRecuperar.setVisible(painelAlvo == painelSubRecuperar);
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

    private void dispararLoopRitmo() {
        labelStatusTurno.setVisible(false);
        painelMinigameAtaque.setVisible(true);
        setBotoesAcaoBloqueados(true);
        painelSubAtaques.setVisible(false);
        
        posicaoMarcador = 20.0;
        indoParaDireita = true;

        loopMinigame = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double velocidadeMovel = 9.0;
                if (indoParaDireita) {
                    posicaoMarcador += velocidadeMovel;
                    if (posicaoMarcador >= 615) indoParaDireita = false;
                } else {
                    posicaoMarcador -= velocidadeMovel;
                    if (posicaoMarcador <= 20) indoParaDireita = true;
                }
                barraMarcadorRitmo.setLayoutX(posicaoMarcador);
            }
        };
        loopMinigame.start();
    }

    private void configurarControleTeclado() {
        caixaCombateUndertale.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;
            newScene.setOnKeyPressed(evento -> {
                switch (evento.getCode()) {
                    case UP:    cima = true; break;
                    case DOWN:  baixo = true; break;
                    case LEFT:  esquerda = true; break;
                    case RIGHT: direita = true; break;
                    case SPACE:
                    case ENTER:
                        if (painelMinigameAtaque.isVisible()) computarDanoRitmo();
                        break;
                }
            });
            newScene.setOnKeyReleased(evento -> {
                switch (evento.getCode()) {
                    case UP:    cima = false; break;
                    case DOWN:  baixo = false; break;
                    case LEFT:  esquerda = false; break;
                    case RIGHT: direita = false; break;
                }
            });
        });
    }

private void computarDanoRitmo() {
        loopMinigame.stop();
        painelMinigameAtaque.setVisible(false);
        labelStatusTurno.setVisible(true);

        double centroAlvo = 312.0;
        double margemErro = Math.abs(posicaoMarcador - centroAlvo);
        int danoFinal = 0;

        if (margemErro <= 20.0) {
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 75 : 35;
            labelStatusTurno.setText("⭐ ACERTO PERFEITO!");
            // BUG CORRIGIDO: Se era um ataque normal ("ONDA"), acumula super. Se já era o SUPER, zera ou mantém.
            if (tipoAtaqueAtual.equals("ONDA")) {
                superAcumulado = Math.min(120, superAcumulado + 40); 
            } else {
                superAcumulado = 0; // Usou o super, gasta a barra
            }
        } else if (margemErro <= 70.0) {
            danoFinal = tipoAtaqueAtual.equals("SUPER") ? 45 : 20;
            labelStatusTurno.setText("* Bom impacto! Causou " + danoFinal + " de dano.");
            superAcumulado = tipoAtaqueAtual.equals("ONDA") ? Math.min(120, superAcumulado + 25) : 0;
        } else {
            labelStatusTurno.setText("* Você falhou no tempo!");
            if (tipoAtaqueAtual.equals("SUPER")) superAcumulado = 0;
        }

        if (danoFinal > 0 && vilao != null) {
            vilao.setHpAtual(Math.max(0, vilao.getHpAtual() - danoFinal));
            animarDanoInimigo(danoFinal);
        }

        atualizarUI();

        // --- CÓDIGO DE VITÓRIA CORRIGIDO ---
        if (vilao != null && vilao.getHpAtual() <= 0) {
            labelStatusTurno.setText("⭐ Vitória! O vilão foi derrotado.");
            
            // Cria um pequeno delay para o jogador ver a animação de dano antes de mudar de tela
            Timeline vitoriaDelay = new Timeline(new KeyFrame(Duration.seconds(2.0), e -> {
                // Troca para o menu ou tela de encerramento
                SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
            }));
            vitoriaDelay.play();
            return; // Sai do método e NÃO agenda o turno do inimigo
        }

        // Só inicia o turno do inimigo se o vilão ainda estiver vivo
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    private void animarDanoInimigo(int dano) {
        labelDanoPopup.setText("-" + dano + " HP");
        labelDanoPopup.setVisible(true);
        
        // HITBOX FIXADA: Em vez de mover o containerVilao inteiro (que mexe as hitboxes de lugar),
        // nós movemos apenas o componente visual da imagem (imgVilao)!
        if (imgVilao != null) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), imgVilao);
            tt.setByX(10);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.setOnFinished(e -> {
                labelDanoPopup.setVisible(false);
                imgVilao.setTranslateX(0); // Garante que a imagem volta à posição original perfeita
            });
            tt.play();
        }
    }//fim do computar dano 

    private void animarDanoInimigo(int dano) {
        labelDanoPopup.setText("-" + dano + " HP");
        labelDanoPopup.setVisible(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), containerVilao);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> labelDanoPopup.setVisible(false));
        tt.play();
    }

    @FXML
    private void usarSentidoAranha() {
        jogador.setHpAtual(Math.min(jogador.getHpMaximo(), jogador.getHpAtual() + 15));
        labelStatusTurno.setText("* Sentido Aranha ativado! Ataques do vilão agora estão mais lentos. +15 HP.");
        concluirAcaoSuporte();
    }

    @FXML
    private void usarSuplemento() {
        if (quantidadeSuplementos > 0 && episodioResolvido > 1) {
            quantidadeSuplementos--;
            jogador.setHpAtual(Math.min(jogador.getHpMaximo(), jogador.getHpAtual() + 35));
            labelStatusTurno.setText("* Você consumiu o suplemento! Isso te enche de determinação. +35 HP.");
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
        labelStatusTurno.setVisible(false);
        iconeCoracaoAranha.setVisible(true);
        iconeCoracaoAranha.setLayoutX(300);
        iconeCoracaoAranha.setLayoutY(55);
        jaLevouDanoNesteTurno = false;

        caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
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
                if (cima && iconeCoracaoAranha.getLayoutY() > 10) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() - velocidadePlayer);
                if (baixo && iconeCoracaoAranha.getLayoutY() < 100) iconeCoracaoAranha.setLayoutY(iconeCoracaoAranha.getLayoutY() + velocidadePlayer);
                if (esquerda && iconeCoracaoAranha.getLayoutX() > 10) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() - velocidadePlayer);
                if (direita && iconeCoracaoAranha.getLayoutX() < 600) iconeCoracaoAranha.setLayoutX(iconeCoracaoAranha.getLayoutX() + velocidadePlayer);

                if (episodioResolvido == 1) { 
                    processarAtaquesAbutre(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 2) { 
                    processarAtaquesShocker(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 3) { 
                    processarAtaquesLagarto(tipoAtaque, tempoDecorrido);
                } else if (episodioResolvido == 4) { 
                    processarAtaquesElectro(tipoAtaque, tempoDecorrido);
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
                img.setLayoutX(300); img.setLayoutY(-50);
                adicionarObjetoAoPainel(img);
            } else if (tipo == 2) { 
                ImageView img = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-ataque2.png", 70, 50);
                img.setLayoutX(-80); img.setLayoutY(50);
                adicionarObjetoAoPainel(img);
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
            gosma.setLayoutX(random.nextInt(580) + 20); gosma.setLayoutY(-30);
            adicionarObjetoAoPainel(gosma);
        }
        if (tipo == 2 && Math.random() < 0.03) { 
            ImageView corrida = criarProjetilImagem("/com/mycompany/entreSombrasETeias/jogo/imagens/gosma.png", 40, 20);
            corrida.setLayoutX(-40); corrida.setLayoutY(iconeCoracaoAranha.getLayoutY());
            adicionarObjetoAoPainel(corrida);
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

    private void processarAtaquesElectro(int tipo, double tempo) {
        if (tipo == 1 && Math.random() < 0.03) { 
            Rectangle raio = new Rectangle(15, 120, Color.LIGHTCYAN);
            raio.setLayoutX(random.nextInt(600)); raio.setLayoutY(0);
            adicionarObjetoAoPainel(raio);
        }
        if (tipo == 2 && Math.random() < 0.02) { 
            Rectangle laser = new Rectangle(140, 6, Color.AQUA);
            laser.setLayoutX(640); laser.setLayoutY(random.nextInt(100) + 10);
            adicionarObjetoAoPainel(laser);
        }
        if (tipo == 3 && tempo < 0.05) { 
            for (int i = 1; i < 4; i++) {
                Rectangle linhaV = new Rectangle(4, 140, Color.YELLOW);
                linhaV.setLayoutX(i * 150); linhaV.setLayoutY(0);
                adicionarObjetoAoPainel(linhaV);
            }
        }

        for (javafx.scene.Node node : elementosAtaqueAtivos) {
            if (tipo == 2) node.setLayoutX(node.getLayoutX() - 9);
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
        caixaCombateUndertale.getChildren().add(node);
    }

    private void verificarColisoesEImpacto() {
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
        iconeCoracaoAranha.setVisible(false);
        caixaCombateUndertale.getChildren().removeAll(elementosAtaqueAtivos);
        elementosAtaqueAtivos.clear();

        if (jogador.getHpAtual() <= 0) {
            telaGameOver.setVisible(true);
        } else {
            iniciarTurnoJogador();
        }
    }

    private void setBotoesAcaoBloqueados(boolean status) {
        btnAtacarAranha.setDisable(status);
        btnRecuperar.setDisable(status);
        btnFugir.setDisable(status);
    }

    @FXML private void executarFugir() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameover.fxml"); }
    @FXML private void voltarMenuInicial() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/menu.fxml"); }
}