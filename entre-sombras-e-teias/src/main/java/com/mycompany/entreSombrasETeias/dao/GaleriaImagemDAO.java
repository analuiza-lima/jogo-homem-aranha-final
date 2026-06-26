package com.mycompany.entreSombrasETeias.dao;

import com.mycompany.entreSombrasETeias.model.GaleriaImagem;
import com.mycompany.entreSombrasETeias.dao.ConexaoBD; // Substitua pela sua classe de conexão
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GaleriaImagemDAO {
    
    public List<GaleriaImagem> buscarImagensDoJogador(int idJogador) {
        List<String> imagens = new ArrayList<>();
        List<GaleriaImagem> lista = new ArrayList<>();
        String sql = "SELECT id_imagem, id_jogador, caminho_textura, bloqueado FROM galeria_imagens WHERE id_jogador = ? ORDER BY caminho_textura ASC";
        
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idJogador);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(new GaleriaImagem(
                    rs.getInt("id_imagem"),
                    rs.getInt("id_jogador"),
                    rs.getString("caminho_textura"),
                    rs.getBoolean("bloqueado")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}