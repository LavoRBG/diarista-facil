package br.com.diaristafacil.dao;

import br.com.diaristafacil.model.Diarista;
import br.com.diaristafacil.model.Servico;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Consultas das tabelas de apoio (diaristas e tipos de serviço). */
public class CadastroBasicoDAO {

    public List<Diarista> listarDiaristasAtivas() throws SQLException {
        List<Diarista> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM diarista WHERE ativa = 1 ORDER BY nome")) {
            while (rs.next()) {
                lista.add(new Diarista(rs.getInt("id"), rs.getString("nome"),
                        rs.getString("telefone"), true));
            }
        }
        return lista;
    }

    public List<Servico> listarServicos() throws SQLException {
        List<Servico> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM servico ORDER BY descricao")) {
            while (rs.next()) {
                lista.add(new Servico(rs.getInt("id"), rs.getString("descricao"), rs.getDouble("valor_base")));
            }
        }
        return lista;
    }
}
