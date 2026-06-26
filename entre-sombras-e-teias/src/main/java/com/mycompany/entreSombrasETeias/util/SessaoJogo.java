package com.mycompany.entreSombrasETeias.util;

import com.mycompany.entreSombrasETeias.model.Jogador;

public class SessaoJogo {
    private static SessaoJogo instancia;
    private Jogador jogadorAtual;
    private String vilaoAtual; // Guarda o vilão selecionado na Teia de Confrontos

    // Construtor privado correto para garantir o Singleton
    private SessaoJogo() {
    }

    // Ponto de acesso global
    public static SessaoJogo get() {
        if (instancia == null) {
            instancia = new SessaoJogo();
        }
        return instancia;
    }

    public Jogador getJogador() { 
        return jogadorAtual; 
    }

    public void setJogador(Jogador j) { 
        this.jogadorAtual = j; 
    }
    
    // GETTER E SETTER DO VILÃO SELECIONADO
    public String getVilaoAtual() {
        return vilaoAtual;
    }

    public void setVilaoAtual(String vilao) {
        this.vilaoAtual = vilao;
    }
    
    /**
     * Limpa os dados da sessão atual.
     * Essencial para quando o jogador volta ao menu principal 
     * ou encerra a partida para iniciar um novo jogo.
     */
    public void encerrarSessao() {
        this.jogadorAtual = null;
        this.vilaoAtual = null; // Limpa o vilão guardado ao fechar a sessão
    }
}