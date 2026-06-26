package com.mycompany.entreSombrasETeias.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {
    public void registrarPartida(int idJogador, int xpObtido, int episodioChegou) throws SQLException {
        String sql = "INSERT INTO ranking (id_jogador, xp_obtido, episodio_chegou) VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJogador);
            ps.setInt(2, xpObtido);
            ps.setInt(3, episodioChegou);
            ps.executeUpdate();
        }
    }
    public List<String[]> listarRanking() throws SQLException {
        List<String[]> lista = new ArrayList<>();
      String sql = "SELECT j.nome, r.xp_obtido, r.episodio_chegou, r.data_partida "
           + "FROM ranking r "
           + "JOIN jogadores j ON j.id_jogador = r.id_jogador "
           + "ORDER BY r.xp_obtido DESC "
           + "LIMIT 20";
      
        try (Connection con = ConexaoBD.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("nome"),
                    String.valueOf(rs.getInt("xp_obtido")),
                    "EP " + rs.getInt("episodio_chegou"),
                    rs.getTimestamp("data_partida").toString()
                });
            }
            
        }
        return lista;
            }
        
}
