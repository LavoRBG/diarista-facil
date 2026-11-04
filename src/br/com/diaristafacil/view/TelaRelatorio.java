package br.com.diaristafacil.view;

import br.com.diaristafacil.dao.RelatorioDAO;
import br.com.diaristafacil.model.ResumoDiarista;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Relatório mensal de faxinas e valores por diarista (RF09). */
public class TelaRelatorio extends JFrame {
    static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
            "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    final JComboBox<String> cbMes = new JComboBox<>(MESES);
    final JTextField txtAno = new JTextField(5);
    final JButton btnGerar = Estilo.botao("Gerar relatório");
    final JButton btnExportar = new JButton("Exportar CSV");
    final JLabel lblMensagem = new JLabel(" ");
    final DefaultTableModel modeloResumo = new DefaultTableModel(
            new Object[]{"Diarista", "Faxinas", "Valor total", "Recebido", "A receber"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    final DefaultTableModel modeloFaxinas = new DefaultTableModel(
            new Object[]{"Data", "Diarista", "Turno", "Cliente", "Serviço", "Valor (R$)", "Pago"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final RelatorioDAO dao = new RelatorioDAO();
    private int anoGerado, mesGerado;

    public TelaRelatorio() {
        super("Diarista Fácil - Relatório mensal");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        LocalDate hoje = LocalDate.now();
        cbMes.setSelectedIndex(hoje.getMonthValue() - 1);
        txtAno.setText(String.valueOf(hoje.getYear()));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.setOpaque(false);
        filtro.add(new JLabel("Mês:"));
        filtro.add(cbMes);
        filtro.add(new JLabel("Ano:"));
        filtro.add(txtAno);
        filtro.add(btnGerar);
        filtro.add(btnExportar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Estilo.titulo("Relatório mensal por diarista"), BorderLayout.NORTH);
        topo.add(filtro, BorderLayout.CENTER);

        JTable tResumo = new JTable(modeloResumo);
        Estilo.ajustarTabela(tResumo);
        JTable tFaxinas = new JTable(modeloFaxinas);
        Estilo.ajustarTabela(tFaxinas);
        int[] larguras = {80, 70, 70, 150, 150, 80, 45};
        for (int i = 0; i < larguras.length; i++) {
            tFaxinas.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        JPanel tabelas = new JPanel(new GridLayout(2, 1, 0, 10));
        tabelas.setOpaque(false);
        tabelas.add(bloco("Resumo do mês", new JScrollPane(tResumo)));
        tabelas.add(bloco("Faxinas do mês", new JScrollPane(tFaxinas)));

        lblMensagem.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        painel.add(topo, BorderLayout.NORTH);
        painel.add(tabelas, BorderLayout.CENTER);
        painel.add(lblMensagem, BorderLayout.SOUTH);
        add(painel);

        btnGerar.addActionListener(e -> gerar());
        btnExportar.addActionListener(e -> exportar());
        btnExportar.setEnabled(false);

        setSize(820, 560);
        setLocationRelativeTo(null);
    }

    private JPanel bloco(String titulo, JScrollPane conteudo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(titulo);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        p.add(l, BorderLayout.NORTH);
        p.add(conteudo, BorderLayout.CENTER);
        return p;
    }

    private static String moeda(double v) {
        return String.format("R$ %.2f", v).replace('.', ',');
    }

    void gerar() {
        int ano;
        try {
            ano = Integer.parseInt(txtAno.getText().trim());
            if (ano < 2000 || ano > 2100) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            Estilo.mensagem(lblMensagem, "Ano inválido. Informe o ano com 4 dígitos.", false);
            return;
        }
        int mes = cbMes.getSelectedIndex() + 1;
        try {
            List<ResumoDiarista> resumo = dao.resumoPorDiarista(ano, mes);
            modeloResumo.setRowCount(0);
            modeloFaxinas.setRowCount(0);
            int qtd = 0;
            double total = 0, recebido = 0;
            for (ResumoDiarista r : resumo) {
                modeloResumo.addRow(new Object[]{r.getDiarista(), r.getQuantidade(), moeda(r.getValorTotal()),
                        moeda(r.getValorRecebido()), moeda(r.getValorPendente())});
                qtd += r.getQuantidade();
                total += r.getValorTotal();
                recebido += r.getValorRecebido();
            }
            for (String[] linha : dao.faxinasDoMes(ano, mes)) {
                modeloFaxinas.addRow(linha);
            }
            String periodo = MESES[mes - 1] + "/" + ano;
            if (resumo.isEmpty()) {
                Estilo.mensagem(lblMensagem, "Nenhuma faxina registrada em " + periodo + ".", false);
                btnExportar.setEnabled(false);
                return;
            }
            modeloResumo.addRow(new Object[]{"TOTAL", qtd, moeda(total), moeda(recebido), moeda(total - recebido)});
            anoGerado = ano;
            mesGerado = mes;
            btnExportar.setEnabled(true);
            lblMensagem.setForeground(Estilo.AZUL);
            lblMensagem.setText("Relatório de " + periodo + " gerado: " + qtd + " faxina(s), total " + moeda(total) + ".");
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao gerar relatório: " + ex.getMessage(), false);
        }
    }

    /** Salva o relatório gerado em CSV (separado por ponto e vírgula, abre direto no Excel). */
    void exportar() {
        File pasta = new File("relatorios");
        pasta.mkdirs();
        File arquivo = new File(pasta, String.format("relatorio_%04d_%02d.csv", anoGerado, mesGerado));
        try (PrintWriter out = new PrintWriter(arquivo, StandardCharsets.UTF_8)) {
            out.print('﻿'); // BOM para o Excel reconhecer os acentos
            out.println("Relatório mensal - " + MESES[mesGerado - 1] + "/" + anoGerado);
            out.println("Diarista;Faxinas;Valor total;Recebido;A receber");
            for (int i = 0; i < modeloResumo.getRowCount(); i++) {
                out.println(linhaCsv(modeloResumo, i));
            }
            out.println();
            out.println("Data;Diarista;Turno;Cliente;Serviço;Valor (R$);Pago");
            for (int i = 0; i < modeloFaxinas.getRowCount(); i++) {
                out.println(linhaCsv(modeloFaxinas, i));
            }
            Estilo.mensagem(lblMensagem, "Relatório exportado para " + arquivo.getPath(), true);
        } catch (Exception ex) {
            Estilo.mensagem(lblMensagem, "Erro ao exportar: " + ex.getMessage(), false);
        }
    }

    private String linhaCsv(DefaultTableModel m, int linha) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < m.getColumnCount(); c++) {
            if (c > 0) sb.append(';');
            sb.append(String.valueOf(m.getValueAt(linha, c)).replace("R$ ", ""));
        }
        return sb.toString();
    }
}
