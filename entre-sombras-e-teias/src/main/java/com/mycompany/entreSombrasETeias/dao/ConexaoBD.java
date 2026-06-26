package com.mycompany.entreSombrasETeias.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL    = "jdbc:postgresql://localhost:5432/entre-sombras-e-teias";
    private static final String USUARIO = "postgres";   
    private static final String SENHA   = "postgres";   
    
    public static Connection conectar() {
        try {
            // Garante o carregamento do Driver mesmo em ambientes antigos de laboratório
            Class.forName("org.postgresql.Driver");
            
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Conexão com o banco realizada com sucesso!");
            return conexao;
        } catch (ClassNotFoundException e) {
            System.err.println("Driver do PostgreSQL não encontrado! Verifique as dependências do pom.xml.");
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        conectar();
    }
}