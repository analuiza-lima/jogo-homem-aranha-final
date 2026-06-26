package com.mycompany.entreSombrasETeias.dao;

import com.mycompany.entreSombrasETeias.model.Jogador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JogadorDAO {

    public Jogador criar(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO jogadores (nome, xp_atual, hp_atual, nivel_atual) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexaoBD.conectar()) {
            if (con == null) {
                throw new SQLException("A conexão retornada é nula. Verifique as configurações do banco!");
            }

            // Configurado para retornar o ID gerado automaticamente pelo banco (AUTO_INCREMENT)
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, jogador.getNome());
                ps.setInt(2, jogador.getXpAtual());
                ps.setInt(3, jogador.getHpAtual());
                ps.setInt(4, jogador.getNivelAtual());
               

                ps.executeUpdate();

                // Recupera o ID gerado e joga de volta no objeto jogador
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        jogador.setIdJogador(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Falha ao criar jogador, nenhum ID retornado.");
                    }
                }

                System.out.println("Jogador criado com sucesso no banco de dados! ID: " + jogador.getIdJogador());
                return jogador;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar jogador no DAO: " + e.getMessage());
            e.printStackTrace();
            throw e; // Lança o erro para o MenuController poder tratar no 'mostrarErro'
        }
    }
    
    public void atualizar(Jogador jogador) throws SQLException {
        String sql = "UPDATE jogadores SET xp_atual = ?, hp_atual = ?, nivel_atual = ? WHERE id_jogador = ?";

        try (Connection con = ConexaoBD.conectar()) {
            if (con == null) {
                throw new SQLException("A conexão retornada é nula. Verifique as configurações do banco!");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, jogador.getXpAtual());
                ps.setInt(2, jogador.getHpAtual());
                ps.setInt(3, jogador.getNivelAtual());
                ps.setInt(4, jogador.getIdJogador());

                ps.executeUpdate();
                System.out.println("Jogador " + jogador.getNome() + " atualizado com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar jogador no DAO: " + e.getMessage());
            e.printStackTrace();
            throw e; // Lança o erro para ser capturado pelos blocos try/catch dos seus controllers
        }
    }
}