package com.mycompany.entreSombrasETeias.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.mycompany.entreSombrasETeias.util.SceneManager;
import java.util.ArrayList;
import java.util.List;

public class TeiaConfrontosController {

    @FXML private Button btnAbutre;
    @FXML private Button btnShocker;
    @FXML private Button btnLagarto;
    @FXML private Button btnElectro;
    @FXML private Button btnOctopus;
    @FXML private Button btnDuende;
    @FXML private Button btnConfirmar;

    @FXML private ImageView imgAbutre;
    @FXML private ImageView imgShocker;
    @FXML private ImageView imgLagarto;
    @FXML private ImageView imgElectro;
    @FXML private ImageView imgOctopus;
    @FXML private ImageView imgDuende;

    private Button botaoSelecionadoAtual = null;
    private String vilaoEscolhido = "";
    private List<Button> todosBotoes = new ArrayList<>();

    @FXML
    public void initialize() {
        todosBotoes.add(btnAbutre);
        todosBotoes.add(btnShocker);
        todosBotoes.add(btnLagarto);
        todosBotoes.add(btnElectro);
        todosBotoes.add(btnOctopus);
        todosBotoes.add(btnDuende);

        // Renderiza em P&B com segurança contra caminhos errados de arquivos
        resetarImagensParaPretoEBranco();
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

         @FXML
         void selecionarElectro(ActionEvent event) {
             processarSelecao(btnElectro, imgElectro, "electro", "/com/mycompany/entreSombrasETeias/jogo/imagens/icon-electro-escolha-vilao.jpeg");
         }

         @FXML
         void selecionarOctopus(ActionEvent event) {
             processarSelecao(btnOctopus, imgOctopus, "octopus", "/com/mycompany/entreSombrasETeias/jogo/imagens/icon-octopus-escolha-vilao.jpeg");
         }

         @FXML
         void selecionarDuende(ActionEvent event) {
             processarSelecao(btnDuende, imgDuende, "duende", "/com/mycompany/entreSombrasETeias/jogo/imagens/duende-verde.jpg");
         }
    private void processarSelecao(Button botaoClicado, ImageView imgView, String nomeVilao, String caminhoImagemColorida) {
        resetarImagensParaPretoEBranco();
        
        for (Button btn : todosBotoes) {
            btn.setStyle("-fx-cursor: hand; -fx-background-color: rgba(0,0,0,0.4); -fx-border-color: black; -fx-border-width: 2;");
        }

        this.botaoSelecionadoAtual = botaoClicado;
        this.vilaoEscolhido = nomeVilao;

        // Borda destacada em vermelho puro ao selecionar
        botaoClicado.setStyle("-fx-cursor: hand; -fx-background-color: rgba(255,34,34,0.3); -fx-border-color: #ff2222; -fx-border-width: 3;");

        try {
            Image imgColorida = new Image(getClass().getResourceAsStream(caminhoImagemColorida));
            imgView.setImage(imgColorida);
        } catch (Exception e) {
            System.err.println("Erro ao carregar textura colorida: " + caminhoImagemColorida);
        }

        btnConfirmar.setDisable(false);
    }

   private void resetarImagensParaPretoEBranco() {
    carregarImagemSegura(imgAbutre, "/com/mycompany/entreSombrasETeias/jogo/imagens/abutre-pb.jpeg");
    carregarImagemSegura(imgShocker, "/com/mycompany/entreSombrasETeias/jogo/imagens/shocker-pb.jpeg");
    carregarImagemSegura(imgLagarto, "/com/mycompany/entreSombrasETeias/jogo/imagens/lagarto-pb.jpeg");
    carregarImagemSegura(imgElectro, "/com/mycompany/entreSombrasETeias/jogo/imagens/electro-pb.jpeg");
    carregarImagemSegura(imgOctopus, "/com/mycompany/entreSombrasETeias/jogo/imagens/octopus-pb.jpeg");
    carregarImagemSegura(imgDuende, "/com/mycompany/entreSombrasETeias/jogo/imagens/duende-verde-branco.png");
}
    private void carregarImagemSegura(ImageView imgView, String caminho) {
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(caminho);
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
            // Insira aqui a chamada para sua gameplay
        }
    }

    @FXML
    void voltarMenu(ActionEvent event) {
        // Redireciona com segurança usando o seu gerenciador centralizado
        SceneManager.trocarTela("/com/mycompany/entreSombrasETeias/jogo/fxml/menu.fxml");
    }
}