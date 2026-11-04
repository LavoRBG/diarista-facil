package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.AgendaOcupadaException;
import br.com.diaristafacil.dao.AgendamentoDAO;
import br.com.diaristafacil.model.Agendamento;
import br.com.diaristafacil.model.Turno;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Agenda do dia (UC04). Para a atendente, a tela também permite cancelar ou remarcar
 * a faxina selecionada (UC05) e registrar o pagamento (UC06).
 */
public class TelaAgendaDia extends JFrame {
    final JTextField txtData = new JTextField(10);
    final JButton btnAnterior = new JButton("<");
    final JButton btnProximo = new JButton(">");
    final JButton btnConsultar = Estilo.botao("Consultar");
    final JLabel lblResumo = new JLabel(" ");
    final JLabel lblMensagem = new JLabel(" ");
    final JButton btnPagamento = Estilo.botao("Registrar pagamento");
    final JButton btnCancelar = new JButton("Cancelar faxina");
    final JTextField txtNovaData = new JTextField(9);
    final JComboBox<Turno> cbNovoTurno = new JComboBox<>(Turno.values());
    final JButton btnRemarcar = new JButton("Remarcar");
    final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Nº", "Diarista", "Turno", "Cliente", "Endereço", "Serviço", "Valor", "Pago"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    final JTable tabela = new JTable(modelo);
    private final List<Agendamento> agendamentos = new ArrayList<>();
    private final AgendamentoDAO dao = new AgendamentoDAO();

    /** Tela só de consulta (perfil diarista). */
    public TelaAgendaDia() {
        this(false);
    }

    /** @param podeAlterar true para a atendente: mostra as ações de cancelar, remarcar e pagamento. */
    public TelaAgendaDia(boolean podeAlterar) {
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

        Estilo.ajustarTabela(tabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] larguras = {35, 65, 65, 125, 200, 135, 75, 40};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        lblResumo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMensagem.setFont(new Font("SansSerif", Font.BOLD, 13));

        JPanel rodape = new JPanel(new GridLayout(0, 1, 0, 4));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        rodape.add(lblResumo);
        if (podeAlterar) {
            JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            acoes.setOpaque(false);
            acoes.add(new JLabel("Faxina selecionada:"));
            acoes.add(btnPagamento);
            acoes.add(btnCancelar);
            acoes.add(new JLabel("   Nova data:"));
            acoes.add(txtNovaData);
            acoes.add(cbNovoTurno);
            acoes.add(btnRemarcar);
            rodape.add(acoes);
            rodape.add(lblMensagem);
        }

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        add(painel);

        txtData.setText(LocalDate.now().format(TelaAgendamento.BR));
        btnConsultar.addActionListener(e -> consultar());
        txtData.addActionListener(e -> consultar());
        btnAnterior.addActionListener(e -> mudarDia(-1));
        btnProximo.addActionListener(e -> mudarDia(1));
        btnPagamento.addActionListener(e -> registrarPagamento());
        btnCancelar.addActionListener(e -> cancelar(true));
        btnRemarcar.addActionListener(e -> remarcar());
        // ao selecionar uma faxina, sugere a data e o turno atuais para a remarcação
        tabela.getSelectionModel().addListSelectionListener(e -> {
            Agendamento a = selecionado();
            if (a != null) {
                txtNovaData.setText(a.getData().format(TelaAgendamento.BR));
                cbNovoTurno.setSelectedItem(a.getTurno());
            }
        });

        setSize(920, podeAlterar ? 480 : 420);
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

    private Agendamento selecionado() {
        int linha = tabela.getSelectedRow();
        return linha < 0 ? null : agendamentos.get(linha);
    }

    void registrarPagamento() {
        Agendamento a = selecionado();
        if (a == null) {
            Estilo.mensagem(lblMensagem, "Selecione uma faxina na lista.", false);
            return;
        }
        if (a.isPago()) {
            Estilo.mensagem(lblMensagem, "A faxina nº " + a.getId() + " já está paga.", false);
            return;
        }
        try {
            dao.registrarPagamento(a);
            consultar();
            Estilo.mensagem(lblMensagem, "Pagamento da faxina nº " + a.getId() + " ("
                    + a.getCliente().getNome() + ") registrado.", true);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao registrar pagamento: " + ex.getMessage(), false);
        }
    }

    void cancelar(boolean pedirConfirmacao) {
        Agendamento a = selecionado();
        if (a == null) {
            Estilo.mensagem(lblMensagem, "Selecione uma faxina na lista.", false);
            return;
        }
        if (pedirConfirmacao && JOptionPane.showConfirmDialog(this,
                "Cancelar a faxina nº " + a.getId() + " de " + a.getCliente().getNome() + "?",
                "Confirmar cancelamento", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.cancelar(a);
            consultar();
            Estilo.mensagem(lblMensagem, "Faxina nº " + a.getId() + " (" + a.getCliente().getNome()
                    + ") cancelada. O horário de " + a.getDiarista().getNome() + " ficou livre.", true);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao cancelar: " + ex.getMessage(), false);
        }
    }

    void remarcar() {
        Agendamento a = selecionado();
        if (a == null) {
            Estilo.mensagem(lblMensagem, "Selecione uma faxina na lista.", false);
            return;
        }
        LocalDate novaData;
        try {
            novaData = LocalDate.parse(txtNovaData.getText().trim(), TelaAgendamento.BR);
        } catch (DateTimeParseException ex) {
            Estilo.mensagem(lblMensagem, "Nova data inválida. Use o formato dd/mm/aaaa.", false);
            return;
        }
        Turno novoTurno = (Turno) cbNovoTurno.getSelectedItem();
        try {
            dao.remarcar(a, novaData, novoTurno);
            consultar();
            Estilo.mensagem(lblMensagem, "Faxina nº " + a.getId() + " remarcada para "
                    + novaData.format(TelaAgendamento.BR) + ", " + novoTurno.name().toLowerCase() + ".", true);
        } catch (AgendaOcupadaException ex) {
            Estilo.mensagem(lblMensagem, ex.getMessage(), false);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao remarcar: " + ex.getMessage(), false);
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
            List<Agendamento> lista = dao.listarPorData(data);
            agendamentos.clear();
            agendamentos.addAll(lista);
            modelo.setRowCount(0);
            lblMensagem.setText(" ");
            double total = 0;
            for (Agendamento a : lista) {
                modelo.addRow(new Object[]{
                        a.getId(),
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
