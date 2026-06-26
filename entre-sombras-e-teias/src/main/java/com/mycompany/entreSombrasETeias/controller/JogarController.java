package com.mycompany.entreSombrasETeias.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.mycompany.entreSombrasETeias.util.SceneManager; // Importa o seu gerenciador de telas

public class JogarController {

    @FXML
    private Button btnModoHistoria;

    @FXML
    private Button btnTeiaConfrontos;

    @FXML
    void handleModoHistoria(ActionEvent event) {
        // Atualizado com o caminho completo do seu projeto Maven
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/modohistoria.fxml");
    }

    @FXML
    void handleTeiaConfrontos(ActionEvent event) {
        // Atualizado com o caminho completo do seu projeto Maven
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/teiadeconfrontos.fxml");
    }
}