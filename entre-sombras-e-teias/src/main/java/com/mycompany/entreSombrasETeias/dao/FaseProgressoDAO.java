package com.mycompany.entreSombrasETeias.dao;
import com.mycompany.entreSombrasETeias.model.FaseProgresso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FaseProgressoDAO {
    public void inicializarFases(int idJogador) throws SQLException {
        String sql = "INSERT INTO fases_progresso (id_jogador, numero_episodio, bloqueado) "
                   + "VALUES (?, ?, ?)";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int ep = 1; ep <= 3; ep++) {
                ps.setInt(1, idJogador);
                ps.setInt(2, ep);
                ps.setBoolean(3, ep != 1); 
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    public List<FaseProgresso> listarPorJogador(int idJogador) throws SQLException {
        List<FaseProgresso> lista = new ArrayList<>();
        String sql = "SELECT * FROM fases_progresso WHERE id_jogador = ? ORDER BY numero_episodio";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJogador);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FaseProgresso fp = new FaseProgresso();
                fp.setIdProgresso(rs.getInt("id_progresso"));
                fp.setIdJogador(rs.getInt("id_jogador"));
                fp.setNumeroEpisodio(rs.getInt("numero_episodio"));
                fp.setBloqueado(rs.getBoolean("bloqueado"));
                lista.add(fp);
            }
        }
        return lista;
    }
    public void desbloquearEpisodio(int idJogador, int episodio) throws SQLException {
        String sql = "UPDATE fases_progresso SET bloqueado = FALSE "
                   + "WHERE id_jogador = ? AND numero_episodio = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJogador);
            ps.setInt(2, episodio);
            ps.executeUpdate();
        }
    }
}