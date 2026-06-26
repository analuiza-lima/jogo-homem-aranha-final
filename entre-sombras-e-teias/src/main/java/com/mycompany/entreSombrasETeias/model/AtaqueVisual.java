package com.mycompany.entreSombrasETeias.model;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class AtaqueVisual {
    private Node formatoVisual;
    private String tipoAtaque;
    private double velocidadeX = 0;
    private double velocidadeY = 0;
    private double tempoDeVida = 0;
    private double limiteMaximoVida = 180; // ~3 segundos em 60 FPS
    private double larguraArena;
    private double alturaArena;

    public AtaqueVisual(String tipoAtaque, double larguraArena, double alturaArena, double xInicial) {
        this.tipoAtaque = tipoAtaque;
        this.larguraArena = larguraArena;
        this.alturaArena = alturaArena;

        switch (tipoAtaque) {
            case "Mergulho vertical":
                Rectangle r1 = new Rectangle(20, 40, Color.SILVER);
                r1.setLayoutX(xInicial);
                r1.setLayoutY(0);
                this.velocidadeY = 5;
                this.formatoVisual = r1;
                break;

            // --- ATAQUES DO SHOCKER BALANCEADOS ---
            case "Ondas horizontais":
                Rectangle r2 = new Rectangle(larguraArena, 15, Color.VIOLET);
                r2.setLayoutX(0);
                r2.setLayoutY(alturaArena - 30);
                this.velocidadeX = 0; 
                this.formatoVisual = r2;
                break;

            case "Explosão central":
                Circle c1 = new Circle(10, Color.ORANGE);
                c1.setLayoutX(larguraArena / 2);
                c1.setLayoutY(alturaArena / 2);
                this.formatoVisual = c1;
                break;

            case "Disparo Triplo":
                Circle c2 = new Circle(12, Color.VIOLET);
                c2.setLayoutX(larguraArena / 2);
                c2.setLayoutY(alturaArena / 2);
                double[] direcoes = {-4.0, 0.0, 4.0};
                this.velocidadeX = direcoes[new java.util.Random().nextInt(3)];
                this.velocidadeY = -4.5; 
                this.formatoVisual = c2;
                break;
            // -------------------------------------

            default:
                Circle padrao = new Circle(15, Color.RED);
                padrao.setLayoutX(xInicial);
                padrao.setLayoutY(10);
                this.velocidadeY = 4;
                this.formatoVisual = padrao;
                break;
        }
    }

    public void atualizarMecanica() {
        tempoDeVida++;
        if (tipoAtaque.equals("Explosão central") && formatoVisual instanceof Circle) {
            Circle c = (Circle) formatoVisual;
            c.setRadius(c.getRadius() + 1.2);
        } else {
            formatoVisual.setLayoutX(formatoVisual.getLayoutX() + velocidadeX);
            formatoVisual.setLayoutY(formatoVisual.getLayoutY() + velocidadeY);
        }
    }

    public boolean deveSerDestruido() {
        return tempoDeVida >= limiteMaximoVida || 
               formatoVisual.getLayoutY() > alturaArena || 
               formatoVisual.getLayoutY() < -50;
    }

    public Node getFormatoVisual() {
        return formatoVisual;
    }
}