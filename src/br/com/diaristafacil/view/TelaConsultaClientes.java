package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.ClienteDAO;
import br.com.diaristafacil.model.Cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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

/** Consulta da lista de clientes, com busca por nome ou bairro (RF03). */
public class TelaConsultaClientes extends JFrame {
    final JTextField txtBusca = new JTextField(22);
    final JButton btnPesquisar = Estilo.botao("Pesquisar");
    final JButton btnTodos = new JButton("Mostrar todos");
    final JLabel lblResumo = new JLabel(" ");
    final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Nome", "Telefone", "Endereço", "Bairro", "Observação"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final ClienteDAO dao = new ClienteDAO();

    public TelaConsultaClientes() {
        super("Diarista Fácil - Consultar clientes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.setOpaque(false);
        filtro.add(new JLabel("Nome ou bairro:"));
        filtro.add(txtBusca);
        filtro.add(btnPesquisar);
        filtro.add(btnTodos);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Estilo.titulo("Consultar clientes"), BorderLayout.NORTH);
        topo.add(filtro, BorderLayout.CENTER);

        JTable tabela = new JTable(modelo);
        Estilo.ajustarTabela(tabela);
        int[] larguras = {55, 140, 115, 210, 80, 170};
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

        btnPesquisar.addActionListener(e -> pesquisar());
        txtBusca.addActionListener(e -> pesquisar());
        btnTodos.addActionListener(e -> { txtBusca.setText(""); pesquisar(); });

        setSize(860, 440);
        setLocationRelativeTo(null);
        pesquisar();
    }

    void pesquisar() {
        try {
            List<Cliente> clientes = dao.buscar(txtBusca.getText());
            modelo.setRowCount(0);
            for (Cliente c : clientes) {
                modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(),
                        c.getEndereco(), c.getBairro(), c.getObservacao()});
            }
            if (clientes.isEmpty()) {
                Estilo.mensagem(lblResumo, "Nenhum cliente encontrado para \"" + txtBusca.getText().trim() + "\".", false);
            } else {
                lblResumo.setForeground(Estilo.AZUL);
                lblResumo.setText(clientes.size() + " cliente(s) encontrado(s).");
            }
        } catch (Exception ex) {
            Estilo.mensagem(lblResumo, "Erro ao consultar: " + ex.getMessage(), false);
        }
    }
}
