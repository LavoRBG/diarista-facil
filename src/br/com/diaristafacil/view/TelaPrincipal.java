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
    final JButton btnClientes = Estilo.botao("Clientes");
    final JButton btnAgendar = Estilo.botao("Novo agendamento");
    final JButton btnAgenda = Estilo.botao("Agenda do dia");
    final JButton btnSair = new JButton("Sair");

    public TelaPrincipal(Usuario usuario) {
        super("Diarista Fácil - Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        painel.add(Estilo.titulo("Olá, " + usuario.getNome() + "!"), BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 0, 10));
        botoes.setOpaque(false);
        botoes.add(btnClientes);
        botoes.add(btnAgendar);
        botoes.add(btnAgenda);
        botoes.add(btnSair);
        painel.add(botoes, BorderLayout.CENTER);
        painel.add(new JLabel("Perfil: " + usuario.getPerfil().toLowerCase()), BorderLayout.SOUTH);

        // a diarista só consulta a agenda; quem cadastra e agenda é a atendente
        boolean atendente = "ATENDENTE".equals(usuario.getPerfil());
        btnClientes.setEnabled(atendente);
        btnAgendar.setEnabled(atendente);

        btnClientes.addActionListener(e -> new TelaClientes().setVisible(true));
        btnAgendar.addActionListener(e -> new TelaAgendamento().setVisible(true));
        btnAgenda.addActionListener(e -> new TelaAgendaDia().setVisible(true));
        btnSair.addActionListener(e -> System.exit(0));

        add(painel);
        setSize(340, 300);
        setLocationRelativeTo(null);
    }
}
