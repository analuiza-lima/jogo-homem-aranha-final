package com.mycompany.entreSombrasETeias.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import com.mycompany.entreSombrasETeias.util.SessaoJogo;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TeiaConfrontosController {

    @FXML private Button btnAbutre;
    @FXML private Button btnShocker;
    @FXML private Button btnLagarto;
    @FXML private Button btnConfirmar;

    @FXML private ImageView imgAbutre;
    @FXML private ImageView imgShocker;
    @FXML private ImageView imgLagarto;

    private Button botaoSelecionadoAtual = null;
    private String vilaoEscolhido = "";
    private List<Button> todosBotoes = new ArrayList<>();
    private List<ImageView> todasImagens = new ArrayList<>();

    @FXML
    public void initialize() {
        todosBotoes.add(btnAbutre);
        todosBotoes.add(btnShocker);
        todosBotoes.add(btnLagarto);

        todasImagens.add(imgAbutre);
        todasImagens.add(imgShocker);
        todasImagens.add(imgLagarto);

        // Configura todas as ImageViews para preencherem totalmente o espaço do ícone
        configurarTamanhoImagens();

        // Renderiza em P&B com segurança contra caminhos errados de arquivos
        resetarImagensParaPretoEBranco();

        // Garante que o botão de confirmar comece desativado até escolherem um vilão
        if (btnConfirmar != null) {
            btnConfirmar.setDisable(true);
        }
    }

    private void configurarTamanhoImagens() {
        for (ImageView img : todasImagens) {
            if (img != null) {
                img.setPreserveRatio(false); // Permite esticar/ajustar perfeitamente ao tamanho estipulado
                img.setSmooth(true);
            }
        }
    }

    @FXML
    void selecionarAbutre(ActionEvent event) {
        processarSelecao(btnAbutre, imgAbutre, "abutre", "/com/mycompany/entreSombrasETeias/jogo/imagens/icon-abutre-escolha-vilao.jpg");
    }

    @FXML
    void selecionarShocker(ActionEvent event) {
        processarSelecao(btnShocker, imgShocker, "shocker", "/com/mycompany/entreSombrasETeias/jogo/imagens/icon-shocker-escolha-vilao.jpg");
    }

    @FXML
    void selecionarLagarto(ActionEvent event) {
        processarSelecao(btnLagarto, imgLagarto, "lagarto", "/com/mycompany/entreSombrasETeias/jogo/imagens/icon-lagarto-escolha-vilao.jpg");
    }

    private void processarSelecao(Button botaoClicado, ImageView imgView, String nomeVilao, String caminhoImagemColorida) {
       
        resetarImagensParaPretoEBranco();

        for (Button btn : todosBotoes) {
            if (btn != null) {
                btn.setStyle("-fx-cursor: hand; -fx-background-color: rgba(0,0,0,0.4); -fx-border-color: black; -fx-border-width: 2;");
            }
        }

        this.botaoSelecionadoAtual = botaoClicado;
        this.vilaoEscolhido = nomeVilao;

        // Borda destacada em vermelho puro ao selecionar
        if (botaoClicado != null) {
            botaoClicado.setStyle("-fx-cursor: hand; -fx-background-color: rgba(255,34,34,0.3); -fx-border-color: #ff2222; -fx-border-width: 3;");
        }

        // BUG CORRIGIDO: antes este método chamava imgView.setImage(...) diretamente,
        // sem checar null e sem o mesmo tratamento de erro usado em carregarImagemSegura.
        // Agora reaproveita o método seguro, evitando duplicação e NPE caso imgView seja nulo.
        carregarImagemSegura(imgView, caminhoImagemColorida);

        if (btnConfirmar != null) {
            btnConfirmar.setDisable(false);
        }
    }

    private void resetarImagensParaPretoEBranco() {
        carregarImagemSegura(imgAbutre, "/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-pb.jpeg");
        carregarImagemSegura(imgShocker, "/com/mycompany/entreSombrasETeias/jogo/imagens/shocker-pb.jpeg");
        carregarImagemSegura(imgLagarto, "/com/mycompany/entreSombrasETeias/jogo/imagens/lagarto-pb.jpeg");
    }

    private void carregarImagemSegura(ImageView imgView, String caminho) {
        if (imgView == null) return;
        try (InputStream stream = getClass().getResourceAsStream(caminho)) {
            if (stream != null) {
                imgView.setImage(new Image(stream));
            } else {
                System.err.println("Arquivo não encontrado no build do Maven: " + caminho);
            }
        } catch (Exception e) {
            System.err.println("Falha crítica ao ler recurso gráfico: " + caminho);
        }
    }

    @FXML
    void confirmarConfronto(ActionEvent event) {
        if (!vilaoEscolhido.isEmpty()) {
            System.out.println("Batalha iniciada contra: " + vilaoEscolhido);

            // Define o vilão selecionado na sessão para que a tela de combate saiba com quem lutar
            SessaoJogo.get().setVilaoAtual(vilaoEscolhido);

            // Troca para a tela onde a gameplay/combate acontece de fato
            SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/gameplay.fxml");
        }
    }

    @FXML
    void voltarMenu(ActionEvent event) {
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}