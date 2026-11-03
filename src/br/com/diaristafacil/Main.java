package br.com.diaristafacil;

import br.com.diaristafacil.dao.Conexao;
import br.com.diaristafacil.view.TelaLogin;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Conexao.criarBanco();
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
