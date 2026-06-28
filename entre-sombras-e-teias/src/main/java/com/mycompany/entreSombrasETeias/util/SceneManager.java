package com.mycompany.entreSombrasETeias.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage stagePrincipal;

    public static void setStage(Stage stage) {
        stagePrincipal = stage;
    }

    /**
     * Troca a cena no stage principal sem perder o modo Tela Cheia (FullScreen).
     * @param fxmlPath caminho completo começando de resources,
     * ex: "/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml"
     */
    public static void trocarTela(String fxmlPath) {
        try {
            URL resource = SceneManager.class.getResource(fxmlPath);

            if (resource == null) {
                System.err.println("ERRO: O arquivo FXML não foi encontrado no caminho: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Tenta obter a Scene que já está ativa no Stage
            Scene cenaAtual = stagePrincipal.getScene();

            if (cenaAtual == null) {
                // Se for a primeira tela do jogo, cria uma nova Scene normalmente
                Scene novaCena = new Scene(root);
                stagePrincipal.setScene(novaCena);
            } else {
                // SE JÁ EXISTE UMA CENA: trocamos apenas o conteúdo interno (root) dela.
                // Isso evita que o JavaFX resete as propriedades da janela no sistema operacional!
                //
                // BUG CORRIGIDO: o código antigo, mesmo trocando só o root (o que já preserva
                // corretamente o fullscreen), em seguida forçava setFullScreen(false) e depois
                // setFullScreen(true) "pra evitar bugs de foco". Esse toggle é o que causava o
                // "saindo e voltando da tela cheia": no Windows (e na maioria dos SOs), entrar/saír
                // do fullscreen dispara uma animação de janela do próprio sistema operacional, então
                // a cada troca de tela a janela visivelmente encolhia e expandia de novo, mesmo o
                // Stage já estando em fullscreen o tempo todo. Como cenaAtual.setRoot(root) já não
                // toca no estado de fullscreen do Stage, esse reset era só desfazer e refazer um
                // estado que já estava correto — removido.
                cenaAtual.setRoot(root);
            }

            stagePrincipal.show();
        } catch (IOException e) {
            System.err.println("ERRO ao carregar a tela de destino: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Abre uma tela em um Stage separado (janela pop-up de forma segura).
     */
    public static void abrirNovoStage(String fxmlPath, String titulo) {
        try {
            URL resource = SceneManager.class.getResource(fxmlPath);

            if (resource == null) {
                System.err.println("ERRO: O arquivo FXML para o pop-up não foi encontrado: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage novoStage = new Stage();
            novoStage.setTitle(titulo);
            novoStage.setScene(new Scene(root));

            // OTIMIZAÇÃO: Define o pop-up como MODAL.
            // Isso impede que o jogador clique em botões na tela de trás enquanto o inventário estiver aberto!
            novoStage.initModality(Modality.APPLICATION_MODAL);
            novoStage.initOwner(stagePrincipal);

            novoStage.showAndWait(); // Pausa a execução da tela de trás até fechar o pop-up
        } catch (IOException e) {
            System.err.println("ERRO ao abrir nova janela: " + fxmlPath);
            e.printStackTrace();
        }
    }
}