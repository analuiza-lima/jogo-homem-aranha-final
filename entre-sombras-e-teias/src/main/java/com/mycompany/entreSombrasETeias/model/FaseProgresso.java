package com.mycompany.entreSombrasETeias.model;


public class FaseProgresso {
    private int     idProgresso;
    private int     idJogador;
    private int     numeroEpisodio;
    private boolean bloqueado;
    
    public FaseProgresso() {
    
    }

    public int getIdProgresso() {
        return idProgresso;
    }

    public void setIdProgresso(int idProgresso) {
        this.idProgresso = idProgresso;
    }

    public int getIdJogador() {
        return idJogador;
    }

    public void setIdJogador(int idJogador) {
        this.idJogador = idJogador;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(int numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
    
}
