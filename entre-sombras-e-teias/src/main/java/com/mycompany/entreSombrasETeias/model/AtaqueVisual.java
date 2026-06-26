package com.mycompany.entreSombrasETeias.model;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.Random;

public class AtaqueVisual {
    private Node formatoVisual;
    private String tipoAtaque;
    private double velocidadeX = 0;
    private double velocidadey = 0;
    private double tempoDeVida = 0;
    private double limiteMaximoVida = 240; // Aumentado para ~4 segundos total (contando o aviso)
    private double larguraArena;
    private double alturaArena;
    private Random random = new Random();

    // Sistema de Tempo/Aviso Prévio (Telegrafia para o jogador desviar)
    private boolean emAviso = true;
    private final int FRAMES_AVISO = 45; // ~0.7 segundos parado servindo de alerta

    public AtaqueVisual(String tipoAtaque, double larguraArena, double alturaArena, double xInicial) {
        this.tipoAtaque = tipoAtaque;
        this.larguraArena = larguraArena;
        this.alturaArena = alturaArena;

        switch (tipoAtaque) {
            // ==================== 1. ABUTRE ====================
            case "Mergulho vertical":
                Rectangle rAbutre = new Rectangle(20, 40, Color.SILVER);
                rAbutre.setLayoutX(xInicial);
                rAbutre.setLayoutY(0);
                this.velocidadey = 6.5; // Ataque veloz pós-aviso
                this.formatoVisual = rAbutre;
                break;

            case "Passagem lateral":
                Rectangle rAza = new Rectangle(40, 15, Color.DARKGREEN);
                rAza.setLayoutX(0);
                rAza.setLayoutY(random.nextDouble() * (alturaArena - 60) + 20);
                this.velocidadeX = 6.0;
                this.formatoVisual = rAza;
                break;

            case "Rajada simples":
                Circle cPena = new Circle(8, Color.LIGHTGRAY);
                cPena.setLayoutX(xInicial);
                cPena.setLayoutY(10);
                this.velocidadey = 5.5;
                this.formatoVisual = cPena;
                break;

            // ==================== 2. SHOCKER ====================
            case "Ondas horizontais":
                Rectangle rOnda = new Rectangle(larguraArena, 18, Color.VIOLET);
                rOnda.setLayoutX(0);
                rOnda.setLayoutY(0);
                this.velocidadey = 4.5; 
                this.formatoVisual = rOnda;
                break;

            case "Explosão central":
                Circle cExplosao = new Circle(6, Color.ORANGE);
                cExplosao.setLayoutX(larguraArena / 2);
                cExplosao.setLayoutY(alturaArena / 2);
                this.formatoVisual = cExplosao;
                break;

            case "Sequência de pulsos":
                Rectangle rPulso = new Rectangle(larguraArena, 10, Color.YELLOW);
                rPulso.setLayoutX(0);
                rPulso.setLayoutY(0);
                this.velocidadey = 5.5;
                this.formatoVisual = rPulso;
                break;

            // ==================== 3. LAGARTO ====================
            case "Salto aleatório":
                Circle cLagarto = new Circle(25, Color.GREEN);
                cLagarto.setLayoutX(random.nextDouble() * (larguraArena - 60) + 30);
                cLagarto.setLayoutY(0);
                this.velocidadey = 7.5;
                this.formatoVisual = cLagarto;
                break;

            case "Corrida horizontal":
                Rectangle rGarra = new Rectangle(35, 20, Color.DARKGREEN);
                rGarra.setLayoutX(0);
                rGarra.setLayoutY(alturaArena - 40); 
                this.velocidadeX = 7.0;
                this.formatoVisual = rGarra;
                break;

            case "Cauda em arco":
                Circle cCauda = new Circle(15, Color.LIMEGREEN);
                cCauda.setLayoutX(0);
                cCauda.setLayoutY(alturaArena / 2);
                this.velocidadeX = 4.5;
                this.velocidadey = -3.5; 
                this.formatoVisual = cCauda;
                break;

            // ==================== 4. ELECTRO ====================
            case "Raios verticais":
                Rectangle rRaio = new Rectangle(15, 70, Color.LIGHTBLUE);
                rRaio.setLayoutX(xInicial);
                rRaio.setLayoutY(0);
                this.velocidadey = 8.5;
                this.formatoVisual = rRaio;
                break;

            case "Laser horizontal":
                Rectangle rLaser = new Rectangle(larguraArena, 12, Color.CYAN);
                rLaser.setLayoutX(0);
                rLaser.setLayoutY(random.nextDouble() * (alturaArena - 30) + 10);
                this.formatoVisual = rLaser;
                break;

            case "Grade elétrica":
                Rectangle rGrid = new Rectangle(12, alturaArena, Color.YELLOW);
                rGrid.setLayoutX(xInicial);
                rGrid.setLayoutY(0);
                this.velocidadeX = 2.5; 
                this.formatoVisual = rGrid;
                break;

            // ==================== 5. DOUTOR OCTOPUS ====================
            case "Tentáculos laterais":
                Rectangle rTentaculo = new Rectangle(55, 22, Color.GRAY);
                if (random.nextBoolean()) {
                    rTentaculo.setLayoutX(0);
                    this.velocidadeX = 5.0;
                } else {
                    rTentaculo.setLayoutX(larguraArena);
                    this.velocidadeX = -5.0;
                }
                rTentaculo.setLayoutY(random.nextDouble() * (alturaArena - 40) + 10);
                this.formatoVisual = rTentaculo;
                break;

            case "Ataque simultâneo":
                Circle cOctopus = new Circle(13, Color.DARKSLATEGRAY);
                cOctopus.setLayoutX(xInicial);
                cOctopus.setLayoutY(0);
                this.velocidadeX = (xInicial > larguraArena / 2) ? -2.5 : 2.5;
                this.velocidadey = 5.5;
                this.formatoVisual = cOctopus;
                break;

            case "Zona bloqueada":
                Rectangle rBloqueio = new Rectangle(larguraArena, 30, Color.BROWN);
                rBloqueio.setLayoutX(0);
                rBloqueio.setLayoutY(random.nextBoolean() ? 0 : alturaArena - 30);
                this.formatoVisual = rBloqueio;
                break;

            // ==================== 6. DUENDE VERDE ====================
            case "Bombas com atraso":
                Circle cBomba = new Circle(14, Color.ORANGERED);
                cBomba.setLayoutX(xInicial);
                cBomba.setLayoutY(random.nextDouble() * (alturaArena / 2) + 20);
                this.formatoVisual = cBomba;
                break;

            case "Investida diagonal":
                Rectangle rPlaneador = new Rectangle(35, 22, Color.PURPLE);
                rPlaneador.setLayoutX(0);
                rPlaneador.setLayoutY(0);
                this.velocidadeX = 5.0;
                this.velocidadey = 4.0;
                this.formatoVisual = rPlaneador;
                break;

            case "Caos aleatório":
                Circle cCaos = new Circle(11, Color.DEEPPINK);
                cCaos.setLayoutX(random.nextDouble() * (larguraArena - 30) + 15);
                cCaos.setLayoutY(0);
                this.velocidadeX = random.nextDouble() * 6.0 - 3.0;
                this.velocidadey = 5.5;
                this.formatoVisual = cCaos;
                break;

            // ==================== PADRÃO ====================
            default:
                Circle padrao = new Circle(15, Color.RED);
                padrao.setLayoutX(xInicial);
                padrao.setLayoutY(10);
                this.velocidadey = 4.5;
                this.formatoVisual = padrao;
                break;
        }

        // Configura o visual inicial como transparente (aviso de perigo chegando)
        if (this.formatoVisual != null) {
            this.formatoVisual.setOpacity(0.3);
        }
    }

    public void atualizarMecanica() {
        tempoDeVida++;

        // FLUXO DE AVISO: Mantém o ataque congelado no início para o jogador desviar
        if (emAviso) {
            if (tempoDeVida >= FRAMES_AVISO) {
                emAviso = false;
                formatoVisual.setOpacity(1.0); // Ativa completamente o ataque (causa dano)
            }
            return; // Impede movimentação mecânica durante o alerta
        }

        // COMPORTAMENTOS ESPECÍFICOS PÓS-AVISO
        if (tipoAtaque.equals("Explosão central") && formatoVisual instanceof Circle) {
            Circle c = (Circle) formatoVisual;
            c.setRadius(c.getRadius() + 2.5); // Crescimento contínuo corrigido
        } 
        else if (tipoAtaque.equals("Cauda em arco")) {
            velocidadey += 0.18; // Simulação física da cauda subindo e descendo
            formatoVisual.setLayoutX(formatoVisual.getLayoutX() + velocidadeX);
            formatoVisual.setLayoutY(formatoVisual.getLayoutY() + velocidadey);
        }
        else if (tipoAtaque.equals("Bombas com atraso") && formatoVisual instanceof Circle) {
            // Conta 60 frames após o fim do aviso para explodir
            if (tempoDeVida >= (FRAMES_AVISO + 60)) { 
                Circle c = (Circle) formatoVisual;
                c.setFill(Color.RED);
                c.setRadius(c.getRadius() + 5.0);
            }
        } 
        else {
            // Movimentação padrão por vetores lineares
            formatoVisual.setLayoutX(formatoVisual.getLayoutX() + velocidadeX);
            formatoVisual.setLayoutY(formatoVisual.getLayoutY() + velocidadey);
        }
    }

    public boolean deveSerDestruido() {
        // Ataques que duram por tempo de tela fixo e não por saídas de bordas
        if (tipoAtaque.equals("Explosão central") || tipoAtaque.equals("Laser horizontal") || tipoAtaque.equals("Zona bloqueada")) {
            return tempoDeVida >= limiteMaximoVida;
        }

        // Varredura para coletar e apagar objetos fora da visualização da Hitbox da Arena
        return tempoDeVida >= limiteMaximoVida || 
               formatoVisual.getLayoutY() > alturaArena + 60 || 
               formatoVisual.getLayoutY() < -60 ||
               formatoVisual.getLayoutX() > larguraArena + 60 ||
               formatoVisual.getLayoutX() < -60;
    }

    public Node getFormatoVisual() {
        return formatoVisual;
    }
    
    public boolean isEmAviso() {
        return emAviso;
    }
}