package br.com.diaristafacil.view;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

/** Cores e fontes usadas em todas as telas, para manter o mesmo visual. */
public final class Estilo {
    public static final Color AZUL = new Color(0x1F4E79);
    public static final Color VERDE = new Color(0x2E7D32);
    public static final Color VERMELHO = new Color(0xC62828);
    public static final Color FUNDO = new Color(0xF4F6F8);
    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 18);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);

    private Estilo() { }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(TITULO);
        l.setForeground(AZUL);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return l;
    }

    public static JButton botao(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    /** Mostra uma mensagem de sucesso (verde) ou erro (vermelho) dentro da própria tela. */
    public static void mensagem(JLabel label, String texto, boolean sucesso) {
        label.setText(texto);
        label.setForeground(sucesso ? VERDE : VERMELHO);
    }

    public static void ajustarTabela(JTable t) {
        t.setRowHeight(24);
        t.setFont(NORMAL);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setFillsViewportHeight(true);
    }
}
