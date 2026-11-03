package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.ClienteDAO;
import br.com.diaristafacil.model.Cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

/** Funcionalidade 1: cadastro de clientes (grava no banco e lista os cadastrados). */
public class TelaClientes extends JFrame {
    final JTextField txtNome = new JTextField(28);
    final JTextField txtTelefone = new JTextField(14);
    final JTextField txtEndereco = new JTextField(28);
    final JTextField txtBairro = new JTextField(14);
    final JTextField txtObservacao = new JTextField(28);
    final JTextField txtBusca = new JTextField(18);
    final JButton btnSalvar = Estilo.botao("Salvar");
    final JButton btnLimpar = new JButton("Limpar");
    final JButton btnBuscar = new JButton("Buscar");
    final JLabel lblMensagem = new JLabel(" ");
    final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Nome", "Telefone", "Bairro", "Observação"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ClienteDAO dao = new ClienteDAO();

    public TelaClientes() {
        super("Diarista Fácil - Cadastro de clientes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        adicionar(form, g, 0, "Nome *", txtNome);
        adicionar(form, g, 1, "Telefone *", txtTelefone);
        adicionar(form, g, 2, "Endereço *", txtEndereco);
        adicionar(form, g, 3, "Bairro", txtBairro);
        adicionar(form, g, 4, "Observação", txtObservacao);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        acoes.setOpaque(false);
        acoes.add(btnSalvar);
        acoes.add(btnLimpar);
        acoes.add(lblMensagem);
        g.gridx = 1; g.gridy = 5;
        form.add(acoes, g);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Estilo.titulo("Cadastro de clientes"), BorderLayout.NORTH);
        topo.add(form, BorderLayout.CENTER);

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busca.setOpaque(false);
        busca.add(new JLabel("Clientes cadastrados - buscar por nome:"));
        busca.add(txtBusca);
        busca.add(btnBuscar);

        JTable tabela = new JTable(modelo);
        Estilo.ajustarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        JPanel lista = new JPanel(new BorderLayout());
        lista.setOpaque(false);
        lista.add(busca, BorderLayout.NORTH);
        lista.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(lista, BorderLayout.CENTER);
        add(painel);

        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limpar());
        btnBuscar.addActionListener(e -> carregarLista());
        txtBusca.addActionListener(e -> carregarLista());

        setSize(760, 560);
        setLocationRelativeTo(null);
        carregarLista();
    }

    private void adicionar(JPanel p, GridBagConstraints g, int linha, String rotulo, JTextField campo) {
        g.gridx = 0; g.gridy = linha; g.weightx = 0;
        p.add(new JLabel(rotulo), g);
        g.gridx = 1; g.weightx = 1;
        p.add(campo, g);
    }

    void salvar() {
        Cliente c = new Cliente(txtNome.getText(), txtTelefone.getText(), txtEndereco.getText(),
                txtBairro.getText(), txtObservacao.getText());
        String erro = c.validar();
        if (erro != null) {
            Estilo.mensagem(lblMensagem, erro, false);
            return;
        }
        try {
            int id = dao.inserir(c);
            Estilo.mensagem(lblMensagem, "Cliente salvo com sucesso (código " + id + ").", true);
            limparCampos();
            txtBusca.setText("");
            carregarLista();
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao salvar: " + ex.getMessage(), false);
        }
    }

    void carregarLista() {
        try {
            List<Cliente> clientes = dao.listar(txtBusca.getText());
            modelo.setRowCount(0);
            for (Cliente c : clientes) {
                modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getBairro(), c.getObservacao()});
            }
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao consultar: " + ex.getMessage(), false);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtEndereco.setText("");
        txtBairro.setText("");
        txtObservacao.setText("");
        txtNome.requestFocus();
    }

    void limpar() {
        limparCampos();
        lblMensagem.setText(" ");
    }
}
