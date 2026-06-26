package com.mycompany.entreSombrasETeias.dao;

import com.mycompany.entreSombrasETeias.model.Vilao; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VilaoDAO {
    public Vilao buscarPorEpisodio(int episodio) throws SQLException {
        String sql = "SELECT * FROM viloes WHERE numero_episodio = ?";
        try (Connection con = ConexaoBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, episodio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        }
        return null;
    }
    private Vilao mapear(ResultSet rs) throws SQLException {
        Vilao v = new Vilao();
        v.setIdVilao(rs.getInt("id_vilao"));
        v.setNome(rs.getString("nome"));
        v.setDescricao(rs.getString("descricao"));
        v.setVidaMaxima(rs.getInt("vida_maxima"));
        v.setVidaAtual(rs.getInt("vida_maxima")); 
        v.setXpRecompensa(rs.getInt("xp_recompensa"));
        v.setNumeroEpisodio(rs.getInt("numero_episodio"));
        return v;
    }
}
