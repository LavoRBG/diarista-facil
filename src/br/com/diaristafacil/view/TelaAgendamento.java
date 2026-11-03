package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.AgendaOcupadaException;
import br.com.diaristafacil.dao.AgendamentoDAO;
import br.com.diaristafacil.dao.CadastroBasicoDAO;
import br.com.diaristafacil.dao.ClienteDAO;
import br.com.diaristafacil.model.Agendamento;
import br.com.diaristafacil.model.Cliente;
import br.com.diaristafacil.model.Diarista;
import br.com.diaristafacil.model.Servico;
import br.com.diaristafacil.model.Turno;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Funcionalidade 2: agendamento de faxina (lê clientes do banco e grava o horário). */
public class TelaAgendamento extends JFrame {
    // STRICT faz o Java recusar datas inexistentes (ex.: 31/02) em vez de ajustar para o fim do mês
    static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    final JComboBox<Cliente> cbCliente = new JComboBox<>();
    final JComboBox<Diarista> cbDiarista = new JComboBox<>();
    final JComboBox<Servico> cbServico = new JComboBox<>();
    final JTextField txtData = new JTextField(10);
    final JComboBox<Turno> cbTurno = new JComboBox<>(Turno.values());
    final JTextField txtValor = new JTextField(8);
    final JButton btnAgendar = Estilo.botao("Agendar");
    final JLabel lblMensagem = new JLabel(" ");
    private final AgendamentoDAO dao = new AgendamentoDAO();

    public TelaAgendamento() {
        super("Diarista Fácil - Novo agendamento");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        adicionar(form, g, 0, "Cliente *", cbCliente);
        adicionar(form, g, 1, "Diarista *", cbDiarista);
        adicionar(form, g, 2, "Serviço *", cbServico);
        adicionar(form, g, 3, "Data (dd/mm/aaaa) *", txtData);
        adicionar(form, g, 4, "Turno *", cbTurno);
        adicionar(form, g, 5, "Valor (R$) *", txtValor);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.add(btnAgendar);
        g.gridx = 1; g.gridy = 6;
        form.add(acoes, g);
        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        form.add(lblMensagem, g);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painel.add(Estilo.titulo("Novo agendamento de faxina"), BorderLayout.NORTH);
        painel.add(form, BorderLayout.CENTER);
        add(painel);

        // ao trocar o serviço, sugere o valor base cadastrado
        cbServico.addActionListener(e -> {
            Servico s = (Servico) cbServico.getSelectedItem();
            if (s != null) txtValor.setText(String.format("%.2f", s.getValorBase()).replace('.', ','));
        });
        btnAgendar.addActionListener(e -> agendar());

        carregarListas();
        setSize(620, 420);
        setLocationRelativeTo(null);
    }

    private void adicionar(JPanel p, GridBagConstraints g, int linha, String rotulo, JComponent campo) {
        g.gridx = 0; g.gridy = linha; g.weightx = 0;
        p.add(new JLabel(rotulo), g);
        g.gridx = 1; g.weightx = 1;
        p.add(campo, g);
    }

    /** Busca no banco os clientes, diaristas e serviços para preencher as listas. */
    void carregarListas() {
        try {
            cbCliente.removeAllItems();
            for (Cliente c : new ClienteDAO().listar("")) cbCliente.addItem(c);
            CadastroBasicoDAO apoio = new CadastroBasicoDAO();
            cbDiarista.removeAllItems();
            for (Diarista d : apoio.listarDiaristasAtivas()) cbDiarista.addItem(d);
            cbServico.removeAllItems();
            for (Servico s : apoio.listarServicos()) cbServico.addItem(s);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao carregar dados: " + ex.getMessage(), false);
        }
    }

    void agendar() {
        LocalDate data;
        double valor;
        try {
            data = LocalDate.parse(txtData.getText().trim(), BR);
        } catch (DateTimeParseException ex) {
            Estilo.mensagem(lblMensagem, "Data inválida. Use o formato dd/mm/aaaa.", false);
            return;
        }
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(".", "").replace(',', '.'));
        } catch (NumberFormatException ex) {
            valor = 0;
        }
        Agendamento a = new Agendamento((Cliente) cbCliente.getSelectedItem(),
                (Diarista) cbDiarista.getSelectedItem(), (Servico) cbServico.getSelectedItem(),
                data, (Turno) cbTurno.getSelectedItem(), valor);
        String erro = a.validar();
        if (erro != null) {
            Estilo.mensagem(lblMensagem, erro, false);
            return;
        }
        try {
            int id = dao.agendar(a);
            Estilo.mensagem(lblMensagem, "Faxina agendada (nº " + id + "): " + a.getDiarista().getNome()
                    + " em " + data.format(BR) + ", " + a.getTurno().name().toLowerCase() + ".", true);
        } catch (AgendaOcupadaException ex) {
            Estilo.mensagem(lblMensagem, ex.getMessage(), false);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao agendar: " + ex.getMessage(), false);
        }
    }
}
