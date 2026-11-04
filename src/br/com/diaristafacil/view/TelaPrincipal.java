package br.com.diaristafacil.view;

import br.com.diaristafacil.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Menu inicial com acesso às funções do sistema. */
public class TelaPrincipal extends JFrame {
    final JButton btnClientes = Estilo.botao("Cadastrar cliente");
    final JButton btnConsultaClientes = Estilo.botao("Consultar clientes");
    final JButton btnAgendar = Estilo.botao("Novo agendamento");
    final JButton btnAgenda = Estilo.botao("Agenda do dia");
    final JButton btnRelatorio = Estilo.botao("Relatório mensal");
    final JButton btnSair = new JButton("Sair");

    public TelaPrincipal(Usuario usuario) {
        super("Diarista Fácil - Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        painel.add(Estilo.titulo("Olá, " + usuario.getNome() + "!"), BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(6, 1, 0, 10));
        botoes.setOpaque(false);
        botoes.add(btnClientes);
        botoes.add(btnConsultaClientes);
        botoes.add(btnAgendar);
        botoes.add(btnAgenda);
        botoes.add(btnRelatorio);
        botoes.add(btnSair);
        painel.add(botoes, BorderLayout.CENTER);
        JLabel perfil = new JLabel("Perfil: " + usuario.getPerfil().toLowerCase());
        perfil.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painel.add(perfil, BorderLayout.SOUTH);

        // a diarista só consulta a agenda; cadastros e relatório ficam com a atendente
        boolean atendente = "ATENDENTE".equals(usuario.getPerfil());
        btnClientes.setEnabled(atendente);
        btnConsultaClientes.setEnabled(atendente);
        btnAgendar.setEnabled(atendente);
        btnRelatorio.setEnabled(atendente);

        btnClientes.addActionListener(e -> new TelaClientes().setVisible(true));
        btnConsultaClientes.addActionListener(e -> new TelaConsultaClientes().setVisible(true));
        btnAgendar.addActionListener(e -> new TelaAgendamento().setVisible(true));
        btnAgenda.addActionListener(e -> new TelaAgendaDia().setVisible(true));
        btnRelatorio.addActionListener(e -> new TelaRelatorio().setVisible(true));
        btnSair.addActionListener(e -> System.exit(0));

        add(painel);
        setSize(360, 440);
        setLocationRelativeTo(null);
    }
}
