package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.JogadorDAO;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpgradesController implements Initializable {
    @FXML private Label labelXP;
    @FXML private Label labelHP;
    @FXML private Label labelMensagem;
    private Jogador jogador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        jogador = SessaoJogo.get().getJogador();
        atualizarLabels();
    }
    
    @FXML
    public void upgradeHP() {
    // Aumenta o HP em 20, limitando o máximo em 200
    jogador.setHpAtual(Math.min(jogador.getHpAtual() + 20, 200));
    
    salvar();
    labelMensagem.setText("❤️ +20 HP máximo adquirido!");
    atualizarLabels();
}
   @FXML
   public void upgradeDano() {
    // Futuramente: adicionar campo danoBonus no modelo se necessário
    salvar();
    labelMensagem.setText("⚔️ Dano aumentado!");
    atualizarLabels();
}
    
    @FXML
    public void upgradeVelocidade() {
      
            salvar();
            labelMensagem.setText("⚡ Velocidade aumentada!");
        
        
        atualizarLabels();
    }

    private void salvar() {
        try { 
            new JogadorDAO().atualizar(jogador); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    private void atualizarLabels() {
        labelXP.setText("XP: " + jogador.getXpAtual());
        labelHP.setText("HP: " + jogador.getHpAtual());
       
    }
    
    @FXML 
    public void continuar() {
        // CORRIGIDO: Rota exata mapeada no Maven do projeto real
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/modohistoria.fxml");
    }
}