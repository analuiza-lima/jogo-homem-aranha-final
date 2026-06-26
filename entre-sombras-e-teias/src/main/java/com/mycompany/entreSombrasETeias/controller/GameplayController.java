package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.dao.JogadorDAO;
import com.mycompany.entreSombrasETeias.dao.VilaoDAO;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.model.Vilao;
import com.mycompany.entreSombrasETeias.model.AtaqueVisual;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GameplayController implements Initializable {

    @FXML private Label      labelVilaoNome;
    @FXML private Label      labelHpVilao;
    @FXML private Label      labelHpJogador;
    @FXML private Label      labelStatusTurno;
    @FXML private Label      labelDialogoVilao;
    @FXML private Label      labelDialogoPeter;
    
    @FXML private ImageView  iconeCoracaoAranha;
    
    @FXML private ProgressBar barraHpJogador;
    @FXML private ProgressBar barraHpVilao;
    @FXML private ImageView   imgVilao;
    @FXML private ImageView   imgPeterIcone; 
    
    @FXML private AnchorPane painelBalaoVilao;
    @FXML private AnchorPane painelBalaoPeter;
    @FXML private HBox       painelBotoesLuta;
    @FXML private AnchorPane caixaCombateUndertale;

    @FXML private Button btnAtacarAranha;  
    @FXML private Button btnRecuperar;     
    @FXML private Button btnFugir;         
    @FXML private Button btnAvancarDialogo; 

    private Jogador jogador;
    private Vilao   vilao;
    private boolean turnoDoJogador = true;

    private AnimationTimer loopBatalha;
    private AtaqueVisual obstaculoAtaque;
    private boolean jaLevouDanoNesteTurno = false;
    
    private boolean cima, baixo, esquerda, direita;

    private static final String[][] DIALOGOS = {
        { "PETER: Ei, senhor… isso não é horário de voo autorizado.", "VILAO: Eu voo quando quiser, garoto.", "PETER: Não se eu puder impedir!" },
        { "VILAO: Hoje eu termino o que comecei, Homem-Aranha!", "PETER: Você começou alguma coisa?", "VILAO: Vou te transformar em poeira!", "PETER: Cara, você fala isso toda vez…" },
        { "PETER: Dr. Connors, você precisa lutar contra isso!", "VILAO: CONNORS NÃO EXISTE MAIS!", "PETER: Eu sei que ele ainda tá aí." },
        { "VILAO: Olha só… finalmente você está prestando atenção em mim.", "PETER: Difícil ignorar quando você tá fritando a cidade.", "PETER: Pode vir com tudo!" },
        { "VILAO: Você nunca entendeu o que é verdadeiro intelecto.", "PETER: Ah, entendi sim. Só não uso para o mal.", "PETER: Então vamos resolver isso." },
        { "VILAO: Olá, Peter… sentiu minha falta?", "PETER: Eu tava torcendo pra nunca mais te ver.", "PETER: Eu sei que hoje acaba!" }
    };

    private int      indiceDialogo = 0;
    private String[] dialogoAtual;
    private int      episodioResolvido = 1; // Guarda o episódio atual da partida

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jogador = SessaoJogo.get().getJogador();
        
        // Determina se o jogo usará o vilão escolhido na Teia ou o nível atual da campanha
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
            e.printStackTrace();
            return;
        }

        if (vilao != null) {
            if (labelVilaoNome != null)
                labelVilaoNome.setText(vilao.getNome());

            String nomeBase = vilao.getNome().toLowerCase().replace(" ", "");
            String[] extensoes = {".png", ".jpg", ".jpeg"};
            java.io.InputStream stream = null;

            for (String ext : extensoes) {
                String caminho = "/com/mycompany/entreSombrasETeias/jogo/imagens/"
                        + nomeBase
                        + "-tela-de-vilao"
                        + ext;

                stream = getClass().getResourceAsStream(caminho);
                if (stream != null) break;
            }

            if (stream != null && imgVilao != null) {
                try {
                    imgVilao.setImage(new Image(stream));
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try (java.io.InputStream stream = getClass().getResourceAsStream("/com/mycompany/entreSombrasETeias/jogo/imagens/homemaranhacorrendo.gif")) {
            if (stream != null) {
                iconeCoracaoAranha.setImage(new Image(stream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (java.io.InputStream stream = getClass().getResourceAsStream("/com/mycompany/entreSombrasETeias/jogo/imagens/homem-aranha-tela-de-vilao.png")) {
            if (stream != null) {
                imgPeterIcone.setImage(new Image(stream));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int indiceVetor = episodioResolvido - 1;
        if (indiceVetor < 0) indiceVetor = 0;
        if (indiceVetor >= DIALOGOS.length) indiceVetor = DIALOGOS.length - 1;

        dialogoAtual = DIALOGOS[indiceVetor];

        setBotoesAcaoBloqueados(true);
        iconeCoracaoAranha.setVisible(false);
        painelBalaoVilao.setVisible(false);
        imgVilao.setVisible(true);
        labelStatusTurno.setText("");

        atualizarDialogo();
        atualizarUI();
        configurarControleTeclado();

        javafx.application.Platform.runLater(() -> {
            caixaCombateUndertale.requestFocus();
        });
    }

    @FXML
    public void avancarDialogo() {
        indiceDialogo++;
        atualizarDialogo();
    }

    private void atualizarDialogo() {
        if (indiceDialogo < dialogoAtual.length) {
            String falaCompleta = dialogoAtual[indiceDialogo];
            if (imgPeterIcone != null) imgPeterIcone.setVisible(true); 
            
            if (falaCompleta.startsWith("PETER:")) {
                if (painelBalaoVilao != null) painelBalaoVilao.setVisible(false);
                if (painelBalaoPeter != null) painelBalaoPeter.setVisible(true);
                if (labelDialogoPeter != null) labelDialogoPeter.setText(falaCompleta);
            } else {
                if (painelBalaoPeter != null) painelBalaoPeter.setVisible(true); 
                if (labelDialogoPeter != null) labelDialogoPeter.setText("...");
                if (painelBalaoVilao != null) painelBalaoVilao.setVisible(true);
                if (labelDialogoVilao != null) labelDialogoVilao.setText(falaCompleta.replace("VILAO:", vilao.getNome() + ":"));
            }
        } else {
            if (painelBalaoPeter != null) painelBalaoPeter.setVisible(false);
            if (painelBalaoVilao != null) painelBalaoVilao.setVisible(false);
            if (imgPeterIcone != null) imgPeterIcone.setVisible(false); 
            
            if (imgVilao != null) imgVilao.setVisible(true); 
            if (painelBotoesLuta != null) painelBotoesLuta.setVisible(true);
            if (iconeCoracaoAranha != null) iconeCoracaoAranha.setVisible(true); 
            
            iniciarTurnoJogador();
        }
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
                    default: break;
                }
            });

            newScene.setOnKeyReleased(evento -> {
                switch (evento.getCode()) {
                    case UP:    cima = false; break;
                    case DOWN:  baixo = false; break;
                    case LEFT:  esquerda = false; break;
                    case RIGHT: direita = false; break;
                    default: break;
                }
            });

            caixaCombateUndertale.requestFocus();
        });
    }
    
    private void iniciarTurnoJogador() {
        turnoDoJogador = true;
        if (labelStatusTurno != null) {
            labelStatusTurno.setVisible(true);
            labelStatusTurno.setText("* Seu turno!");
        }
        setBotoesAcaoBloqueados(false);
    }

    @FXML
    public void atacarAranha() {
        if (!turnoDoJogador) return;
        
        int dano = 35; 
        vilao.receberDano(dano);
        if (labelStatusTurno != null) labelStatusTurno.setText("* Ataque realizado!");
        
        setBotoesAcaoBloqueados(true);
        atualizarUI();
        if (verificarVitoria()) return;
        
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    private void iniciarTurnoInimigo() {
        turnoDoJogador = false;
        caixaCombateUndertale.requestFocus();
        jaLevouDanoNesteTurno = false;

        if (labelStatusTurno != null) labelStatusTurno.setVisible(false);

        String[] habilidadesVilao;
        // CORREÇÃO: Lê baseado no episódio resolvido da luta atual e não na conta persistida do jogador
        switch (episodioResolvido) {
            case 1: habilidadesVilao = new String[]{"Mergulho vertical", "Passagem lateral", "Rajada simples"}; break;
            case 2: habilidadesVilao = new String[]{"Ondas horizontais", "Explosão central", "Sequência de pulsos"}; break;
            case 3: habilidadesVilao = new String[]{"Salto aleatório", "Corrida horizontal", "Cauda em arco"}; break;
            case 4: habilidadesVilao = new String[]{"Raios verticais", "Laser horizontal", "Grade elétrica"}; break;
            case 5: habilidadesVilao = new String[]{"Tentáculos laterais", "Ataque simultâneo", "Zona bloqueada"}; break;
            case 6: habilidadesVilao = new String[]{"Bombas com atraso", "Investida diagonal", "Caos aleatório"}; break;
            default: habilidadesVilao = new String[]{"Mergulho vertical"};
        }

        int indexAleatorio = new java.util.Random().nextInt(habilidadesVilao.length);
        String ataqueSorteado = habilidadesVilao[indexAleatorio];

        double larguraCaixa = caixaCombateUndertale.getPrefWidth() > 0 ? caixaCombateUndertale.getPrefWidth() : 480.0;
        double alturaCaixa = caixaCombateUndertale.getPrefHeight() > 0 ? caixaCombateUndertale.getPrefHeight() : 140.0;
        
        double xSorteado = 50 + new java.util.Random().nextInt((int)larguraCaixa - 100);

        obstaculoAtaque = new AtaqueVisual(ataqueSorteado, larguraCaixa, alturaCaixa, xSorteado);
        caixaCombateUndertale.getChildren().add(obstaculoAtaque.getFormatoVisual());

        loopBatalha = new AnimationTimer() {
            @Override
            public void handle(long agora) {
                double velocidade = 4;

                double x = iconeCoracaoAranha.getLayoutX();
                double y = iconeCoracaoAranha.getLayoutY();

                double largura = iconeCoracaoAranha.getBoundsInParent().getWidth();
                double altura = iconeCoracaoAranha.getBoundsInParent().getHeight();

                if (cima && y > 0) {
                    iconeCoracaoAranha.setLayoutY(y - velocidade);
                }

                if (baixo && y < caixaCombateUndertale.getHeight() - altura) {
                    iconeCoracaoAranha.setLayoutY(y + velocidade);
                }

                if (esquerda && x > 0) {
                    iconeCoracaoAranha.setLayoutX(x - velocidade);
                    iconeCoracaoAranha.setScaleX(-1);
                }

                if (direita && x < caixaCombateUndertale.getWidth() - largura) {
                    iconeCoracaoAranha.setLayoutX(x + velocidade);
                    iconeCoracaoAranha.setScaleX(1);
                }

                if (obstaculoAtaque != null) {
                    obstaculoAtaque.atualizarMecanica();

                    if (!jaLevouDanoNesteTurno &&
                        obstaculoAtaque.getFormatoVisual().getBoundsInParent()
                            .intersects(iconeCoracaoAranha.getBoundsInParent())) {

                        jaLevouDanoNesteTurno = true;
                        int danoVilao = 15 + (episodioResolvido * 2);
                        jogador.setHpAtual(Math.max(0, jogador.getHpAtual() - danoVilao));
                        atualizarUI();
                        verificarDerrota();
                    }

                    if (obstaculoAtaque.deveSerDestruido()) {
                        caixaCombateUndertale.getChildren().remove(obstaculoAtaque.getFormatoVisual());
                        obstaculoAtaque = null;
                        stop(); 

                        if (jogador.getHpAtual() > 0) {
                            iniciarTurnoJogador();
                        }
                    }
                }
            }
        };

        loopBatalha.start(); 
    }

    @FXML
    public void recuperar() {
        if (!turnoDoJogador) return;
        SceneManager.abrirNovoStage("/com/mycompany/entreSombrasETeias/jogo/fxml/inventario.fxml", "Inventário de Combate");
        salvarDadosProgresso();
        atualizarUI();
        setBotoesAcaoBloqueados(true);
        
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> iniciarTurnoInimigo()));
        delay.play();
    }

    @FXML
    public void fugir() {
        salvarDadosProgresso();
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameover.fxml");
    }

    private boolean verificarVitoria() {
        if (vilao.estaMorto()) {
            jogador.adicionarXp(vilao.getXpRecompensa());
            jogador.adicionarMoedas(vilao.getXpRecompensa() / 10);
            try {
                new JogadorDAO().atualizar(jogador);
                // Apenas avança o nível da campanha se ele ganhou do vilão equivalente ao nível atual dele
                if (episodioResolvido == jogador.getNivelAtual() && episodioResolvido < 6) {
                    new FaseProgressoDAO().desbloquearEpisodio(jogador.getIdJogador(), episodioResolvido + 1);
                    jogador.setNivelAtual(episodioResolvido + 1);
                    new JogadorDAO().atualizar(jogador);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/upgrades.fxml");
            return true;
        }
        return false;
    }

    private void verificarDerrota() {
        if (jogador.getHpAtual() <= 0) {
            if (loopBatalha != null) loopBatalha.stop();
            salvarDadosProgresso();
            SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameover.fxml");
        }
    }

    private void salvarDadosProgresso() {
        try {
            new JogadorDAO().atualizar(jogador);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setBotoesAcaoBloqueados(boolean estado) {
        if (btnAtacarAranha != null) btnAtacarAranha.setDisable(estado);
        if (btnRecuperar != null) btnRecuperar.setDisable(estado);
        if (btnFugir != null) btnFugir.setDisable(estado);
    }

    private void atualizarUI() {
        if (labelHpJogador != null) labelHpJogador.setText("ARANHA HP: " + jogador.getHpAtual() + "/100");
        if (labelHpVilao != null) labelHpVilao.setText("HP: " + vilao.getVidaAtual() + "/" + vilao.getVidaMaxima());
        if (barraHpJogador != null) barraHpJogador.setProgress(jogador.getHpAtual() / 100.0);
        if (barraHpVilao != null) barraHpVilao.setProgress((double) vilao.getVidaAtual() / vilao.getVidaMaxima());
    }
}