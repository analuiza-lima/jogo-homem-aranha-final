package com.mycompany.entreSombrasETeias.controller;

import com.mycompany.entreSombrasETeias.dao.FaseProgressoDAO;
import com.mycompany.entreSombrasETeias.model.FaseProgresso;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GaleriaController implements Initializable {

    @FXML
    private GridPane gridGaleria;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnProximo;

    private List<FaseProgresso> listaProgresso = new ArrayList<>();
    private int paginaAtual = 0;
    private final int ITENS_POR_PAGINA = 8; // Grid 4x2 por página conforme o Wireframe

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarDadosDoBanco();
        renderizarPagina();
    }

    private void carregarDadosDoBanco() {
        if (SessaoJogo.get().getJogador() != null) {
            int idJogador = SessaoJogo.get().getJogador().getIdJogador();
            FaseProgressoDAO fpDao = new FaseProgressoDAO();
            try {
                listaProgresso = fpDao.listarPorJogador(idJogador);
            } catch (SQLException e) {
                System.out.println("Erro ao buscar dados do progresso: " + e.getMessage());
            }
        }
    }

    private void renderizarPagina() {
        gridGaleria.getChildren().clear();

        int inicio = paginaAtual * ITENS_POR_PAGINA;
        int fim = Math.min(inicio + ITENS_POR_PAGINA, listaProgresso.size());

        int indiceGrid = 0;
        for (int i = inicio; i < fim; i++) {
            FaseProgresso fase = listaProgresso.get(i);
            
            // StackPane para conter a imagem perfeitamente posicionada no Grid
            StackPane painelItem = new StackPane();
            painelItem.setAlignment(Pos.CENTER);

            ImageView imgExibicao = new ImageView();
            imgExibicao.setFitHeight(110);
            imgExibicao.setFitWidth(95);
            imgExibicao.setPreserveRatio(false);

            if (!fase.isBloqueado()) {
                // Se o boss foi derrotado, descobrimos o arquivo correspondente pelo número do episódio
                int numEpisodio = fase.getNumeroEpisodio(); 
                String arquivoImagem = obterNomeArquivoPorEpisodio(numEpisodio);
                String caminhoArte = "/com/mycompany/entreSombrasETeias/jogo/imagens/" + arquivoImagem;
                
                try (InputStream is = getClass().getResourceAsStream(caminhoArte)) {
                    if (is != null) {
                        imgExibicao.setImage(new Image(is));
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao carregar a imagem de conquista: " + caminhoArte);
                }
                painelItem.getChildren().add(imgExibicao);
            } else {
                // Se está bloqueado, renderiza diretamente a imagem customizada de bloqueio
                String caminhoCadeado = "/com/mycompany/entreSombrasETeias/jogo/imagens/imagembloqueada.jpg";
                try (InputStream isCadeado = getClass().getResourceAsStream(caminhoCadeado)) {
                    if (isCadeado != null) {
                        imgExibicao.setImage(new Image(isCadeado));
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao carregar o cadeado de bloqueio.");
                }
                painelItem.getChildren().add(imgExibicao);
            }

            int coluna = indiceGrid % 4;
            int linha = indiceGrid / 4;
            gridGaleria.add(painelItem, coluna, linha);
            indiceGrid++;
        }

        // Atualiza a visibilidade das setas laterais de paginação do livro
        btnAnterior.setDisable(paginaAtual == 0);
        btnProximo.setDisable(fim >= listaProgresso.size());
    }

    /**
     * Traduz o número do episódio mapeado no banco para o arquivo físico enviado por você.
     */
    private String obterNomeArquivoPorEpisodio(int numeroEpisodio) {
        switch (numeroEpisodio) {
            case 1:
                return "imagem-conquistada-abutre.jpg";
            case 2:
                return "imagem-conquistada-shocker.jpg";
            case 3:
                return "imagem-conquistada-lagarto.jpg";
            case 4:
                return "imagem-conquistada-electro.jpg";
            case 5:
                return "imagem-conquistada-duende-verde.jpg";
            default:
                return "imagembloqueada.jpg";
        }
    }

    @FXML
    public void paginaAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            renderizarPagina();
        }
    }

    @FXML
    public void proximaPagina() {
        if ((paginaAtual + 1) * ITENS_POR_PAGINA < listaProgresso.size()) {
            paginaAtual++;
            renderizarPagina();
        }
    }

    @FXML
    public void voltarMenu() {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}