package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.model.FaseProgresso;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class GaleriaController implements Initializable {

    @FXML
    private GridPane gridGaleria;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SessaoJogo.get().getJogador() != null) {
            int idJogador = SessaoJogo.get().getJogador().getIdJogador();
            FaseProgressoDAO fpDao = new FaseProgressoDAO();
            
            try {
                // CORRIGIDO: Nome do método correto do seu DAO
                List<FaseProgresso> progresso = fpDao.listarPorJogador(idJogador);

                for (int i = 0; i < progresso.size(); i++) {
                    FaseProgresso fase = progresso.get(i);
                    // CORRIGIDO: Nome do método do seu Model
                    int episodio = fase.getNumeroEpisodio(); 
                    
                    StackPane painelItem = new StackPane();
                    painelItem.setAlignment(Pos.CENTER);
                    
                    ImageView imgConquista = new ImageView();
                    imgConquista.setFitHeight(110);
                    imgConquista.setFitWidth(95);
                    imgConquista.setPreserveRatio(true);

                    // CORRIGIDO: Se NÃO está bloqueado (!bloqueado), mostra a imagem da vitória!
                    if (!fase.isBloqueado()) {
                        String caminhoLuta = "/com/mycompany/entreSombrasETeias/jogo/imagens/episodio" + episodio + "Conquista.jpg";
                        try {
                            URL resource = getClass().getResource(caminhoLuta);
                            if (resource != null) {
                                imgConquista.setImage(new Image(resource.toExternalForm()));
                            }
                        } catch (Exception e) {
                            System.out.println("Imagem da conquista do episódio " + episodio + " não pôde ser carregada.");
                        }
                        painelItem.getChildren().add(imgConquista);
                    } else {
                        // Se está bloqueado, adiciona o cadeado por cima
                        ImageView cadeado = new ImageView();
                        try {
                            URL resourceCadeado = getClass().getResource("/com/mycompany/entreSombrasETeias/jogo/imagens/icocadeado.jpg");
                            if (resourceCadeado != null) {
                                cadeado.setImage(new Image(resourceCadeado.toExternalForm()));
                            }
                        } catch (Exception e) {
                            System.out.println("Ícone de cadeado não encontrado.");
                        }
                        cadeado.setFitHeight(45);
                        cadeado.setFitWidth(45);
                        cadeado.setPreserveRatio(true);
                        
                        painelItem.getChildren().addAll(imgConquista, cadeado);
                    }

                    gridGaleria.add(painelItem, i % 4, i / 4);
                }
            } catch (SQLException e) {
                System.out.println("Erro ao buscar dados do progresso para a galeria: " + e.getMessage());
            }
        }
    }

    @FXML
    public void voltarMenu() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}