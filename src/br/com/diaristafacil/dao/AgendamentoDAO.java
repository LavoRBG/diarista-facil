package br.com.diaristafacil.dao;

import br.com.diaristafacil.model.Agendamento;
import br.com.diaristafacil.model.Cliente;
import br.com.diaristafacil.model.Diarista;
import br.com.diaristafacil.model.Servico;
import br.com.diaristafacil.model.Turno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    /**
     * Verifica se a diarista já tem faxina marcada na mesma data em um turno que
     * se choca com o turno pedido (integral ocupa manhã e tarde).
     */
    public boolean diaristaOcupada(int diaristaId, LocalDate data, Turno turno) throws SQLException {
        String sql = "SELECT turno FROM agendamento WHERE diarista_id = ? AND data = ? AND status <> 'CANCELADO'";
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, diaristaId);
            ps.setString(2, data.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (Turno.valueOf(rs.getString("turno")).conflitaCom(turno)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Grava o agendamento depois de checar se a diarista está livre. */
    public int agendar(Agendamento a) throws SQLException, AgendaOcupadaException {
        if (diaristaOcupada(a.getDiarista().getId(), a.getData(), a.getTurno())) {
            throw new AgendaOcupadaException(a.getDiarista().getNome()
                    + " já tem faxina marcada nesse dia e turno. Escolha outro horário ou outra diarista.");
        }
        String sql = "INSERT INTO agendamento (cliente_id, diarista_id, servico_id, data, turno, valor) "
                + "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = Conexao.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getCliente().getId());
            ps.setInt(2, a.getDiarista().getId());
            ps.setInt(3, a.getServico().getId());
            ps.setString(4, a.getData().toString());
            ps.setString(5, a.getTurno().name());
            ps.setDouble(6, a.getValor());
            // RETURNING id devolve o código gerado pelo banco
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                a.setId(rs.getInt(1));
            }
        }
        return a.getId();
    }

    /** Consulta as faxinas de uma data (todas as diaristas), ordenadas por diarista e turno. */
    public List<Agendamento> listarPorData(LocalDate data) throws SQLException {
        String sql = """
            SELECT a.id, a.data, a.turno, a.valor, a.status, a.pago,
                   c.id AS c_id, c.nome, c.telefone, c.endereco, c.bairro, c.observacao,
                   d.id AS d_id, d.nome AS d_nome, d.telefone AS d_tel,
                   s.id AS s_id, s.descricao, s.valor_base
              FROM agendamento a
              JOIN cliente c  ON c.id = a.cliente_id
              JOIN diarista d ON d.id = a.diarista_id
              JOIN servico s  ON s.id = a.servico_id
             WHERE a.data = ? AND a.status <> 'CANCELADO'
             ORDER BY d.nome, CASE a.turno WHEN 'MANHA' THEN 1 WHEN 'INTEGRAL' THEN 2 ELSE 3 END
            """;
        List<Agendamento> lista = new ArrayList<>();
        try (Connection con = Conexao.getConexao(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, data.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente(rs.getString("nome"), rs.getString("telefone"),
                            rs.getString("endereco"), rs.getString("bairro"), rs.getString("observacao"));
                    c.setId(rs.getInt("c_id"));
                    Diarista d = new Diarista(rs.getInt("d_id"), rs.getString("d_nome"), rs.getString("d_tel"), true);
                    Servico s = new Servico(rs.getInt("s_id"), rs.getString("descricao"), rs.getDouble("valor_base"));
                    Agendamento a = new Agendamento(c, d, s, LocalDate.parse(rs.getString("data")),
                            Turno.valueOf(rs.getString("turno")), rs.getDouble("valor"));
                    a.setId(rs.getInt("id"));
                    a.setStatus(rs.getString("status"));
                    a.setPago(rs.getInt("pago") == 1);
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
