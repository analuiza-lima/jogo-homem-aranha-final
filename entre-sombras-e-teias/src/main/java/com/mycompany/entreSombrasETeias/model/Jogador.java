package com.mycompany.entreSombrasETeias.model;


public class Jogador {
    private int    idJogador;
    private String nome;
    private int    xpAtual;
    private int    hpAtual;
    private int    nivelAtual;
    private int    moedas;
    public Jogador() {}
    public Jogador(String nome) {
        this.nome      = nome;
        this.xpAtual   = 0;
        this.hpAtual   = 100;
        this.nivelAtual = 1;
        this.moedas    = 0;
    }

    
    public void adicionarXp(int quantidade) {
        this.xpAtual += quantidade;
    }

    public void adicionarMoedas(int quantidade) {
        this.moedas += quantidade; 
    }
    
        public void receberDano(int dano) {
        this.hpAtual = Math.max(0, this.hpAtual - dano);
    }

    public boolean estaVivo() {
        return this.hpAtual > 0;
    }

    public int getIdJogador() {
        return idJogador;
    }

    public void setIdJogador(int idJogador) {
        this.idJogador = idJogador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getXpAtual() {
        return xpAtual;
    }

    public void setXpAtual(int xpAtual) {
        this.xpAtual = xpAtual;
    }

    public int getHpAtual() {
        return hpAtual;
    }

    public void setHpAtual(int hpAtual) {
        this.hpAtual = hpAtual;
    }

    public int getNivelAtual() {
        return nivelAtual;
    }

    public void setNivelAtual(int nivelAtual) {
        this.nivelAtual = nivelAtual;
    }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = moedas;
    }
    
    
}
