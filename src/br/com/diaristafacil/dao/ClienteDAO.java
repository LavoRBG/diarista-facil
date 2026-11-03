package br.com.diaristafacil.dao;

import br.com.diaristafacil.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /** Grava um novo cliente e devolve o id gerado pelo banco. */
    public int inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, endereco, bairro, observacao) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome().trim());
            ps.setString(2, c.getTelefone().trim());
            ps.setString(3, c.getEndereco().trim());
            ps.setString(4, c.getBairro());
            ps.setString(5, c.getObservacao());
            // RETURNING id devolve o código gerado pelo banco
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                c.setId(rs.getInt(1));
            }
        }
        return c.getId();
    }

    /** Lista os clientes em ordem alfabética, com filtro opcional pelo nome. */
    public List<Cliente> listar(String filtroNome) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtroNome == null ? "" : filtroNome.trim()) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    static Cliente montar(ResultSet rs) throws SQLException {
        Cliente c = new Cliente(rs.getString("nome"), rs.getString("telefone"),
                rs.getString("endereco"), rs.getString("bairro"), rs.getString("observacao"));
        c.setId(rs.getInt("id"));
        return c;
    }
}
