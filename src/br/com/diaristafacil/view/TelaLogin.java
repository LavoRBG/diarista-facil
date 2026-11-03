package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.UsuarioDAO;
import br.com.diaristafacil.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TelaLogin extends JFrame {
    final JTextField txtLogin = new JTextField(16);
    final JPasswordField txtSenha = new JPasswordField(16);
    final JButton btnEntrar = Estilo.botao("Entrar");
    final JLabel lblMensagem = new JLabel(" ", SwingConstants.CENTER);
    Usuario usuarioLogado;

    public TelaLogin() {
        super("Diarista Fácil - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = Estilo.titulo("Diarista Fácil");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        painel.add(titulo, g);
        g.gridy = 1;
        painel.add(new JLabel("Agenda de faxinas", SwingConstants.CENTER), g);

        g.gridwidth = 1; g.gridy = 2; g.gridx = 0;
        painel.add(new JLabel("Usuário:"), g);
        g.gridx = 1;
        painel.add(txtLogin, g);
        g.gridy = 3; g.gridx = 0;
        painel.add(new JLabel("Senha:"), g);
        g.gridx = 1;
        painel.add(txtSenha, g);
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        painel.add(btnEntrar, g);
        g.gridy = 5;
        painel.add(lblMensagem, g);

        btnEntrar.addActionListener(e -> entrar());
        txtSenha.addActionListener(e -> entrar());

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    void entrar() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        if (login.isBlank() || senha.isBlank()) {
            Estilo.mensagem(lblMensagem, "Informe usuário e senha.", false);
            return;
        }
        try {
            usuarioLogado = new UsuarioDAO().autenticar(login, senha);
            if (usuarioLogado == null) {
                Estilo.mensagem(lblMensagem, "Usuário ou senha incorretos.", false);
                txtSenha.setText("");
                return;
            }
            new TelaPrincipal(usuarioLogado).setVisible(true);
            dispose();
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao acessar o banco: " + ex.getMessage(), false);
        }
    }
}
