package com.mycompany.entreSombrasETeias.model;

public class Jogador {
    private int    idJogador;
    private String nome;
    private int    xpAtual;
    private int    hpAtual;
    private int    nivelAtual;
    private boolean derrotouAbutre;
    private boolean derrotouShocker;
    private boolean derrotouLagarto;
    private int suplementos;
    
    public Jogador() {}

    public Jogador(String nome) {
        this.nome       = nome;
        this.xpAtual   = 0;
        this.nivelAtual = 1;    
        this.hpAtual   = getHpMaximo();
        this.suplementos = 0;
    }

    // Calcula o HP Máximo baseado no nível atual do Peter (100 base + 20 por nível)
    public int getHpMaximo() {
        return 100 + ((this.nivelAtual - 1) * 20);
    }
    
    public void adicionarXp(int quantidade) {
        this.xpAtual += quantidade;
        // Sistema simples de Level Up retroativo caso queira implementar futuramente
        if (this.xpAtual >= this.nivelAtual * 100) {
            this.nivelAtual++;
            this.hpAtual = getHpMaximo(); // Cura completa ao subir de nível
        }
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
        // Garante que o HP modificado externamente não passe do limite máximo permitido
        this.hpAtual = Math.min(hpAtual, getHpMaximo());
    }

    public int getNivelAtual() {
        return nivelAtual;
    }

    public void setNivelAtual(int nivelAtual) {
        this.nivelAtual = nivelAtual;
    }
    
    public boolean isDerrotouAbutre() {
    return derrotouAbutre;
}

    public void setDerrotouAbutre(boolean derrotouAbutre) {
        this.derrotouAbutre = derrotouAbutre;
    }

    public boolean isDerrotouShocker() {
        return derrotouShocker;
    }

    public void setDerrotouShocker(boolean derrotouShocker) {
        this.derrotouShocker = derrotouShocker;
    }

    public boolean isDerrotouLagarto() {
        return derrotouLagarto;
    }

    public void setDerrotouLagarto(boolean derrotouLagarto) {
        this.derrotouLagarto = derrotouLagarto;
    }
    
    public int getSuplementos() {
    return suplementos;
}

public void setSuplementos(int suplementos) {
    this.suplementos = suplementos;
}

public void ganharSuplemento() {
    suplementos++;
}

public boolean usarSuplemento() {

    if (suplementos <= 0)
        return false;

    suplementos--;

    setHpAtual(getHpAtual() + 15);

    return true;
}
}