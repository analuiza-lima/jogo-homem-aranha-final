package com.mycompany.entreSombrasETeias.model;


public class GaleriaImagem {
    private int     idImagem;
    private int     idJogador;
    private String  caminhoTextura;
    private boolean bloqueado;

    public GaleriaImagem() {
    }

    
    public GaleriaImagem(int idImagem, int idJogador, String caminhoTextura, boolean bloqueado) {
        this.idImagem = idImagem;
        this.idJogador = idJogador;
        this.caminhoTextura = caminhoTextura;
        this.bloqueado = bloqueado;
    }
   


    public int getIdImagem() {
        return idImagem;
    }

    public void setIdImagem(int idImagem) {
        this.idImagem = idImagem;
    }

    public int getIdJogador() {
        return idJogador;
    }

    public void setIdJogador(int idJogador) {
        this.idJogador = idJogador;
    }

    public String getCaminhoTextura() {
        return caminhoTextura;
    }

    public void setCaminhoTextura(String caminhoTextura) {
        this.caminhoTextura = caminhoTextura;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
    
    
}
