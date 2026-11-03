package br.com.diaristafacil.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre a conexão com o SQLite e cria as tabelas na primeira execução. */
public class Conexao {
    private static String url = "jdbc:sqlite:diarista_facil.db";

    /** Permite apontar para outro arquivo de banco (usado nos testes). */
    public static void setArquivo(String arquivo) {
        url = "jdbc:sqlite:" + arquivo;
    }

    public static Connection getConexao() throws SQLException {
        Connection con = DriverManager.getConnection(url);
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }

    public static void criarBanco() {
        try (Connection con = getConexao(); Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS usuario (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    login TEXT NOT NULL UNIQUE,
                    senha_hash TEXT NOT NULL,
                    perfil TEXT NOT NULL CHECK (perfil IN ('ATENDENTE','DIARISTA'))
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS cliente (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    telefone TEXT NOT NULL,
                    endereco TEXT NOT NULL,
                    bairro TEXT,
                    observacao TEXT,
                    data_cadastro TEXT NOT NULL DEFAULT (date('now','localtime'))
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS diarista (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    telefone TEXT NOT NULL,
                    ativa INTEGER NOT NULL DEFAULT 1
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS servico (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    descricao TEXT NOT NULL,
                    valor_base REAL NOT NULL
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS agendamento (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL REFERENCES cliente(id),
                    diarista_id INTEGER NOT NULL REFERENCES diarista(id),
                    servico_id INTEGER NOT NULL REFERENCES servico(id),
                    data TEXT NOT NULL,
                    turno TEXT NOT NULL CHECK (turno IN ('MANHA','TARDE','INTEGRAL')),
                    valor REAL NOT NULL,
                    status TEXT NOT NULL DEFAULT 'AGENDADO',
                    pago INTEGER NOT NULL DEFAULT 0
                )""");
            popularDadosIniciais(st);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar o banco: " + e.getMessage(), e);
        }
    }

    /** Cadastra usuários, diaristas e serviços básicos se o banco estiver vazio. */
    private static void popularDadosIniciais(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuario")) {
            if (rs.getInt(1) > 0) return;
        }
        st.execute("INSERT INTO usuario (nome, login, senha_hash, perfil) VALUES "
                + "('Ana', 'ana', '" + hash("ana123") + "', 'ATENDENTE'),"
                + "('Maria', 'maria', '" + hash("maria123") + "', 'DIARISTA'),"
                + "('Joana', 'joana', '" + hash("joana123") + "', 'DIARISTA')");
        st.execute("INSERT INTO diarista (nome, telefone) VALUES "
                + "('Maria', '(83) 98811-2201'), ('Joana', '(83) 98811-2202')");
        st.execute("INSERT INTO servico (descricao, valor_base) VALUES "
                + "('Faxina completa', 180.0), ('Faxina de manutenção', 130.0),"
                + "('Passadoria', 100.0), ('Faxina pós-obra', 280.0)");
    }

    /** Gera o hash SHA-256 da senha, para não guardar a senha em texto puro. */
    public static String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
