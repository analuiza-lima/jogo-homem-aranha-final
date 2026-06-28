package com.mycompany.entreSombrasETeias.controller;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
public class InventarioController implements Initializable {
    @FXML
    private Label labelHP;
    @FXML
    private Label labelXP;
    @FXML
    private ListView<String> listaItens;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Jogador j = SessaoJogo.get().getJogador();
        if (j == null) {
            return;
        }
        labelHP.setText("HP: " + j.getHpAtual() + "/" + j.getHpMaximo());
        labelXP.setText("XP: " + j.getXpAtual());
        listaItens.getItems().clear();
        // BUG CORRIGIDO (pedido do usuário): suplemento agora cura +35 HP (era +15),
        // texto da lista atualizado pra refletir o valor real de Jogador.usarSuplemento().
        for (int i = 0; i < j.getSuplementos(); i++) {
            listaItens.getItems().add("► Suplemento (+35 HP)");
        }
        if (j.getSuplementos() == 0) {
            listaItens.getItems().add("(Nenhum suplemento obtido)");
        }
    }
    @FXML
    public void fechar() {
        listaItens.getScene().getWindow().hide();
    }
}