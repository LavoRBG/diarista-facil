package br.com.diaristafacil.dao;

import br.com.diaristafacil.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    /** Retorna o usuário se login e senha conferirem; caso contrário, null. */
    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT id, nome, login, perfil FROM usuario WHERE login = ? AND senha_hash = ?";
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login.trim().toLowerCase());
            ps.setString(2, Conexao.hash(senha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nome"),
                            rs.getString("login"), rs.getString("perfil"));
                }
            }
        }
        return null;
    }
}
