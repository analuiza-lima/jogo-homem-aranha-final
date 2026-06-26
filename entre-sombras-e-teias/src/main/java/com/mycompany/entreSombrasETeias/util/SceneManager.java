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
     * Troca a cena no stage principal.
     * @param fxmlPath caminho completo começando de resources, 
     * ex: "/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml"
     */
    public static void trocarTela(String fxmlPath) {
        try {
            URL resource = SceneManager.class.getResource(fxmlPath);
            
            // CORRIGIDO: Validação explícita para evitar NullPointerException mascarado
            if (resource == null) {
                System.err.println("ERRO: O arquivo FXML não foi encontrado no caminho: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            stagePrincipal.setScene(scene);
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