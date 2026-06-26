package com.mycompany.entreSombrasETeias.jogo;

import com.mycompany.entreSombrasETeias.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        stage.setTitle("Entre Sombras e Teias");
        stage.setResizable(true);
        stage.setFullScreen(true);
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}