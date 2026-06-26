package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.net.URL;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    @FXML private Label labelNome;
    @FXML private Label labelHP;
    @FXML private Label labelXP;
    @FXML private ListView<String> listaItens;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Jogador j = SessaoJogo.get().getJogador();
        if (j == null) return;
        labelNome.setText("Herói: " + j.getNome());
        labelHP.setText("HP: " + j.getHpAtual());
        labelXP.setText("XP: " + j.getXpAtual());
        
        listaItens.getItems().clear();
        listaItens.getItems().addAll(
            "🕷️ Traje Clássico do Homem-Aranha",
            "🧪 Fluido de Teia Reserva",
            "🕸 Lançador de Teias Avançado"
        );
    }
    
    @FXML
    public void fechar() {
        listaItens.getScene().getWindow().hide();
    }
}