package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.util.SceneManager;
import javafx.fxml.FXML;

public class GameOverController {
    @FXML
    public void voltarMenu() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}