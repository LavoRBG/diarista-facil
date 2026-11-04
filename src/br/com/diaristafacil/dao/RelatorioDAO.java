package br.com.diaristafacil.dao;

import br.com.diaristafacil.model.ResumoDiarista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consultas usadas no relatório mensal (RF09). */
public class RelatorioDAO {

    /**
     * Agrupa as faxinas do mês por diarista, somando o valor total e o que já foi pago.
     * Faxinas canceladas não entram no relatório.
     */
    public List<ResumoDiarista> resumoPorDiarista(int ano, int mes) throws SQLException {
        String sql = """
            SELECT d.nome,
                   COUNT(a.id)                                  AS quantidade,
                   SUM(a.valor)                                 AS total,
                   SUM(CASE WHEN a.pago = 1 THEN a.valor ELSE 0 END) AS recebido
              FROM agendamento a
              JOIN diarista d ON d.id = a.diarista_id
             WHERE strftime('%Y-%m', a.data) = ?
               AND a.status <> 'CANCELADO'
             GROUP BY d.id, d.nome
             ORDER BY d.nome
            """;
        List<ResumoDiarista> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.format("%04d-%02d", ano, mes));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ResumoDiarista(rs.getString("nome"), rs.getInt("quantidade"),
                            rs.getDouble("total"), rs.getDouble("recebido")));
                }
            }
        }
        return lista;
    }

    /** Lista cada faxina do mês (data, diarista, cliente, serviço, valor e se foi pago). */
    public List<String[]> faxinasDoMes(int ano, int mes) throws SQLException {
        String sql = """
            SELECT a.data, d.nome AS diarista, a.turno, c.nome AS cliente, s.descricao, a.valor, a.pago
              FROM agendamento a
              JOIN diarista d ON d.id = a.diarista_id
              JOIN cliente c  ON c.id = a.cliente_id
              JOIN servico s  ON s.id = a.servico_id
             WHERE strftime('%Y-%m', a.data) = ? AND a.status <> 'CANCELADO'
             ORDER BY a.data, d.nome
            """;
        List<String[]> linhas = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.format("%04d-%02d", ano, mes));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] d = rs.getString("data").split("-");
                    linhas.add(new String[]{
                            d[2] + "/" + d[1] + "/" + d[0],
                            rs.getString("diarista"),
                            rotuloTurno(rs.getString("turno")),
                            rs.getString("cliente"),
                            rs.getString("descricao"),
                            String.format("%.2f", rs.getDouble("valor")).replace('.', ','),
                            rs.getInt("pago") == 1 ? "Sim" : "Não"});
                }
            }
        }
        return linhas;
    }

    private static String rotuloTurno(String turno) {
        return switch (turno) {
            case "MANHA" -> "Manhã";
            case "TARDE" -> "Tarde";
            default -> "Integral";
        };
    }
}
