package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.AgendamentoDAO;
import br.com.diaristafacil.model.Agendamento;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Consulta ao banco: faxinas marcadas em uma data, separadas por diarista. */
public class TelaAgendaDia extends JFrame {
    final JTextField txtData = new JTextField(10);
    final JButton btnAnterior = new JButton("<");
    final JButton btnProximo = new JButton(">");
    final JButton btnConsultar = Estilo.botao("Consultar");
    final JLabel lblResumo = new JLabel(" ");
    final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Diarista", "Turno", "Cliente", "Endereço", "Serviço", "Valor", "Pago"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public TelaAgendaDia() {
        super("Diarista Fácil - Agenda do dia");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.setOpaque(false);
        filtro.add(new JLabel("Data:"));
        filtro.add(btnAnterior);
        filtro.add(txtData);
        filtro.add(btnProximo);
        filtro.add(btnConsultar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Estilo.titulo("Agenda do dia"), BorderLayout.NORTH);
        topo.add(filtro, BorderLayout.CENTER);

        JTable tabela = new JTable(modelo);
        Estilo.ajustarTabela(tabela);
        int[] larguras = {70, 70, 130, 200, 140, 75, 45};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        lblResumo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblResumo.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(lblResumo, BorderLayout.SOUTH);
        add(painel);

        txtData.setText(LocalDate.now().format(TelaAgendamento.BR));
        btnConsultar.addActionListener(e -> consultar());
        txtData.addActionListener(e -> consultar());
        btnAnterior.addActionListener(e -> mudarDia(-1));
        btnProximo.addActionListener(e -> mudarDia(1));

        setSize(860, 420);
        setLocationRelativeTo(null);
        consultar();
    }

    private void mudarDia(int dias) {
        try {
            LocalDate d = LocalDate.parse(txtData.getText().trim(), TelaAgendamento.BR).plusDays(dias);
            txtData.setText(d.format(TelaAgendamento.BR));
            consultar();
        } catch (DateTimeParseException ex) {
            Estilo.mensagem(lblResumo, "Data inválida. Use o formato dd/mm/aaaa.", false);
        }
    }

    void consultar() {
        LocalDate data;
        try {
            data = LocalDate.parse(txtData.getText().trim(), TelaAgendamento.BR);
        } catch (DateTimeParseException ex) {
            Estilo.mensagem(lblResumo, "Data inválida. Use o formato dd/mm/aaaa.", false);
            return;
        }
        try {
            List<Agendamento> lista = new AgendamentoDAO().listarPorData(data);
            modelo.setRowCount(0);
            double total = 0;
            for (Agendamento a : lista) {
                modelo.addRow(new Object[]{
                        a.getDiarista().getNome(),
                        a.getTurno().name().charAt(0) + a.getTurno().name().substring(1).toLowerCase()
                                .replace("anha", "anhã"),
                        a.getCliente().getNome(),
                        a.getCliente().getEndereco() + " - " + a.getCliente().getBairro(),
                        a.getServico().getDescricao(),
                        String.format("R$ %.2f", a.getValor()).replace('.', ','),
                        a.isPago() ? "Sim" : "Não"});
                total += a.getValor();
            }
            if (lista.isEmpty()) {
                Estilo.mensagem(lblResumo, "Nenhuma faxina marcada para " + data.format(TelaAgendamento.BR) + ".", false);
            } else {
                lblResumo.setForeground(Estilo.AZUL);
                lblResumo.setText(lista.size() + " faxina(s) em " + data.format(TelaAgendamento.BR)
                        + " | Total previsto: " + String.format("R$ %.2f", total).replace('.', ','));
            }
        } catch (Exception ex) {
            Estilo.mensagem(lblResumo, "Erro ao consultar: " + ex.getMessage(), false);
        }
    }
}
