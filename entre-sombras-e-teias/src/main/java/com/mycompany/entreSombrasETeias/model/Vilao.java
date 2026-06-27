package com.mycompany.entreSombrasETeias.model;

public class Vilao {
    
    private int    idVilao;
    private String nome;
    private String descricao;
    private int    vidaMaxima;
    private int    vidaAtual;
    private int    xpRecompensa;
    private int    numeroEpisodio;
    
    public Vilao() {
    }
    
    public void receberDano(int dano) {
        this.vidaAtual = Math.max(0, this.vidaAtual - dano);
    }

    public boolean estaMorto() {
        return this.vidaAtual <= 0;
    }

    // ==========================================
    //   MÉTODOS DE COMPATIBILIDADE (GETTER/SETTER HP)
    //   Evitam erros de compilação no GameplayController
    // ==========================================
    
    public int getHpMaximo() {
        return this.vidaMaxima;
    }
    
    public void setHpMaximo(int hpMaximo) {
        this.vidaMaxima = hpMaximo;
    }
    
    public int getHpAtual() {
        return this.vidaAtual;
    }
    
    public void setHpAtual(int hpAtual) {
        this.vidaAtual = Math.min(Math.max(0, hpAtual), this.vidaMaxima);
    }

    // ==========================================
    //   GETTERS E SETTERS PADRÃO
    // ==========================================

    public int getIdVilao() {
        return idVilao;
    }

    public void setIdVilao(int idVilao) {
        this.idVilao = idVilao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public void setVidaMaxima(int vidaMaxima) {
        this.vidaMaxima = vidaMaxima;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(int vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public int getXpRecompensa() {
        return xpRecompensa;
    }

    public void setXpRecompensa(int xpRecompensa) {
        this.xpRecompensa = xpRecompensa;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(int numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }
}