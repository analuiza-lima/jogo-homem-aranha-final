package com.mycompany.entreSombrasETeias.jogo;

import com.mycompany.entreSombrasETeias.util.SceneManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
 
        Font fontePixelada = Font.loadFont(
            Main.class.getResourceAsStream("/com/mycompany/entreSombrasETeias/jogo/fonte/PressStart2P-Regular.ttf"),
            10
        );
        if (fontePixelada == null) {
            System.err.println("AVISO: não foi possível carregar PressStart2P-Regular.ttf. "
                + "Verifique se o caminho /com/mycompany/entreSombrasETeias/jogo/fonte/PressStart2P-Regular.ttf "
                + "existe exatamente assim dentro de resources. Telas que usam essa fonte no CSS vão cair "
                + "para a fonte padrão do sistema.");
        } else {
            System.out.println("Fonte carregada com sucesso: " + fontePixelada.getName());
        }

        SceneManager.setStage(stage);
        stage.setTitle("Entre Sombras e Teias");
        stage.setResizable(true);

 
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}