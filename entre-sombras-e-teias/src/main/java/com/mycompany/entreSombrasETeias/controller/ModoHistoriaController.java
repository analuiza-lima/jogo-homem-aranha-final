package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.model.FaseProgresso;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ModoHistoriaController implements Initializable {
    @FXML private Label labelNome;
    @FXML private Label labelXP;
    @FXML private Label labelHP;
    @FXML private Button btnEp1;
    @FXML private Button btnEp2;
    @FXML private Button btnEp3;
    @FXML private Button btnEp4;
    @FXML private Button btnEp5;
    @FXML private Button btnEp6;
    
    private int episodioSelecionado = 1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Jogador j = SessaoJogo.get().getJogador();
        if (j == null) return;
        labelNome.setText(j.getNome().toUpperCase());
        labelXP.setText("XP: " + j.getXpAtual());
        labelHP.setText("HP: " + j.getHpAtual() + "/100");
        carregarFases(j.getIdJogador());
    }
    
private void carregarFases(int idJogador) {
    try {
        FaseProgressoDAO dao = new FaseProgressoDAO();
        List<FaseProgresso> fases = dao.listarPorJogador(idJogador);
        Button[] botoes = {btnEp1, btnEp2, btnEp3, btnEp4, btnEp5, btnEp6};
        
        // 1. Reset inicial: Configura tudo como bloqueado usando APENAS classes CSS
        for (Button b : botoes) {
            b.setDisable(true);
            b.setText("🔒");
            b.getStyleClass().removeAll("botao-historia-acao", "botao-historia-bloqueado");
            b.getStyleClass().add("botao-historia-bloqueado");
            
            VBox cardPai = (VBox) b.getParent();
            if (cardPai != null) {
                cardPai.getStyleClass().removeAll("card-episodio", "card-episodio-bloqueado");
                cardPai.getStyleClass().add("card-episodio-bloqueado");
            }
        }

        // 2. Desbloqueia os episódios que o jogador já liberou no banco
        for (FaseProgresso fp : fases) {
            int idx = fp.getNumeroEpisodio() - 1;
            if (idx >= 0 && idx < botoes.length) {
                Button btn = botoes[idx];
                
                if (!fp.isBloqueado()) {
                    btn.setDisable(false);
                    btn.setText("JOGAR");
                    
                    // Remove a classe de bloqueado e ativa a classe de ação (onde está o vermelho sólido)
                    btn.getStyleClass().removeAll("botao-historia-bloqueado");
                    if (!btn.getStyleClass().contains("botao-historia-acao")) {
                        btn.getStyleClass().add("botao-historia-acao");
                    }
                    
                    // Transforma o Card de cinza para o Vermelho do Wireframe
                    VBox cardPai = (VBox) btn.getParent();
                    if (cardPai != null) {
                        cardPai.getStyleClass().removeAll("card-episodio-bloqueado");
                        cardPai.getStyleClass().add("card-episodio");
                        
                        // Alterna o ícone interno para a aranha ativa
                        if (!cardPai.getChildren().isEmpty() && cardPai.getChildren().size() > 1) {
                            cardPai.getChildren().get(1).getStyleClass().removeAll("container-decorativo-teia");
                            cardPai.getChildren().get(1).getStyleClass().add("container-decorativo-aranha");
                        }
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
} //fim do carregar fases

    public void selecionarEpisodio(int ep) {
        episodioSelecionado = ep;
        SessaoJogo.get().getJogador().setNivelAtual(ep);
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameplay.fxml");
    }

    @FXML public void jogarEp1() { selecionarEpisodio(1); }
    @FXML public void jogarEp2() { selecionarEpisodio(2); }
    @FXML public void jogarEp3() { selecionarEpisodio(3); }
    @FXML public void jogarEp4() { selecionarEpisodio(4); }
    @FXML public void jogarEp5() { selecionarEpisodio(5); }
    @FXML public void jogarEp6() { selecionarEpisodio(6); }

    @FXML public void abrirUpgrades() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/upgrades.fxml"); }
    @FXML public void abrirInventario(){ SceneManager.abrirNovoStage("/com/mycompany/entreSombrasETeias/jogo/fxml/inventario.fxml", "Inventário"); }
    @FXML public void voltarMenu() { SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml"); }
}