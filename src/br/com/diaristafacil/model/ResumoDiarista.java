package br.com.diaristafacil.model;

/** Linha do relatório mensal: totais de uma diarista no mês. */
public class ResumoDiarista {
    private final String diarista;
    private final int quantidade;
    private final double valorTotal;
    private final double valorRecebido;

    public ResumoDiarista(String diarista, int quantidade, double valorTotal, double valorRecebido) {
        this.diarista = diarista;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.valorRecebido = valorRecebido;
    }

    public String getDiarista() { return diarista; }
    public int getQuantidade() { return quantidade; }
    public double getValorTotal() { return valorTotal; }
    public double getValorRecebido() { return valorRecebido; }

    /** O que ainda falta receber dos clientes. */
    public double getValorPendente() { return valorTotal - valorRecebido; }
}
