package com.mycompany.entreSombrasETeias.controller;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import javafx.fxml.FXML;

public class CreditosController {
    @FXML
    public void voltar() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}
