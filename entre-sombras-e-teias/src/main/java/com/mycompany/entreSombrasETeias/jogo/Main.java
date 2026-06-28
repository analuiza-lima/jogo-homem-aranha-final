package com.mycompany.entreSombrasETeias.jogo;

import com.mycompany.entreSombrasETeias.util.SceneManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // BUG CORRIGIDO: o CSS referenciava -fx-font-family: "Press Start 2P" em
        // várias telas (inventário, modo história, etc), mas essa fonte NUNCA
        // funcionou em lugar nenhum do jogo. Diferente do CSS de navegador, o
        // JavaFX não tem @font-face: ele não carrega arquivos .ttf automaticamente
        // a partir do CSS. Sem um Font.loadFont() explícito ANTES do CSS ser
        // aplicado, "Press Start 2P" falha silenciosamente e cai para a fonte
        // padrão do sistema - exatamente o sintoma visto (texto em fonte normal,
        // sem o visual pixelado esperado). Carregando aqui, uma única vez, no
        // início da aplicação, a fonte fica disponível globalmente para QUALQUER
        // tela que a referencie no CSS depois.
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

        // BUG CORRIGIDO: setFullScreen(true) estava sendo chamado ANTES de existir
        // qualquer Scene no Stage (trocarTela só cria a Scene depois disso). Pedir
        // fullscreen num Stage ainda sem Scene é inconsistente entre SOs - no Windows
        // o resultado observado era uma janela "meio fullscreen", com a barra de
        // título do SO ainda visível e o conteúdo sendo cortado/distorcido, porque o
        // tamanho real da janela ficava diferente do que o root (StackPane) esperava
        // quando a letterbox calculava a escala.
        //
        // CORREÇÃO: primeiro trocamos para a tela do menu (o que cria a Scene e
        // dá tamanho real ao Stage), e só DEPOIS pedimos fullscreen.
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}