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

    // ===================== OVERLAY FULLSCREEN =====================
    @FXML
    private StackPane painelFullscreen;
    @FXML
    private ImageView imgFullscreen;

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
            int numEpisodio = fase.getNumeroEpisodio(); 
            
            // StackPane para conter a imagem perfeitamente posicionada no Grid
            StackPane painelItem = new StackPane();
            painelItem.setAlignment(Pos.CENTER);

            ImageView imgExibicao = new ImageView();
            imgExibicao.setFitHeight(110);
            imgExibicao.setFitWidth(95);
            imgExibicao.setPreserveRatio(false);

            // Nova checagem inteligente: Só mostra a imagem se o boss correspondente foi DE FATO derrotado
            boolean bossDerrotado = false;
            com.mycompany.entreSombrasETeias.model.Jogador jog = SessaoJogo.get().getJogador();
            
            if (jog != null) {
                switch (numEpisodio) {
                    case 1: bossDerrotado = jog.isDerrotouAbutre(); break;
                    case 2: bossDerrotado = jog.isDerrotouShocker(); break;
                    case 3: bossDerrotado = jog.isDerrotouLagarto(); break;
                    default: bossDerrotado = false; break;
                }
            }

            if (bossDerrotado) {
                // Se o boss foi realmente derrotado, carrega a arte da vitória correspondente
                String arquivoImagem = obterNomeArquivoPorEpisodio(numEpisodio);
                String caminhoArte = "/com/mycompany/entreSombrasETeias/jogo/imagens/" + arquivoImagem;

                Image imagemConquista = carregarImagem(caminhoArte);
                if (imagemConquista != null) {
                    imgExibicao.setImage(imagemConquista);
                }

                // Só permite abrir o fullscreen se a imagem desbloqueada existir de fato
                if (imagemConquista != null) {
                    Image imagemParaFullscreen = imagemConquista;
                    painelItem.setCursor(javafx.scene.Cursor.HAND);
                    painelItem.setOnMouseClicked(e -> abrirFullscreen(imagemParaFullscreen));
                }

                painelItem.getChildren().add(imgExibicao);
            } else {
                // Se o boss não foi derrotado ainda (fase bloqueada OU fase liberada para jogar mas não vencida), mostra o cadeado
                String caminhoCadeado = "/com/mycompany/entreSombrasETeias/jogo/imagens/imagembloqueada.png";
                Image imagemCadeado = carregarImagem(caminhoCadeado);
                if (imagemCadeado != null) {
                    imgExibicao.setImage(imagemCadeado);
                }
                // Imagem bloqueada não abre fullscreen — sem onMouseClicked aqui de propósito.
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
     * Carrega uma imagem a partir do classpath, retornando null em caso de falha
     * (mantendo o mesmo padrão de try/catch já usado no resto da classe).
     */
    private Image carregarImagem(String caminho) {
        try (InputStream is = getClass().getResourceAsStream(caminho)) {
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + caminho);
        }
        return null;
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

    // ===================== OVERLAY FULLSCREEN =====================

    /**
     * Exibe a imagem desbloqueada em tela cheia, sobrepondo a galeria.
     */
    private void abrirFullscreen(Image imagem) {
        imgFullscreen.setImage(imagem);
        painelFullscreen.setVisible(true);
    }

    /**
     * Fecha o overlay de tela cheia (botão "X" ou clique fora da imagem).
     */
    @FXML
    public void fecharFullscreen() {
        painelFullscreen.setVisible(false);
        imgFullscreen.setImage(null);
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