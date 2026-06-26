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
        labelNome.setText(j.getNome());
        labelXP.setText("XP: " + j.getXpAtual());
        labelHP.setText("HP: " + j.getHpAtual());
        carregarFases(j.getIdJogador());
    }
    
    private void carregarFases(int idJogador) {
        try {
            FaseProgressoDAO dao = new FaseProgressoDAO();
            List<FaseProgresso> fases = dao.listarPorJogador(idJogador);
            Button[] botoes = {btnEp1, btnEp2, btnEp3, btnEp4, btnEp5, btnEp6};
            
            // Força todos os estados iniciais limpos como segurança
            for (Button b : botoes) {
                b.setDisable(true);
                b.setText("🔒");
                b.setStyle("-fx-background-color: #444; -fx-text-fill: #888; -fx-font-size: 10px; -fx-pref-width: 110; -fx-pref-height: 40; -fx-font-family: 'Press Start 2P';");
            }

            for (FaseProgresso fp : fases) {
                int idx = fp.getNumeroEpisodio() - 1;
                if (idx >= 0 && idx < botoes.length) {
                    Button btn = botoes[idx];
                    
                    if (!fp.isBloqueado()) {
                        btn.setDisable(false);
                        btn.setText("JOGAR");
                        // Aplica dinamicamente a estilização vermelha e o cursor hand do wireframe para os desbloqueados
                        btn.setStyle("-fx-background-color: #ff2211; -fx-text-fill: black; -fx-font-size: 10px; -fx-cursor: hand; -fx-pref-width: 110; -fx-pref-height: 40; -fx-font-family: 'Press Start 2P'; -fx-border-color: black; -fx-border-width: 1;");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    @FXML public void abrirLoja() { 
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/loja.fxml"); 
    }
    @FXML public void abrirUpgrades() { 
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/upgrades.fxml"); 
    }
    @FXML public void abrirInventario(){
        SceneManager.abrirNovoStage("/com/mycompany/entreSombrasETeias/jogo/fxml/inventario.fxml", "Inventário"); 
    }
    @FXML public void voltarMenu() { 
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}