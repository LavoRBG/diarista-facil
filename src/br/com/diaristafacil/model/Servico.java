package br.com.diaristafacil.model;

/** Tipo de serviço oferecido (faxina completa, passadoria etc.). */
public class Servico {
    private int id;
    private String descricao;
    private double valorBase;

    public Servico(int id, String descricao, double valorBase) {
        this.id = id;
        this.descricao = descricao;
        this.valorBase = valorBase;
    }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public double getValorBase() { return valorBase; }

    @Override
    public String toString() { return descricao; }
}
