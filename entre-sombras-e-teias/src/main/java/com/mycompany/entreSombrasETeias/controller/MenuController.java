package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.dao.JogadorDAO;
import com.mycompany.entreSombrasETeias.model.Jogador;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.sql.SQLException;

public class MenuController {

    @FXML
    public void jogar() {
        if (SessaoJogo.get().getJogador() == null) {
            try {
                JogadorDAO dao = new JogadorDAO();
                Jogador novo = dao.criar(new Jogador("Peter Parker"));

                FaseProgressoDAO fpDao = new FaseProgressoDAO();
                fpDao.inicializarFases(novo.getIdJogador());

                SessaoJogo.get().setJogador(novo);
                SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/jogar.fxml");
            } catch (SQLException e) {
                mostrarErro("Erro ao iniciar o jogo no banco de dados: " + e.getMessage());
            }    
        } else {
            SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/jogar.fxml");
        }
    }

    @FXML
    public void abrirGaleria() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/galeria.fxml");
    }

    @FXML
    public void abrirRanking() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/ranking.fxml");
    }

    @FXML
    public void encerrar() {
        System.exit(0);
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
}