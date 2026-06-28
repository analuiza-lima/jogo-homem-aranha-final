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
    private Label labelNome;

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

        labelNome.setText("Herói: " + j.getNome());
        labelHP.setText("HP: " + j.getHpAtual() + "/" + j.getHpMaximo());
        labelXP.setText("XP: " + j.getXpAtual());

        listaItens.getItems().clear();

        for (int i = 0; i < j.getSuplementos(); i++) {
            listaItens.getItems().add("► Suplemento (+15 HP)");
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